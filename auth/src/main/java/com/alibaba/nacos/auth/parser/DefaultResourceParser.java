
package com.alibaba.nacos.auth.parser;

import org.apache.commons.lang3.StringUtils;

public class DefaultResourceParser implements ResourceParser {

    @Override
    public String parseName(Object request) {
        return StringUtils.EMPTY;
    }
}
