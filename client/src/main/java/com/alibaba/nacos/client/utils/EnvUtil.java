
package com.alibaba.nacos.client.utils;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnvUtil {

    public static final Logger LOGGER = LogUtils.logger(EnvUtil.class);

    private static String selfAmorayTag;

    private static String selfVipserverTag;

    private static String selfLocationTag;

    private static final String AMORY_TAG = "Amory-Tag";

    private static final String VIPSERVER_TAG = "Vipserver-Tag";

    private static final String LOCATION_TAG = "Location-Tag";

    public static void setSelfEnv(Map<String, List<String>> headers) {
        if (headers != null) {
            List<String> amorayTagTmp = headers.get(AMORY_TAG);
            if (amorayTagTmp == null) {
                if (selfAmorayTag != null) {
                    selfAmorayTag = null;
                    //LOGGER.warn("selfAmoryTag:null");
                }
            } else {
                String amorayTagTmpStr = listToString(amorayTagTmp);
                if (!amorayTagTmpStr.equals(selfAmorayTag)) {
                    selfAmorayTag = amorayTagTmpStr;
                    //LOGGER.warn("selfAmoryTag:{}", selfAmorayTag);
                }
            }

            List<String> vipserverTagTmp = headers.get(VIPSERVER_TAG);
            if (vipserverTagTmp == null) {
                if (selfVipserverTag != null) {
                    selfVipserverTag = null;
                    //LOGGER.warn("selfVipserverTag:null");
                }
            } else {
                String vipserverTagTmpStr = listToString(vipserverTagTmp);
                if (!vipserverTagTmpStr.equals(selfVipserverTag)) {
                    selfVipserverTag = vipserverTagTmpStr;
                    //LOGGER.warn("selfVipserverTag:{}", selfVipserverTag);
                }
            }
            List<String> locationTagTmp = headers.get(LOCATION_TAG);
            if (locationTagTmp == null) {
                if (selfLocationTag != null) {
                    selfLocationTag = null;
                    //LOGGER.warn("selfLocationTag:null");
                }
            } else {
                String locationTagTmpStr = listToString(locationTagTmp);
                if (!locationTagTmpStr.equals(selfLocationTag)) {
                    selfLocationTag = locationTagTmpStr;
                    //LOGGER.warn("selfLocationTag:{}", selfLocationTag);
                }
            }
        }
    }

    //[a,b] -> a,b
    private static String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (String string : list) {
            result.append(string);
            result.append(",");
        }
        return result.toString().substring(0, result.length() - 1);
    }
}
