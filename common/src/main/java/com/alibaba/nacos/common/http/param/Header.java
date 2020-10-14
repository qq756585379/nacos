
package com.alibaba.nacos.common.http.param;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.common.constant.HttpHeaderConsts;
import com.alibaba.nacos.common.utils.MapUtils;
import com.alibaba.nacos.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Header {

    public static final Header EMPTY = Header.newInstance();

    private final Map<String, String> header;

    private final Map<String, List<String>> originalResponseHeader;

    private Header() {
        header = new LinkedHashMap<String, String>();
        originalResponseHeader = new LinkedHashMap<String, List<String>>();
        addParam(HttpHeaderConsts.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        addParam(HttpHeaderConsts.ACCEPT_CHARSET, "UTF-8");
        addParam(HttpHeaderConsts.ACCEPT_ENCODING, "gzip");
    }

    public static Header newInstance() {
        return new Header();
    }

    public Header addParam(String key, String value) {
        if (StringUtils.isNotEmpty(key)) {
            header.put(key, value);
        }
        return this;
    }

    public Header setContentType(String contentType) {
        if (contentType == null) {
            contentType = MediaType.APPLICATION_JSON;
        }
        return addParam(HttpHeaderConsts.CONTENT_TYPE, contentType);
    }

    public Header build() {
        return this;
    }

    public String getValue(String key) {
        return header.get(key);
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public Iterator<Map.Entry<String, String>> iterator() {
        return header.entrySet().iterator();
    }

    public List<String> toList() {
        List<String> list = new ArrayList<String>(header.size() * 2);
        Iterator<Map.Entry<String, String>> iterator = iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            list.add(entry.getKey());
            list.add(entry.getValue());
        }
        return list;
    }

    public Header addAll(List<String> list) {
        if ((list.size() & 1) != 0) {
            throw new IllegalArgumentException("list size must be a multiple of 2");
        }
        for (int i = 0; i < list.size(); ) {
            String key = list.get(i++);
            if (StringUtils.isNotEmpty(key)) {
                header.put(key, list.get(i++));
            }
        }
        return this;
    }

    public void addAll(Map<String, String> params) {
        if (MapUtils.isNotEmpty(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                addParam(entry.getKey(), entry.getValue());
            }
        }
    }

    public void setOriginalResponseHeader(Map<String, List<String>> headers) {
        if (MapUtils.isNotEmpty(headers)) {
            this.originalResponseHeader.putAll(headers);
            for (Map.Entry<String, List<String>> entry : this.originalResponseHeader.entrySet()) {
                addParam(entry.getKey(), entry.getValue().get(0));
            }
        }
    }

    public Map<String, List<String>> getOriginalResponseHeader() {
        return this.originalResponseHeader;
    }

    public String getCharset() {
        String acceptCharset = getValue(HttpHeaderConsts.ACCEPT_CHARSET);
        if (acceptCharset == null) {
            String contentType = getValue(HttpHeaderConsts.CONTENT_TYPE);
            acceptCharset = StringUtils.isNotBlank(contentType) ? analysisCharset(contentType) : Constants.ENCODE;
        }
        return acceptCharset;
    }

    private String analysisCharset(String contentType) {
        String[] values = contentType.split(";");
        String charset = "UTF-8";
        if (values.length == 0) {
            return charset;
        }
        for (String value : values) {
            if (value.startsWith("charset=")) {
                charset = value.substring("charset=".length());
            }
        }
        return charset;
    }

    public void clear() {
        header.clear();
        originalResponseHeader.clear();
    }

    @Override
    public String toString() {
        return "Header{" + "headerToMap=" + header + '}';
    }
}

