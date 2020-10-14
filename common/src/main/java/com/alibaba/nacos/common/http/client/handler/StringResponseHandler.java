
package com.alibaba.nacos.common.http.client.handler;

import com.alibaba.nacos.common.http.HttpRestResult;
import com.alibaba.nacos.common.http.client.response.HttpClientResponse;
import com.alibaba.nacos.common.http.param.Header;
import com.alibaba.nacos.common.utils.IoUtils;

import java.lang.reflect.Type;

public class StringResponseHandler extends AbstractResponseHandler<String> {

    @Override
    public HttpRestResult<String> convertResult(HttpClientResponse response, Type responseType) throws Exception {
        final Header headers = response.getHeaders();
        String extractBody = IoUtils.toString(response.getBody(), headers.getCharset());
        return new HttpRestResult<String>(headers, response.getStatusCode(), extractBody, null);
    }
}
