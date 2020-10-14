
package com.alibaba.nacos.core.distributed;

import com.alibaba.nacos.consistency.Config;
import com.alibaba.nacos.consistency.ConsistencyProtocol;
import com.alibaba.nacos.consistency.LogProcessor;
import com.alibaba.nacos.consistency.ProtocolMetaData;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public abstract class AbstractConsistencyProtocol<T extends Config, L extends LogProcessor> implements ConsistencyProtocol<T, L> {

    protected final ProtocolMetaData metaData = new ProtocolMetaData();

    protected Map<String, L> processorMap = Collections.synchronizedMap(new HashMap<>());

    public void loadLogProcessor(List<L> logProcessors) {
        logProcessors.forEach(logDispatcher -> processorMap.put(logDispatcher.group(), logDispatcher));
    }

    protected Map<String, L> allProcessor() {
        return processorMap;
    }

    @Override
    public ProtocolMetaData protocolMetaData() {
        return this.metaData;
    }

}
