
package com.alibaba.nacos.consistency;

import java.util.Map;

public interface IdGenerator {

    /**
     * Perform the corresponding initialization operation.
     */
    void init();

    /**
     * current id info.
     *
     * @return current id
     */
    long currentId();

    /**
     * Get next id.
     *
     * @return next id
     */
    long nextId();

    /**
     * Returns information for the current IDGenerator.
     *
     * @return {@link Map}
     */
    Map<Object, Object> info();

}
