package com.alibaba.nacos.test.base;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class Params {

    private MultiValueMap<String, String> paramMap;

    public static Params newParams() {
        Params params = new Params();
        params.paramMap = new LinkedMultiValueMap<>();
        return params;
    }

    public Params appendParam(String name, String value) {
        this.paramMap.add(name, value);
        return this;
    }

    public MultiValueMap<String, String> done() {
        return paramMap;
    }
}
