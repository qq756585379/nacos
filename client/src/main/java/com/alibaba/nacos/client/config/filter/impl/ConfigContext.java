
package com.alibaba.nacos.client.config.filter.impl;

import com.alibaba.nacos.api.config.filter.IConfigContext;

import java.util.HashMap;
import java.util.Map;

public class ConfigContext implements IConfigContext {

    private final Map<String, Object> param = new HashMap<String, Object>();

    @Override
    public Object getParameter(String key) {
        return param.get(key);
    }

    @Override
    public void setParameter(String key, Object value) {
        param.put(key, value);
    }
}
