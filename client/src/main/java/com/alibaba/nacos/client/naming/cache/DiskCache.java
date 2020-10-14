
package com.alibaba.nacos.client.naming.cache;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.alibaba.nacos.common.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.nacos.client.utils.LogUtils.NAMING_LOGGER;

public class DiskCache {

    public static void write(ServiceInfo dom, String dir) {
        try {
            makeSureCacheDirExists(dir);

            File file = new File(dir, dom.getKeyEncoded());
            if (!file.exists()) {
                // add another !file.exists() to avoid conflicted creating-new-file from multi-instances
                if (!file.createNewFile() && !file.exists()) {
                    throw new IllegalStateException("failed to create cache file");
                }
            }

            StringBuilder keyContentBuffer = new StringBuilder();

            String json = dom.getJsonFromServer();

            if (StringUtils.isEmpty(json)) {
                json = JacksonUtils.toJson(dom);
            }

            keyContentBuffer.append(json);

            //Use the concurrent API to ensure the consistency.
            ConcurrentDiskUtil.writeFileContent(file, keyContentBuffer.toString(), Charset.defaultCharset().toString());

        } catch (Throwable e) {
            NAMING_LOGGER.error("[NA] failed to write cache for dom:" + dom.getName(), e);
        }
    }

    public static String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    public static Map<String, ServiceInfo> read(String cacheDir) {
        Map<String, ServiceInfo> domMap = new HashMap<String, ServiceInfo>(16);

        BufferedReader reader = null;
        try {
            File[] files = makeSureCacheDirExists(cacheDir).listFiles();
            if (files == null || files.length == 0) {
                return domMap;
            }

            for (File file : files) {
                if (!file.isFile()) {
                    continue;
                }

                String fileName = URLDecoder.decode(file.getName(), "UTF-8");
                if (!(fileName.endsWith(Constants.SERVICE_INFO_SPLITER + "meta") || fileName.endsWith(Constants.SERVICE_INFO_SPLITER + "special-url"))) {
                    ServiceInfo dom = new ServiceInfo(fileName);
                    List<Instance> ips = new ArrayList<Instance>();
                    dom.setHosts(ips);

                    ServiceInfo newFormat = null;

                    try {
                        String dataString = ConcurrentDiskUtil.getFileContent(file, Charset.defaultCharset().toString());
                        reader = new BufferedReader(new StringReader(dataString));

                        String json;
                        while ((json = reader.readLine()) != null) {
                            try {
                                if (!json.startsWith("{")) {
                                    continue;
                                }

                                newFormat = JacksonUtils.toObj(json, ServiceInfo.class);

                                if (StringUtils.isEmpty(newFormat.getName())) {
                                    ips.add(JacksonUtils.toObj(json, Instance.class));
                                }
                            } catch (Throwable e) {
                                NAMING_LOGGER.error("[NA] error while parsing cache file: " + json, e);
                            }
                        }
                    } catch (Exception e) {
                        NAMING_LOGGER.error("[NA] failed to read cache for dom: " + file.getName(), e);
                    } finally {
                        try {
                            if (reader != null) {
                                reader.close();
                            }
                        } catch (Exception e) {
                            //ignore
                        }
                    }
                    if (newFormat != null && !StringUtils.isEmpty(newFormat.getName()) && !CollectionUtils.isEmpty(newFormat.getHosts())) {
                        domMap.put(dom.getKey(), newFormat);
                    } else if (!CollectionUtils.isEmpty(dom.getHosts())) {
                        domMap.put(dom.getKey(), dom);
                    }
                }
            }
        } catch (Throwable e) {
            NAMING_LOGGER.error("[NA] failed to read cache file", e);
        }
        return domMap;
    }

    private static File makeSureCacheDirExists(String dir) {
        File cacheDir = new File(dir);
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs() && !cacheDir.exists()) {
                throw new IllegalStateException("failed to create cache dir: " + dir);
            }
        }
        return cacheDir;
    }
}
