
package com.alibaba.nacos.client.utils;

import com.alibaba.nacos.common.utils.StringUtils;

public class TenantUtil {

    private static final String USER_TENANT;

    static {
        USER_TENANT = System.getProperty("tenant.id", "");
    }

    public static String getUserTenantForAcm() {
        String tmp = USER_TENANT;
        if (StringUtils.isBlank(USER_TENANT)) {
            tmp = System.getProperty("acm.namespace", "");
        }
        return tmp;
    }

    public static String getUserTenantForAns() {
        String tmp = USER_TENANT;
        if (StringUtils.isBlank(USER_TENANT)) {
            tmp = System.getProperty("ans.namespace");
        }
        return tmp;
    }
}
