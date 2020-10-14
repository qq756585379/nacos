
package com.alibaba.nacos.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Loggers {

    public static final Logger AUTH = LoggerFactory.getLogger("com.alibaba.nacos.core.auth");

    public static final Logger CORE = LoggerFactory.getLogger("com.alibaba.nacos.core");

    public static final Logger RAFT = LoggerFactory.getLogger("com.alibaba.nacos.core.protocol.raft");

    public static final Logger DISTRO = LoggerFactory.getLogger("com.alibaba.nacos.core.protocol.distro");

    public static final Logger CLUSTER = LoggerFactory.getLogger("com.alibaba.nacos.core.cluster");
}
