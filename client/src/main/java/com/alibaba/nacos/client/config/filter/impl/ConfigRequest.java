
package com.alibaba.nacos.client.config.filter.impl;

import com.alibaba.nacos.api.config.filter.IConfigContext;
import com.alibaba.nacos.api.config.filter.IConfigRequest;

import java.util.HashMap;
import java.util.Map;

public class ConfigRequest implements IConfigRequest {

    private final Map<String, Object> param = new HashMap<String, Object>();

    private final IConfigContext configContext = new ConfigContext();

    public String getTenant() {
        return (String) param.get("tenant");
    }

    public void setTenant(String tenant) {
        param.put("tenant", tenant);
    }

    public String getDataId() {
        return (String) param.get("dataId");
    }

    public void setDataId(String dataId) {
        param.put("dataId", dataId);
    }

    public String getGroup() {
        return (String) param.get("group");
    }

    public void setGroup(String group) {
        param.put("group", group);
    }

    public String getContent() {
        return (String) param.get("content");
    }

    public void setContent(String content) {
        param.put("content", content);
    }

    @Override
    public Object getParameter(String key) {
        return param.get(key);
    }

    @Override
    public IConfigContext getConfigContext() {
        return configContext;
    }
}
