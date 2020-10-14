
package com.alibaba.nacos.common.http.client.handler;

import com.alibaba.nacos.common.http.HttpRestResult;
import com.alibaba.nacos.common.http.client.response.HttpClientResponse;

import java.lang.reflect.Type;

public interface ResponseHandler<T> {

    /**
     * set response type.
     *
     * @param responseType responseType
     */
    void setResponseType(Type responseType);

    /**
     * handle response convert to HttpRestResult.
     *
     * @param response http response
     * @return HttpRestResult {@link HttpRestResult}
     * @throws Exception ex
     */
    HttpRestResult<T> handle(HttpClientResponse response) throws Exception;

}
