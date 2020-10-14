
package com.alibaba.nacos.config.server.auth;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.nacos.auth.model.Resource;
import com.alibaba.nacos.auth.parser.ResourceParser;
import org.apache.commons.lang3.StringUtils;

public class ConfigResourceParser implements ResourceParser {

    private static final String AUTH_CONFIG_PREFIX = "config/";

    @Override
    public String parseName(Object request) {
        HttpServletRequest req = (HttpServletRequest) request;
        String namespaceId = req.getParameter("tenant");
        String groupName = req.getParameter("group");
        String dataId = req.getParameter("dataId");

        StringBuilder sb = new StringBuilder();

        if (StringUtils.isNotBlank(namespaceId)) {
            sb.append(namespaceId);
        }

        sb.append(":");

        if (StringUtils.isBlank(dataId)) {
            sb.append("*").append(":").append(AUTH_CONFIG_PREFIX).append("*");
        } else {
            sb.append(groupName).append(":").append(AUTH_CONFIG_PREFIX).append(dataId);
        }

        return sb.toString();
    }
}
