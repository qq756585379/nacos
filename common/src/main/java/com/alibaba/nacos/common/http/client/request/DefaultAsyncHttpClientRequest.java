
package com.alibaba.nacos.common.http.client.request;

import com.alibaba.nacos.common.http.Callback;
import com.alibaba.nacos.common.http.HttpRestResult;
import com.alibaba.nacos.common.http.client.handler.ResponseHandler;
import com.alibaba.nacos.common.http.client.response.DefaultClientHttpResponse;
import com.alibaba.nacos.common.model.RequestHttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.io.IOException;
import java.net.URI;

public class DefaultAsyncHttpClientRequest implements AsyncHttpClientRequest {

    private final CloseableHttpAsyncClient asyncClient;

    public DefaultAsyncHttpClientRequest(CloseableHttpAsyncClient asyncClient) {
        this.asyncClient = asyncClient;
        if (!this.asyncClient.isRunning()) {
            this.asyncClient.start();
        }
    }

    @Override
    public <T> void execute(URI uri, String httpMethod, RequestHttpEntity requestHttpEntity, final ResponseHandler<T> responseHandler, final Callback<T> callback) throws Exception {
        HttpRequestBase httpRequestBase = DefaultHttpClientRequest.build(uri, httpMethod, requestHttpEntity);
        asyncClient.execute(httpRequestBase, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse result) {
                DefaultClientHttpResponse response = new DefaultClientHttpResponse(result);
                try {
                    HttpRestResult<T> httpRestResult = responseHandler.handle(response);
                    callback.onReceive(httpRestResult);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }

            @Override
            public void failed(Exception ex) {
                callback.onError(ex);
            }

            @Override
            public void cancelled() {
                callback.onCancel();
            }
        });
    }

    @Override
    public void close() throws IOException {
        this.asyncClient.close();
    }
}
