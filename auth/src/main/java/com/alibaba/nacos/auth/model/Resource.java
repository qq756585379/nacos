
package com.alibaba.nacos.auth.model;

import java.io.Serializable;

public class Resource implements Serializable {

    public static final String SPLITTER = ":";

    public static final String ANY = "*";

    private static final long serialVersionUID = 925971662931204553L;

    /**
     * The unique key of resource.
     */
    private String key;

    public Resource(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String parseName() {
        return key.substring(0, key.lastIndexOf(SPLITTER));
    }

    @Override
    public String toString() {
        return "Resource{" + "key='" + key + '\'' + '}';
    }
}
