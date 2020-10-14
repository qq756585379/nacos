
package com.alibaba.nacos.client.naming.utils;

import com.alibaba.nacos.common.utils.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUtils {

    private static String localIp;

    public static String localIP() {
        try {
            if (!StringUtils.isEmpty(localIp)) {
                return localIp;
            }
            String ip = System.getProperty("com.alibaba.nacos.client.naming.local.ip", InetAddress.getLocalHost().getHostAddress());
            return localIp = ip;
        } catch (UnknownHostException e) {
            return "resolve_failed";
        }
    }
}
