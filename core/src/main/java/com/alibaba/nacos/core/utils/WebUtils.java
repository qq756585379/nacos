
package com.alibaba.nacos.core.utils;

import com.alibaba.nacos.common.constant.HttpHeaderConsts;
import com.alibaba.nacos.common.http.HttpUtils;
import com.alibaba.nacos.common.model.RestResult;
import com.alibaba.nacos.common.model.RestResultUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class WebUtils {

    public static String required(final HttpServletRequest req, final String key) {
        String value = req.getParameter(key);
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException("Param '" + key + "' is required.");
        }
        String encoding = req.getParameter("encoding");
        return resolveValue(value, encoding);
    }

    public static String optional(final HttpServletRequest req, final String key, final String defaultValue) {
        if (!req.getParameterMap().containsKey(key) || req.getParameterMap().get(key)[0] == null) {
            return defaultValue;
        }
        String value = req.getParameter(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        String encoding = req.getParameter("encoding");
        return resolveValue(value, encoding);
    }

    private static String resolveValue(String value, String encoding) {
        if (StringUtils.isEmpty(encoding)) {
            encoding = StandardCharsets.UTF_8.name();
        }
        try {
            value = HttpUtils.decode(new String(value.getBytes(StandardCharsets.UTF_8), encoding), encoding);
        } catch (UnsupportedEncodingException ignore) {
        }
        return value.trim();
    }

    public static String getAcceptEncoding(HttpServletRequest req) {
        String encode = StringUtils.defaultIfEmpty(req.getHeader("Accept-Charset"), StandardCharsets.UTF_8.name());
        encode = encode.contains(",") ? encode.substring(0, encode.indexOf(",")) : encode;
        return encode.contains(";") ? encode.substring(0, encode.indexOf(";")) : encode;
    }

    public static String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader(HttpHeaderConsts.USER_AGENT_HEADER);
        if (StringUtils.isEmpty(userAgent)) {
            userAgent = StringUtils.defaultIfEmpty(request.getHeader(HttpHeaderConsts.CLIENT_VERSION_HEADER), StringUtils.EMPTY);
        }
        return userAgent;
    }

    public static void response(HttpServletResponse response, String body, int code) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(body);
        response.setStatus(code);
    }

    public static void onFileUpload(MultipartFile multipartFile, Consumer<File> consumer, DeferredResult<RestResult<String>> response) {
        if (Objects.isNull(multipartFile) || multipartFile.isEmpty()) {
            response.setResult(RestResultUtils.failed("File is empty"));
            return;
        }
        File tmpFile = null;
        try {
            tmpFile = DiskUtils.createTmpFile(multipartFile.getName(), ".tmp");
            multipartFile.transferTo(tmpFile);
            consumer.accept(tmpFile);
        } catch (Throwable ex) {
            if (!response.isSetOrExpired()) {
                response.setResult(RestResultUtils.failed(ex.getMessage()));
            }
        } finally {
            DiskUtils.deleteQuietly(tmpFile);
        }
    }

    public static <T> void process(DeferredResult<T> deferredResult, CompletableFuture<T> future, Function<Throwable, T> errorHandler) {
        deferredResult.onTimeout(future::join);

        future.whenComplete((t, throwable) -> {
            if (Objects.nonNull(throwable)) {
                deferredResult.setResult(errorHandler.apply(throwable));
                return;
            }
            deferredResult.setResult(t);
        });
    }

    public static <T> void process(DeferredResult<T> deferredResult, CompletableFuture<T> future, Runnable success, Function<Throwable, T> errorHandler) {
        deferredResult.onTimeout(future::join);

        future.whenComplete((t, throwable) -> {
            if (Objects.nonNull(throwable)) {
                deferredResult.setResult(errorHandler.apply(throwable));
                return;
            }
            success.run();
            deferredResult.setResult(t);
        });
    }
}
