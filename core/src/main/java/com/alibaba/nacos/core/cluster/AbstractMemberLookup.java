
package com.alibaba.nacos.core.cluster;

import com.alibaba.nacos.api.exception.NacosException;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractMemberLookup implements MemberLookup {

    protected ServerMemberManager memberManager;

    protected AtomicBoolean start = new AtomicBoolean(false);

    @Override
    public void injectMemberManager(ServerMemberManager memberManager) {
        this.memberManager = memberManager;
    }

    @Override
    public void afterLookup(Collection<Member> members) {
        this.memberManager.memberChange(members);
    }

    @Override
    public void destroy() throws NacosException {

    }
}
