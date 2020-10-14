
package com.alibaba.nacos.common.http;

import com.alibaba.nacos.common.http.client.NacosAsyncRestTemplate;
import com.alibaba.nacos.common.http.client.NacosRestTemplate;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public final class HttpClientBeanHolder {

    private static final Map<String, NacosRestTemplate> SINGLETON_REST = new HashMap<String, NacosRestTemplate>(10);

    private static final Map<String, NacosAsyncRestTemplate> SINGLETON_ASYNC_REST = new HashMap<String, NacosAsyncRestTemplate>(10);

    public static NacosRestTemplate getNacosRestTemplate(Logger logger) {
        return getNacosRestTemplate(new DefaultHttpClientFactory(logger));
    }

    public static NacosRestTemplate getNacosRestTemplate(HttpClientFactory httpClientFactory) {
        if (httpClientFactory == null) {
            throw new NullPointerException("httpClientFactory is null");
        }
        String factoryName = httpClientFactory.getClass().getName();
        NacosRestTemplate nacosRestTemplate = SINGLETON_REST.get(factoryName);
        if (nacosRestTemplate == null) {
            synchronized (SINGLETON_REST) {
                nacosRestTemplate = SINGLETON_REST.get(factoryName);
                if (nacosRestTemplate != null) {
                    return nacosRestTemplate;
                }
                nacosRestTemplate = httpClientFactory.createNacosRestTemplate();
                SINGLETON_REST.put(factoryName, nacosRestTemplate);
            }
        }
        return nacosRestTemplate;
    }

    public static NacosAsyncRestTemplate getNacosAsyncRestTemplate(Logger logger) {
        return getNacosAsyncRestTemplate(new DefaultHttpClientFactory(logger));
    }

    public static NacosAsyncRestTemplate getNacosAsyncRestTemplate(HttpClientFactory httpClientFactory) {
        if (httpClientFactory == null) {
            throw new NullPointerException("httpClientFactory is null");
        }
        String factoryName = httpClientFactory.getClass().getName();
        NacosAsyncRestTemplate nacosAsyncRestTemplate = SINGLETON_ASYNC_REST.get(factoryName);
        if (nacosAsyncRestTemplate == null) {
            synchronized (SINGLETON_ASYNC_REST) {
                nacosAsyncRestTemplate = SINGLETON_ASYNC_REST.get(factoryName);
                if (nacosAsyncRestTemplate != null) {
                    return nacosAsyncRestTemplate;
                }
                nacosAsyncRestTemplate = httpClientFactory.createNacosAsyncRestTemplate();
                SINGLETON_ASYNC_REST.put(factoryName, nacosAsyncRestTemplate);
            }
        }
        return nacosAsyncRestTemplate;
    }

    public static void shutdown(String className) throws Exception {
        shutdownNacostSyncRest(className);
        shutdownNacosAsyncRest(className);
    }

    public static void shutdownNacostSyncRest(String className) throws Exception {
        final NacosRestTemplate nacosRestTemplate = SINGLETON_REST.get(className);
        if (nacosRestTemplate != null) {
            nacosRestTemplate.close();
            SINGLETON_REST.remove(className);
        }
    }

    public static void shutdownNacosAsyncRest(String className) throws Exception {
        final NacosAsyncRestTemplate nacosAsyncRestTemplate = SINGLETON_ASYNC_REST.get(className);
        if (nacosAsyncRestTemplate != null) {
            nacosAsyncRestTemplate.close();
            SINGLETON_ASYNC_REST.remove(className);
        }
    }
}
