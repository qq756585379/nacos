
package com.alibaba.nacos.api.config.filter;

import com.alibaba.nacos.api.exception.NacosException;

public interface IConfigFilter {

    void init(IFilterConfig filterConfig);

    void doFilter(IConfigRequest request, IConfigResponse response, IConfigFilterChain filterChain) throws NacosException;

    int getOrder();

    String getFilterName();
}
