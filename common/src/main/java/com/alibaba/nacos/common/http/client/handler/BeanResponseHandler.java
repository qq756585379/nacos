
package com.alibaba.nacos.common.http.client.handler;

import com.alibaba.nacos.common.http.HttpRestResult;
import com.alibaba.nacos.common.http.client.response.HttpClientResponse;
import com.alibaba.nacos.common.http.param.Header;
import com.alibaba.nacos.common.utils.JacksonUtils;

import java.io.InputStream;
import java.lang.reflect.Type;

public class BeanResponseHandler<T> extends AbstractResponseHandler<T> {

    @Override
    public HttpRestResult<T> convertResult(HttpClientResponse response, Type responseType) throws Exception {
        final Header headers = response.getHeaders();
        InputStream body = response.getBody();
        T extractBody = JacksonUtils.toObj(body, responseType);
        return new HttpRestResult<T>(headers, response.getStatusCode(), extractBody, null);
    }
}
