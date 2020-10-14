
package com.alibaba.nacos.api.config.filter;

public interface IConfigRequest {

    Object getParameter(String key);

    IConfigContext getConfigContext();
}
