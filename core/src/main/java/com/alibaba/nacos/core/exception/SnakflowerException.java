
package com.alibaba.nacos.core.exception;

public class SnakflowerException extends RuntimeException {

    public SnakflowerException() {
        super();
    }

    public SnakflowerException(String message) {
        super(message);
    }

    public SnakflowerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SnakflowerException(Throwable cause) {
        super(cause);
    }

    protected SnakflowerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
