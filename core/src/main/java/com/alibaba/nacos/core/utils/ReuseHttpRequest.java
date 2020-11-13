
package com.alibaba.nacos.core.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface ReuseHttpRequest extends HttpServletRequest {

    Object getBody() throws Exception;

    default Map<String, String[]> toDuplication(HttpServletRequest request) {
        Map<String, String[]> tmp = request.getParameterMap();
        Map<String, String[]> result = new HashMap<>(tmp.size());
        Set<String> set = new HashSet<>();
        for (Map.Entry<String, String[]> entry : tmp.entrySet()) {
            set.addAll(Arrays.asList(entry.getValue()));
            result.put(entry.getKey(), set.toArray(new String[0]));
            set.clear();
        }
        return result;
    }
}
