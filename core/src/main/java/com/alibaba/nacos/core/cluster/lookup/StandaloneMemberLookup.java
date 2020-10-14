
package com.alibaba.nacos.core.cluster.lookup;

import com.alibaba.nacos.core.cluster.AbstractMemberLookup;
import com.alibaba.nacos.core.cluster.MemberUtils;
import com.alibaba.nacos.core.utils.ApplicationUtils;
import com.alibaba.nacos.core.utils.InetUtils;

import java.util.Collections;

public class StandaloneMemberLookup extends AbstractMemberLookup {

    @Override
    public void start() {
        if (start.compareAndSet(false, true)) {
            String url = InetUtils.getSelfIp() + ":" + ApplicationUtils.getPort();
            afterLookup(MemberUtils.readServerConf(Collections.singletonList(url)));
        }
    }
}
