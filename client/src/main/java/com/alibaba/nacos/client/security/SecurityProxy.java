
package com.alibaba.nacos.client.security;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.common.http.HttpRestResult;
import com.alibaba.nacos.common.http.client.NacosRestTemplate;
import com.alibaba.nacos.common.http.param.Header;
import com.alibaba.nacos.common.http.param.Query;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class SecurityProxy {

    private static final Logger SECURITY_LOGGER = LoggerFactory.getLogger(SecurityProxy.class);

    private static final String LOGIN_URL = "/v1/auth/users/login";

    private final NacosRestTemplate nacosRestTemplate;

    private String contextPath;

    private final String username;

    private final String password;

    private String accessToken;

    private long tokenTtl;

    private long lastRefreshTime;

    private long tokenRefreshWindow;

    public SecurityProxy(Properties properties, NacosRestTemplate nacosRestTemplate) {
        username = properties.getProperty(PropertyKeyConst.USERNAME, StringUtils.EMPTY);
        password = properties.getProperty(PropertyKeyConst.PASSWORD, StringUtils.EMPTY);
        contextPath = properties.getProperty(PropertyKeyConst.CONTEXT_PATH, "/nacos");
        contextPath = contextPath.startsWith("/") ? contextPath : "/" + contextPath;
        this.nacosRestTemplate = nacosRestTemplate;
    }

    public boolean login(List<String> servers) {
        try {
            if ((System.currentTimeMillis() - lastRefreshTime) < TimeUnit.SECONDS.toMillis(tokenTtl - tokenRefreshWindow)) {
                return true;
            }
            for (String server : servers) {
                if (login(server)) {
                    lastRefreshTime = System.currentTimeMillis();
                    return true;
                }
            }
        } catch (Throwable ignore) {
        }
        return false;
    }

    public boolean login(String server) throws UnsupportedEncodingException {
        if (StringUtils.isNotBlank(username)) {
            Map<String, String> params = new HashMap<String, String>(2);
            params.put("username", username);

            Map<String, String> bodyMap = new HashMap<String, String>(2);
            bodyMap.put("password", URLEncoder.encode(password, "utf-8"));

            String url = "http://" + server + contextPath + LOGIN_URL;
            if (server.contains(Constants.HTTP_PREFIX)) {
                url = server + contextPath + LOGIN_URL;
            }

            try {
                HttpRestResult<String> restResult = nacosRestTemplate.postForm(url, Header.EMPTY, Query.newInstance().initParams(params), bodyMap, String.class);
                if (!restResult.ok()) {
                    SECURITY_LOGGER.error("login failed: {}", JacksonUtils.toJson(restResult));
                    return false;
                }
                JsonNode obj = JacksonUtils.toObj(restResult.getData());
                if (obj.has(Constants.ACCESS_TOKEN)) {
                    accessToken = obj.get(Constants.ACCESS_TOKEN).asText();
                    tokenTtl = obj.get(Constants.TOKEN_TTL).asInt();
                    tokenRefreshWindow = tokenTtl / 10;
                }
            } catch (Exception e) {
                SECURITY_LOGGER.error("[SecurityProxy] login http request failed" + " url: {}, params: {}, bodyMap: {}, errorMsg: {}", url, params, bodyMap, e.getMessage());
                return false;
            }
        }
        return true;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
