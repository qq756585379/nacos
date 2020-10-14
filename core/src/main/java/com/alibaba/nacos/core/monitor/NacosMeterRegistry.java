
package com.alibaba.nacos.core.monitor;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public final class NacosMeterRegistry {

    private static final CompositeMeterRegistry METER_REGISTRY = new CompositeMeterRegistry();

    public static DistributionSummary summary(String module, String name) {
        ImmutableTag moduleTag = new ImmutableTag("module", module);
        List<Tag> tags = new ArrayList<>();
        tags.add(moduleTag);
        tags.add(new ImmutableTag("name", name));
        return METER_REGISTRY.summary("nacos_monitor", tags);
    }

    public static Timer timer(String module, String name) {
        ImmutableTag moduleTag = new ImmutableTag("module", module);
        List<Tag> tags = new ArrayList<>();
        tags.add(moduleTag);
        tags.add(new ImmutableTag("name", name));
        return METER_REGISTRY.timer("nacos_monitor", tags);
    }
}
