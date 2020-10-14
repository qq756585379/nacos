
package com.alibaba.nacos.common.http.param;

import com.alibaba.nacos.common.utils.MapUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Query {

    private boolean isEmpty = true;

    public static final Query EMPTY = new Query();

    private Map<String, Object> params;

    public Query() {
        params = new LinkedHashMap<String, Object>();
    }

    public static Query newInstance() {
        return new Query();
    }

    public Query addParam(String key, Object value) {
        isEmpty = false;
        params.put(key, value);
        return this;
    }

    public Object getValue(String key) {
        return params.get(key);
    }

    public Query initParams(Map<String, String> params) {
        if (MapUtils.isNotEmpty(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                addParam(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    /**
     * Add query parameters from KV list. KV list: odd index is key, even index is value.
     *
     * @param list KV list
     */
    public void initParams(List<String> list) {
        if ((list.size() & 1) != 0) {
            throw new IllegalArgumentException("list size must be a multiple of 2");
        }
        for (int i = 0; i < list.size(); ) {
            addParam(list.get(i++), list.get(i++));
        }
    }

    public String toQueryUrl() {
        StringBuilder urlBuilder = new StringBuilder();
        Set<Map.Entry<String, Object>> entrySet = params.entrySet();
        int i = entrySet.size();
        for (Map.Entry<String, Object> entry : entrySet) {
            try {
                if (null != entry.getValue()) {
                    urlBuilder.append(entry.getKey()).append("=").append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
                    if (i > 1) {
                        urlBuilder.append("&");
                    }
                }
                i--;
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        return urlBuilder.toString();
    }

    public void clear() {
        isEmpty = false;
        params.clear();
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}
