
package com.alibaba.nacos.common.http.client.request;

import com.alibaba.nacos.common.http.client.response.HttpClientResponse;
import com.alibaba.nacos.common.model.RequestHttpEntity;

import java.io.Closeable;
import java.net.URI;

public interface HttpClientRequest extends Closeable {

    HttpClientResponse execute(URI uri, String httpMethod, RequestHttpEntity requestHttpEntity) throws Exception;
}
