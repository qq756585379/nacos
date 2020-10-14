
package com.alibaba.nacos.client.config.impl;

import com.alibaba.nacos.api.config.listener.ConfigChangeParser;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class ConfigChangeHandler {

    private final List<ConfigChangeParser> parserList;

    private ConfigChangeHandler() {
        this.parserList = new LinkedList<ConfigChangeParser>();
        ServiceLoader<ConfigChangeParser> loader = ServiceLoader.load(ConfigChangeParser.class);
        for (ConfigChangeParser aLoader : loader) {
            this.parserList.add(aLoader);
        }
        this.parserList.add(new PropertiesChangeParser());
        this.parserList.add(new YmlChangeParser());
    }

    private static class ConfigChangeHandlerHolder {
        private static final ConfigChangeHandler INSTANCE = new ConfigChangeHandler();
    }

    public static ConfigChangeHandler getInstance() {
        return ConfigChangeHandlerHolder.INSTANCE;
    }

    public Map parseChangeData(String oldContent, String newContent, String type) throws IOException {
        for (ConfigChangeParser changeParser : this.parserList) {
            if (changeParser.isResponsibleFor(type)) {
                return changeParser.doParse(oldContent, newContent, type);
            }
        }
        return Collections.emptyMap();
    }
}
