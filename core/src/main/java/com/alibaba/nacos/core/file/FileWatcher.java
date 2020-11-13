
package com.alibaba.nacos.core.file;

import java.util.concurrent.Executor;

@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
public abstract class FileWatcher {

    public abstract void onChange(FileChangeEvent event);

    public abstract boolean interest(String context);

    public Executor executor() {
        return null;
    }
}
