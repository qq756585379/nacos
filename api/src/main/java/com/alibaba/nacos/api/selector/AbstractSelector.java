
package com.alibaba.nacos.api.selector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, property = "type", defaultImpl = NoneSelector.class)
public abstract class AbstractSelector {

    /**
     * The type of this selector, each child class should announce its own unique type.
     */
    @JsonIgnore
    private final String type;

    protected AbstractSelector(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
