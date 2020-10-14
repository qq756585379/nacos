
package com.alibaba.nacos.core.cluster;

import com.alibaba.nacos.common.notify.Event;
import com.alibaba.nacos.common.notify.listener.Subscriber;

@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
public abstract class MemberChangeListener extends Subscriber<MembersChangeEvent> {

    /**
     * return NodeChangeEvent.class info.
     *
     * @return {@link MembersChangeEvent#getClass()}
     */
    @Override
    public Class<? extends Event> subscribeType() {
        return MembersChangeEvent.class;
    }

    /**
     * Whether to ignore expired events.
     *
     * @return default value is {@link Boolean#TRUE}
     */
    @Override
    public boolean ignoreExpireEvent() {
        return true;
    }
}
