
package com.alibaba.nacos.auth.common;

public enum ActionTypes {
    /**
     * Read.
     */
    READ("r"),
    /**
     * Write.
     */
    WRITE("w");

    private String action;

    ActionTypes(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return action;
    }
}
