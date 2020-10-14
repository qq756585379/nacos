
package com.alibaba.nacos.api.config.listener;

import com.alibaba.nacos.api.config.ConfigChangeItem;

import java.io.IOException;
import java.util.Map;

public interface ConfigChangeParser {

    boolean isResponsibleFor(String type);

    Map<String, ConfigChangeItem> doParse(String oldContent, String newContent, String type) throws IOException;
}
