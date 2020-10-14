
package com.alibaba.nacos.common.http;

import com.alibaba.nacos.common.http.client.NacosAsyncRestTemplate;
import com.alibaba.nacos.common.http.client.NacosRestTemplate;
import com.alibaba.nacos.common.http.client.request.DefaultAsyncHttpClientRequest;
import com.alibaba.nacos.common.http.client.request.JdkHttpClientRequest;
import com.alibaba.nacos.common.tls.SelfHostnameVerifier;
import com.alibaba.nacos.common.tls.TlsFileWatcher;
import com.alibaba.nacos.common.tls.TlsHelper;
import com.alibaba.nacos.common.tls.TlsSystemConfig;
import com.alibaba.nacos.common.utils.BiConsumer;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.slf4j.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public abstract class AbstractHttpClientFactory implements HttpClientFactory {

    @Override
    public NacosRestTemplate createNacosRestTemplate() {
        HttpClientConfig httpClientConfig = buildHttpClientConfig();
        final JdkHttpClientRequest clientRequest = new JdkHttpClientRequest(httpClientConfig);

        // enable ssl
        initTls(new BiConsumer<SSLContext, HostnameVerifier>() {
            @Override
            public void accept(SSLContext sslContext, HostnameVerifier hostnameVerifier) {
                clientRequest.setSSLContext(loadSSLContext());
                clientRequest.replaceSSLHostnameVerifier(hostnameVerifier);
            }
        }, new TlsFileWatcher.FileChangeListener() {
            @Override
            public void onChanged(String filePath) {
                clientRequest.setSSLContext(loadSSLContext());
            }
        });

        return new NacosRestTemplate(assignLogger(), clientRequest);
    }

    @Override
    public NacosAsyncRestTemplate createNacosAsyncRestTemplate() {
        final HttpClientConfig originalRequestConfig = buildHttpClientConfig();
        final RequestConfig requestConfig = getRequestConfig();
        return new NacosAsyncRestTemplate(assignLogger(), new DefaultAsyncHttpClientRequest(
            HttpAsyncClients.custom().setDefaultRequestConfig(requestConfig)
                .setMaxConnTotal(originalRequestConfig.getMaxConnTotal())
                .setMaxConnPerRoute(originalRequestConfig.getMaxConnPerRoute())
                .setUserAgent(originalRequestConfig.getUserAgent()).build()));
    }

    protected RequestConfig getRequestConfig() {
        HttpClientConfig httpClientConfig = buildHttpClientConfig();
        return RequestConfig.custom().setConnectTimeout(httpClientConfig.getConTimeOutMillis())
            .setSocketTimeout(httpClientConfig.getReadTimeOutMillis())
            .setConnectionRequestTimeout(httpClientConfig.getConnectionRequestTimeout())
            .setMaxRedirects(httpClientConfig.getMaxRedirects()).build();
    }

    protected void initTls(BiConsumer<SSLContext, HostnameVerifier> initTlsBiFunc, TlsFileWatcher.FileChangeListener tlsChangeListener) {
        if (!TlsSystemConfig.tlsEnable) {
            return;
        }

        final HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
        final SelfHostnameVerifier selfHostnameVerifier = new SelfHostnameVerifier(hv);

        initTlsBiFunc.accept(loadSSLContext(), selfHostnameVerifier);

        if (tlsChangeListener != null) {
            try {
                TlsFileWatcher.getInstance().addFileChangeListener(tlsChangeListener, TlsSystemConfig.tlsClientTrustCertPath, TlsSystemConfig.tlsClientKeyPath);
            } catch (IOException e) {
                assignLogger().error("add tls file listener fail", e);
            }
        }
    }

    @SuppressWarnings("checkstyle:abbreviationaswordinname")
    protected synchronized SSLContext loadSSLContext() {
        if (!TlsSystemConfig.tlsEnable) {
            return null;
        }
        try {
            return TlsHelper.buildSslContext(true);
        } catch (NoSuchAlgorithmException e) {
            assignLogger().error("Failed to create SSLContext", e);
        } catch (KeyManagementException e) {
            assignLogger().error("Failed to create SSLContext", e);
        }
        return null;
    }

    protected abstract HttpClientConfig buildHttpClientConfig();

    protected abstract Logger assignLogger();
}
