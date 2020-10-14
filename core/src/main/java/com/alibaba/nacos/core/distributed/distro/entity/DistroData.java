package com.alibaba.nacos.core.distributed.distro.entity;

import com.alibaba.nacos.consistency.DataOperation;

public class DistroData {

    private DistroKey distroKey;

    private DataOperation type;

    private byte[] content;

    public DistroData() {
    }

    public DistroData(DistroKey distroKey, byte[] content) {
        this.distroKey = distroKey;
        this.content = content;
    }

    public DistroKey getDistroKey() {
        return distroKey;
    }

    public void setDistroKey(DistroKey distroKey) {
        this.distroKey = distroKey;
    }

    public DataOperation getType() {
        return type;
    }

    public void setType(DataOperation type) {
        this.type = type;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
