
package com.alibaba.nacos.core.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

public class OverrideParameterRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String[]> params = new HashMap<>();

    public OverrideParameterRequestWrapper(HttpServletRequest request) {
        super(request);
        this.params.putAll(request.getParameterMap());
    }

    public static OverrideParameterRequestWrapper buildRequest(HttpServletRequest request) {
        return new OverrideParameterRequestWrapper(request);
    }

    public static OverrideParameterRequestWrapper buildRequest(HttpServletRequest request, String name, String value) {
        OverrideParameterRequestWrapper requestWrapper = new OverrideParameterRequestWrapper(request);
        requestWrapper.addParameter(name, value);
        return requestWrapper;
    }

    public static OverrideParameterRequestWrapper buildRequest(HttpServletRequest request, Map<String, String[]> appendParameters) {
        OverrideParameterRequestWrapper requestWrapper = new OverrideParameterRequestWrapper(request);
        requestWrapper.params.putAll(appendParameters);
        return requestWrapper;
    }

    @Override
    public String getParameter(String name) {
        String[] values = params.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return params;
    }

    @Override
    public String[] getParameterValues(String name) {
        return params.get(name);
    }

    public void addParameter(String name, String value) {
        if (value != null) {
            params.put(name, new String[] {value});
        }
    }
}
