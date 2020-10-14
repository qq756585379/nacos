
package com.alibaba.nacos.common.http.client.request;

import com.alibaba.nacos.common.constant.HttpHeaderConsts;
import com.alibaba.nacos.common.http.BaseHttpMethod;
import com.alibaba.nacos.common.http.HttpClientConfig;
import com.alibaba.nacos.common.http.HttpUtils;
import com.alibaba.nacos.common.http.client.response.DefaultClientHttpResponse;
import com.alibaba.nacos.common.http.client.response.HttpClientResponse;
import com.alibaba.nacos.common.http.param.Header;
import com.alibaba.nacos.common.http.param.MediaType;
import com.alibaba.nacos.common.model.RequestHttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@SuppressWarnings({"unchecked", "resource"})
public class DefaultHttpClientRequest implements HttpClientRequest {

    private final CloseableHttpClient client;

    public DefaultHttpClientRequest(CloseableHttpClient client) {
        this.client = client;
    }

    @Override
    public HttpClientResponse execute(URI uri, String httpMethod, RequestHttpEntity requestHttpEntity) throws Exception {
        HttpRequestBase request = build(uri, httpMethod, requestHttpEntity);
        CloseableHttpResponse response = client.execute(request);
        return new DefaultClientHttpResponse(response);
    }

    static HttpRequestBase build(URI uri, String method, RequestHttpEntity requestHttpEntity) throws Exception {
        final Header headers = requestHttpEntity.getHeaders();
        final BaseHttpMethod httpMethod = BaseHttpMethod.sourceOf(method);
        final HttpRequestBase httpRequestBase = httpMethod.init(uri.toString());
        HttpUtils.initRequestHeader(httpRequestBase, headers);
        if (MediaType.APPLICATION_FORM_URLENCODED.equals(headers.getValue(HttpHeaderConsts.CONTENT_TYPE)) && requestHttpEntity.getBody() instanceof Map) {
            HttpUtils.initRequestFromEntity(httpRequestBase, (Map<String, String>) requestHttpEntity.getBody(), headers.getCharset());
        } else {
            HttpUtils.initRequestEntity(httpRequestBase, requestHttpEntity.getBody(), headers);
        }
        replaceDefaultConfig(httpRequestBase, requestHttpEntity.getHttpClientConfig());
        return httpRequestBase;
    }

    private static void replaceDefaultConfig(HttpRequestBase requestBase, HttpClientConfig httpClientConfig) {
        if (httpClientConfig == null) {
            return;
        }
        requestBase.setConfig(RequestConfig.custom()
                .setConnectTimeout(httpClientConfig.getConTimeOutMillis())
                .setSocketTimeout(httpClientConfig.getReadTimeOutMillis()).build());
    }

    @Override
    public void close() throws IOException {
        client.close();
    }
}
