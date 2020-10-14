
package com.alibaba.nacos.auth.exception;

import com.alibaba.nacos.api.exception.NacosException;

public class AccessException extends NacosException {

    private static final long serialVersionUID = -2926344920552803270L;

    public AccessException() {

    }

    public AccessException(int code) {
        this.setErrCode(code);
    }

    public AccessException(String msg) {
        this.setErrMsg(msg);
    }
}
