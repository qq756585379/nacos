
package com.alibaba.nacos.client.config.http;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.common.http.HttpRestResult;
import com.alibaba.nacos.common.lifecycle.Closeable;

import java.util.Map;

public interface HttpAgent extends Closeable {

    void start() throws NacosException;

    HttpRestResult<String> httpGet(String path, Map<String, String> headers, Map<String, String> paramValues, String encoding, long readTimeoutMs) throws Exception;

    HttpRestResult<String> httpPost(String path, Map<String, String> headers, Map<String, String> paramValues, String encoding, long readTimeoutMs) throws Exception;

    HttpRestResult<String> httpDelete(String path, Map<String, String> headers, Map<String, String> paramValues, String encoding, long readTimeoutMs) throws Exception;

    String getName();

    String getNamespace();

    String getTenant();

    String getEncode();
}
