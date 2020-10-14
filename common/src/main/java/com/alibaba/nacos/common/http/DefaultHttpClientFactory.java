
package com.alibaba.nacos.common.http;

import org.slf4j.Logger;

public class DefaultHttpClientFactory extends AbstractHttpClientFactory {

    private static final int TIMEOUT = Integer.getInteger("nacos.http.timeout", 5000);

    private final Logger logger;

    public DefaultHttpClientFactory(Logger logger) {
        this.logger = logger;
    }

    @Override
    protected HttpClientConfig buildHttpClientConfig() {
        return HttpClientConfig.builder().setConTimeOutMillis(TIMEOUT).setReadTimeOutMillis(TIMEOUT >> 1).build();
    }

    @Override
    protected Logger assignLogger() {
        return logger;
    }
}
