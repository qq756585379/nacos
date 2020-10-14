
package com.alibaba.nacos.core.distributed;

import com.alibaba.nacos.consistency.cp.CPProtocol;
import com.alibaba.nacos.core.cluster.ServerMemberManager;
import com.alibaba.nacos.core.distributed.raft.JRaftProtocol;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;

@SuppressWarnings("all")
@Configuration
public class ConsistencyConfiguration {

    @Bean(value = "strongAgreementProtocol")
    public CPProtocol strongAgreementProtocol(ServerMemberManager memberManager) throws Exception {
        final CPProtocol protocol = getProtocol(CPProtocol.class, () -> new JRaftProtocol(memberManager));
        return protocol;
    }

    private <T> T getProtocol(Class<T> cls, Callable<T> builder) throws Exception {
        ServiceLoader<T> protocols = ServiceLoader.load(cls);

        // Select only the first implementation

        Iterator<T> iterator = protocols.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return builder.call();
        }
    }
}
