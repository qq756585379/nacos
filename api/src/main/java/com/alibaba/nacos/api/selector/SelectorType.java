
package com.alibaba.nacos.api.selector;

public enum SelectorType {
    /**
     * not match any type.
     */
    unknown,
    /**
     * not filter out any entity.
     */
    none,
    /**
     * select by label.
     */
    label
}
