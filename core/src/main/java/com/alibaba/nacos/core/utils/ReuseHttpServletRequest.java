
package com.alibaba.nacos.core.utils;

import com.alibaba.nacos.common.http.HttpUtils;
import com.alibaba.nacos.common.http.param.MediaType;
import com.alibaba.nacos.common.utils.ByteUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ReuseHttpServletRequest extends HttpServletRequestWrapper implements ReuseHttpRequest {

    private final HttpServletRequest target;

    private byte[] body;

    private Map<String, String[]> stringMap;

    public ReuseHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        this.target = request;
        this.body = toBytes(request.getInputStream());
        this.stringMap = toDuplication(request);
    }

    @Override
    public Object getBody() throws Exception {
        if (StringUtils.containsIgnoreCase(target.getContentType(), MediaType.MULTIPART_FORM_DATA)) {
            return target.getParts();
        } else {
            String s = ByteUtils.toString(body);
            if (StringUtils.isBlank(s)) {
                return HttpUtils.encodingParams(HttpUtils.translateParameterMap(stringMap), StandardCharsets.UTF_8.name());
            }
            return s;
        }
    }

    private byte[] toBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int n = 0;
        while ((n = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, n);
        }
        return bos.toByteArray();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return stringMap;
    }

    @Override
    public String getParameter(String name) {
        String[] values = stringMap.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    @Override
    public String[] getParameterValues(String name) {
        return stringMap.get(name);
    }

    @Override
    public ServletInputStream getInputStream() {

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(body);

        return new ServletInputStream() {
            @Override
            public int read() {
                return inputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }
}
