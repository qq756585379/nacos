
package com.alibaba.nacos.auth.model;

import java.io.Serializable;

public class Permission implements Serializable {

    private static final long serialVersionUID = -3583076254743606551L;

    /**
     * An unique key of resource.
     */
    private String resource;

    /**
     * Action on resource, refer to class ActionTypes.
     */
    private String action;

    public Permission() {

    }

    public Permission(String resource, String action) {
        this.resource = resource;
        this.action = action;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "Permission{" + "resource='" + resource + '\'' + ", action='" + action + '\'' + '}';
    }
}
