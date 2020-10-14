package com.alibaba.nacos.client.config.http;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.monitor.MetricsMonitor;
import com.alibaba.nacos.common.http.HttpRestResult;
import io.prometheus.client.Histogram;

import java.io.IOException;
import java.util.Map;

public class MetricsHttpAgent implements HttpAgent {

    private final HttpAgent httpAgent;

    public MetricsHttpAgent(HttpAgent httpAgent) {
        this.httpAgent = httpAgent;
    }

    @Override
    public void start() throws NacosException {
        httpAgent.start();
    }

    @Override
    public HttpRestResult<String> httpGet(String path, Map<String, String> headers, Map<String, String> paramValues, String encode, long readTimeoutMs) throws Exception {
        Histogram.Timer timer = MetricsMonitor.getConfigRequestMonitor("GET", path, "NA");
        HttpRestResult<String> result;
        try {
            result = httpAgent.httpGet(path, headers, paramValues, encode, readTimeoutMs);
        } catch (IOException e) {
            throw e;
        } finally {
            timer.observeDuration();
            timer.close();
        }
        return result;
    }

    @Override
    public HttpRestResult<String> httpPost(String path, Map<String, String> headers, Map<String, String> paramValues, String encode, long readTimeoutMs) throws Exception {
        Histogram.Timer timer = MetricsMonitor.getConfigRequestMonitor("POST", path, "NA");
        HttpRestResult<String> result;
        try {
            result = httpAgent.httpPost(path, headers, paramValues, encode, readTimeoutMs);
        } catch (IOException e) {
            throw e;
        } finally {
            timer.observeDuration();
            timer.close();
        }
        return result;
    }

    @Override
    public HttpRestResult<String> httpDelete(String path, Map<String, String> headers, Map<String, String> paramValues, String encode, long readTimeoutMs) throws Exception {
        Histogram.Timer timer = MetricsMonitor.getConfigRequestMonitor("DELETE", path, "NA");
        HttpRestResult<String> result;
        try {
            result = httpAgent.httpDelete(path, headers, paramValues, encode, readTimeoutMs);
        } catch (IOException e) {
            throw e;
        } finally {
            timer.observeDuration();
            timer.close();
        }
        return result;
    }

    @Override
    public String getName() {
        return httpAgent.getName();
    }

    @Override
    public String getNamespace() {
        return httpAgent.getNamespace();
    }

    @Override
    public String getTenant() {
        return httpAgent.getTenant();
    }

    @Override
    public String getEncode() {
        return httpAgent.getEncode();
    }

    @Override
    public void shutdown() throws NacosException {
        httpAgent.shutdown();
    }
}

