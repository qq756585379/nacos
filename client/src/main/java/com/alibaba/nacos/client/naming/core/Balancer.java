
package com.alibaba.nacos.client.naming.core;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.alibaba.nacos.client.naming.utils.Chooser;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.alibaba.nacos.client.naming.utils.Pair;

import java.util.ArrayList;
import java.util.List;

import static com.alibaba.nacos.client.utils.LogUtils.NAMING_LOGGER;

public class Balancer {

    public static class RandomByWeight {

        public static List<Instance> selectAll(ServiceInfo serviceInfo) {
            List<Instance> hosts = serviceInfo.getHosts();
            if (CollectionUtils.isEmpty(hosts)) {
                throw new IllegalStateException("no host to srv for serviceInfo: " + serviceInfo.getName());
            }
            return hosts;
        }

        public static Instance selectHost(ServiceInfo dom) {
            List<Instance> hosts = selectAll(dom);
            if (CollectionUtils.isEmpty(hosts)) {
                throw new IllegalStateException("no host to srv for service: " + dom.getName());
            }
            return getHostByRandomWeight(hosts);
        }
    }

    private static Instance getHostByRandomWeight(List<Instance> hosts) {
        //NAMING_LOGGER.debug("entry randomWithWeight");
        if (hosts == null || hosts.size() == 0) {
            //NAMING_LOGGER.debug("hosts == null || hosts.size() == 0");
            return null;
        }
        //NAMING_LOGGER.debug("new Chooser");
        List<Pair<Instance>> hostsWithWeight = new ArrayList<Pair<Instance>>();
        for (Instance host : hosts) {
            if (host.isHealthy()) {
                hostsWithWeight.add(new Pair<Instance>(host, host.getWeight()));
            }
        }
        //NAMING_LOGGER.debug("for (Host host : hosts)");
        Chooser<String, Instance> vipChooser = new Chooser<String, Instance>("www.taobao.com");
        vipChooser.refresh(hostsWithWeight);
        //NAMING_LOGGER.debug("vipChooser.refresh");
        return vipChooser.randomWithWeight();
    }
}
