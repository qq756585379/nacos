
package com.alibaba.nacos.common.http.client.request;

import com.alibaba.nacos.common.http.Callback;
import com.alibaba.nacos.common.http.client.handler.ResponseHandler;
import com.alibaba.nacos.common.model.RequestHttpEntity;

import java.io.Closeable;
import java.net.URI;

public interface AsyncHttpClientRequest extends Closeable {

    <T> void execute(URI uri, String httpMethod, RequestHttpEntity requestHttpEntity, final ResponseHandler<T> responseHandler, final Callback<T> callback) throws Exception;
}
