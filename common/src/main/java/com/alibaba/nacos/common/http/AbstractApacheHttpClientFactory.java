
package com.alibaba.nacos.common.http;

import com.alibaba.nacos.common.http.client.NacosRestTemplate;
import com.alibaba.nacos.common.http.client.request.DefaultHttpClientRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;

public abstract class AbstractApacheHttpClientFactory extends AbstractHttpClientFactory {

    @Override
    public final NacosRestTemplate createNacosRestTemplate() {
        final HttpClientConfig originalRequestConfig = buildHttpClientConfig();
        final RequestConfig requestConfig = getRequestConfig();
        return new NacosRestTemplate(assignLogger(), new DefaultHttpClientRequest(
                HttpClients.custom().setDefaultRequestConfig(requestConfig)
                        .setUserAgent(originalRequestConfig.getUserAgent())
                        .setMaxConnTotal(originalRequestConfig.getMaxConnTotal())
                        .setMaxConnPerRoute(originalRequestConfig.getMaxConnPerRoute())
                        .setConnectionTimeToLive(originalRequestConfig.getConnTimeToLive(),
                                originalRequestConfig.getConnTimeToLiveTimeUnit()).build()));
    }
}
