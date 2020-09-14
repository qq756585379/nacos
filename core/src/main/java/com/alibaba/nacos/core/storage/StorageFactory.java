/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.core.storage;

import com.alibaba.nacos.core.storage.kv.FileKVStorage;
import com.alibaba.nacos.core.storage.kv.KvStorage;
import com.alibaba.nacos.core.storage.kv.MemoryKVStorage;
import com.alibaba.nacos.core.storage.kv.RocksStorage;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public final class StorageFactory {
    
    public static KvStorage createKVStorage(KvStorage.KVType type, final String label, final String baseDir) throws Exception {
        switch (type) {
            case File:
                return new FileKVStorage(baseDir);
            case Memory:
                return new MemoryKVStorage();
            case RocksDB:
                return RocksStorage.createDefault(label, baseDir);
            default:
                throw new IllegalArgumentException("this kv type : [" + type.name() + "] not support");
        }
    }
    
}