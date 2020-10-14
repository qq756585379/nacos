
package com.alibaba.nacos.common.http.client;

import com.alibaba.nacos.common.constant.ResponseHandlerType;
import com.alibaba.nacos.common.http.client.handler.BeanResponseHandler;
import com.alibaba.nacos.common.http.client.handler.ResponseHandler;
import com.alibaba.nacos.common.http.client.handler.RestResultResponseHandler;
import com.alibaba.nacos.common.http.client.handler.StringResponseHandler;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JavaType;
import org.slf4j.Logger;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public abstract class AbstractNacosRestTemplate {

    private final Map<String, ResponseHandler> responseHandlerMap = new HashMap<String, ResponseHandler>();

    protected final Logger logger;

    public AbstractNacosRestTemplate(Logger logger) {
        this.logger = logger;
        initDefaultResponseHandler();
    }

    private void initDefaultResponseHandler() {
        // init response handler
        responseHandlerMap.put(ResponseHandlerType.STRING_TYPE, new StringResponseHandler());
        responseHandlerMap.put(ResponseHandlerType.RESTRESULT_TYPE, new RestResultResponseHandler());
        responseHandlerMap.put(ResponseHandlerType.DEFAULT_BEAN_TYPE, new BeanResponseHandler());
    }

    /**
     * register customization Response Handler.
     *
     * @param responseHandler {@link ResponseHandler}
     */
    public void registerResponseHandler(String responseHandlerType, ResponseHandler responseHandler) {
        responseHandlerMap.put(responseHandlerType, responseHandler);
    }

    /**
     * Select a response handler by responseType.
     *
     * @param responseType responseType
     * @return ResponseHandler
     */
    protected ResponseHandler selectResponseHandler(Type responseType) {
        ResponseHandler responseHandler = null;
        if (responseType == null) {
            responseHandler = responseHandlerMap.get(ResponseHandlerType.STRING_TYPE);
        }
        if (responseHandler == null) {
            JavaType javaType = JacksonUtils.constructJavaType(responseType);
            String name = javaType.getRawClass().getName();
            responseHandler = responseHandlerMap.get(name);
        }
        // When the corresponding type of response handler cannot be obtained,
        // the default bean response handler is used
        if (responseHandler == null) {
            responseHandler = responseHandlerMap.get(ResponseHandlerType.DEFAULT_BEAN_TYPE);
        }
        responseHandler.setResponseType(responseType);
        return responseHandler;
    }
}
