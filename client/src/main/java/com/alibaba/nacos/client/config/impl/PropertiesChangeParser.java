
package com.alibaba.nacos.client.config.impl;

import com.alibaba.nacos.api.config.ConfigChangeItem;
import com.alibaba.nacos.common.utils.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

public class PropertiesChangeParser extends AbstractConfigChangeParser {

    public PropertiesChangeParser() {
        super("properties");
    }

    @Override
    public Map<String, ConfigChangeItem> doParse(String oldContent, String newContent, String type) throws IOException {
        Properties oldProps = new Properties();
        Properties newProps = new Properties();

        if (StringUtils.isNotBlank(oldContent)) {
            oldProps.load(new StringReader(oldContent));
        }
        if (StringUtils.isNotBlank(newContent)) {
            newProps.load(new StringReader(newContent));
        }

        return filterChangeData(oldProps, newProps);
    }
}
