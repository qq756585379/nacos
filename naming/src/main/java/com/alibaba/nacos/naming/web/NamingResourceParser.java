
package com.alibaba.nacos.naming.web;

import com.alibaba.nacos.api.naming.CommonParams;
import com.alibaba.nacos.api.naming.utils.NamingUtils;
import com.alibaba.nacos.auth.model.Resource;
import com.alibaba.nacos.auth.parser.ResourceParser;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class NamingResourceParser implements ResourceParser {

    private static final String AUTH_NAMING_PREFIX = "naming/";

    @Override
    public String parseName(Object request) {

        HttpServletRequest req = (HttpServletRequest) request;

        String namespaceId = req.getParameter(CommonParams.NAMESPACE_ID);
        String serviceName = req.getParameter(CommonParams.SERVICE_NAME);
        String groupName = req.getParameter(CommonParams.GROUP_NAME);
        if (StringUtils.isBlank(groupName)) {
            groupName = NamingUtils.getGroupName(serviceName);
        }
        serviceName = NamingUtils.getServiceName(serviceName);

        StringBuilder sb = new StringBuilder();

        if (StringUtils.isNotBlank(namespaceId)) {
            sb.append(namespaceId);
        }

        sb.append(Resource.SPLITTER);

        if (StringUtils.isBlank(serviceName)) {
            sb.append("*").append(Resource.SPLITTER).append(AUTH_NAMING_PREFIX).append("*");
        } else {
            sb.append(groupName).append(Resource.SPLITTER).append(AUTH_NAMING_PREFIX).append(serviceName);
        }

        return sb.toString();
    }
}
