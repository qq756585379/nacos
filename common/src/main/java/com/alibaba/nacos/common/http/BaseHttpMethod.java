
package com.alibaba.nacos.common.http;

import com.alibaba.nacos.common.utils.HttpMethod;
import com.alibaba.nacos.common.utils.StringUtils;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;

import java.net.URI;

public enum BaseHttpMethod {

    /**
     * get request.
     */
    GET(HttpMethod.GET) {
        @Override
        protected HttpRequestBase createRequest(String url) {
            return new HttpGet(url);
        }
    },

    GET_LARGE(HttpMethod.GET_LARGE) {
        @Override
        protected HttpRequestBase createRequest(String url) {
            return new HttpGetWithEntity(url);
        }
    },

    /**
     * post request.
     */
    POST(HttpMethod.POST) {
        @Override
        protected HttpRequestBase createRequest(String url) {
            return new HttpPost(url);
        }
    },

    /**
     * put request.
     */
    PUT(HttpMethod.PUT) {
        @Override
        protected HttpRequestBase createRequest(String url) {
            return new HttpPut(url);
        }
    },

    /**
     * delete request.
     */
    DELETE(HttpMethod.DELETE) {
        @Override
        protected HttpRequestBase createRequest(String url) {
            return new HttpDelete(url);
        }
    },

    /**
     * delete Large request.
     */
    DELETE_LARGE(HttpMethod.DELETE_LARGE) {
        @Override
        protected HttpRequestBase createRequest(String url) {
            return new HttpDeleteWithEntity(url);
        }
    },

    /**
     * head request.
     */
    HEAD(HttpMethod.HEAD) {
        @Override
        protected HttpRequestBase createRequest(String url) {
            return new HttpHead(url);
        }
    },

    /**
     * trace request.
     */
    TRACE(HttpMethod.TRACE) {
        @Override
        protected HttpRequestBase createRequest(String url) {
            return new HttpTrace(url);
        }
    },

    /**
     * patch request.
     */
    PATCH(HttpMethod.PATCH) {
        @Override
        protected HttpRequestBase createRequest(String url) {
            return new HttpPatch(url);
        }
    },

    /**
     * options request.
     */
    OPTIONS(HttpMethod.OPTIONS) {
        @Override
        protected HttpRequestBase createRequest(String url) {
            return new HttpTrace(url);
        }
    };

    private String name;

    BaseHttpMethod(String name) {
        this.name = name;
    }

    public HttpRequestBase init(String url) {
        return createRequest(url);
    }

    protected HttpRequestBase createRequest(String url) {
        throw new UnsupportedOperationException();
    }

    public static BaseHttpMethod sourceOf(String name) {
        for (BaseHttpMethod method : BaseHttpMethod.values()) {
            if (StringUtils.equalsIgnoreCase(name, method.name)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unsupported http method : " + name);
    }

    public static class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {

        public static final String METHOD_NAME = "GET";

        public HttpGetWithEntity(String url) {
            super();
            setURI(URI.create(url));
        }

        @Override
        public String getMethod() {
            return METHOD_NAME;
        }
    }

    public static class HttpDeleteWithEntity extends HttpEntityEnclosingRequestBase {

        public static final String METHOD_NAME = "DELETE";

        public HttpDeleteWithEntity(String url) {
            super();
            setURI(URI.create(url));
        }

        @Override
        public String getMethod() {
            return METHOD_NAME;
        }
    }
}
