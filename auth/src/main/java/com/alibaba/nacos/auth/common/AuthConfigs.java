
package com.alibaba.nacos.auth.common;

import com.alibaba.nacos.auth.common.env.ReloadableConfigs;
import com.alibaba.nacos.common.JustForTest;
import io.jsonwebtoken.io.Decoders;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class AuthConfigs {

    @JustForTest
    private static Boolean cachingEnabled = null;

    @Autowired
    private ReloadableConfigs reloadableConfigs;

    /**
     * secret key.
     */
    @Value("${nacos.core.auth.default.token.secret.key:}")
    private String secretKey;

    /**
     * secret key byte array.
     */
    private byte[] secretKeyBytes;

    /**
     * Token validity time(seconds).
     */
    @Value("${nacos.core.auth.default.token.expire.seconds:1800}")
    private long tokenValidityInSeconds;

    /**
     * Which auth system is in use.
     */
    @Value("${nacos.core.auth.system.type:}")
    private String nacosAuthSystemType;

    public byte[] getSecretKeyBytes() {
        if (secretKeyBytes == null) {
            secretKeyBytes = Decoders.BASE64.decode(secretKey);
        }
        return secretKeyBytes;
    }

    public long getTokenValidityInSeconds() {
        return tokenValidityInSeconds;
    }

    public String getNacosAuthSystemType() {
        return nacosAuthSystemType;
    }

    public boolean isAuthEnabled() {
        // Runtime -D parameter has higher priority:
        String enabled = System.getProperty("nacos.core.auth.enabled");
        if (StringUtils.isNotBlank(enabled)) {
            return BooleanUtils.toBoolean(enabled);
        }
        return BooleanUtils.toBoolean(reloadableConfigs.getProperties().getProperty("nacos.core.auth.enabled", "false"));
    }

    public boolean isCachingEnabled() {
        if (Objects.nonNull(AuthConfigs.cachingEnabled)) {
            return cachingEnabled;
        }
        return BooleanUtils.toBoolean(reloadableConfigs.getProperties().getProperty("nacos.core.auth.caching.enabled", "true"));
    }

    @JustForTest
    public static void setCachingEnabled(boolean cachingEnabled) {
        AuthConfigs.cachingEnabled = cachingEnabled;
    }
}
