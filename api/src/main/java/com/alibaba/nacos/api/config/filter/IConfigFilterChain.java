
package com.alibaba.nacos.api.config.filter;

import com.alibaba.nacos.api.exception.NacosException;

public interface IConfigFilterChain {

    void doFilter(IConfigRequest request, IConfigResponse response) throws NacosException;

}
