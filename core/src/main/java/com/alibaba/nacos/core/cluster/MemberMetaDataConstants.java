
package com.alibaba.nacos.core.cluster;

public class MemberMetaDataConstants {

    public static final String RAFT_PORT = "raftPort";

    public static final String SITE_KEY = "site";

    public static final String AD_WEIGHT = "adWeight";

    public static final String WEIGHT = "weight";

    public static final String LAST_REFRESH_TIME = "lastRefreshTime";

    public static final String VERSION = "version";

    public static final String[] META_KEY_LIST_WITHOUT_LAST_REFRESH_TIME = new String[]{SITE_KEY, AD_WEIGHT, RAFT_PORT, WEIGHT, VERSION};
}
