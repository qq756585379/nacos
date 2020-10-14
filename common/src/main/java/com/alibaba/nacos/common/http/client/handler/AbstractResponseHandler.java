
package com.alibaba.nacos.common.http.client.handler;

import com.alibaba.nacos.common.http.HttpRestResult;
import com.alibaba.nacos.common.http.client.response.HttpClientResponse;
import com.alibaba.nacos.common.http.param.Header;
import com.alibaba.nacos.common.utils.IoUtils;
import org.apache.http.HttpStatus;

import java.lang.reflect.Type;

public abstract class AbstractResponseHandler<T> implements ResponseHandler<T> {

    private Type responseType;

    @Override
    public final void setResponseType(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public final HttpRestResult<T> handle(HttpClientResponse response) throws Exception {
        if (HttpStatus.SC_OK != response.getStatusCode()) {
            return handleError(response);
        }
        return convertResult(response, this.responseType);
    }

    private HttpRestResult<T> handleError(HttpClientResponse response) throws Exception {
        Header headers = response.getHeaders();
        String message = IoUtils.toString(response.getBody(), headers.getCharset());
        return new HttpRestResult<T>(headers, response.getStatusCode(), null, message);
    }

    public abstract HttpRestResult<T> convertResult(HttpClientResponse response, Type responseType) throws Exception;
}
