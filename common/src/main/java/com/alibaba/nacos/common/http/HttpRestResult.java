
package com.alibaba.nacos.common.http;

import com.alibaba.nacos.common.http.param.Header;
import com.alibaba.nacos.common.model.RestResult;

public class HttpRestResult<T> extends RestResult<T> {

    private static final long serialVersionUID = 3766947816720175947L;

    private Header header;

    public HttpRestResult() {
    }

    public HttpRestResult(Header header, int code, T data, String message) {
        super(code, message, data);
        this.header = header;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }
}
