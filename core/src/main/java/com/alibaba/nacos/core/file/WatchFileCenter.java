
package com.alibaba.nacos.core.file;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.common.executor.ExecutorFactory;
import com.alibaba.nacos.common.executor.NameThreadFactory;
import com.alibaba.nacos.common.utils.ConcurrentHashSet;
import com.alibaba.nacos.common.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class WatchFileCenter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchFileCenter.class);

    /**
     * Maximum number of monitored file directories.
     */
    private static final int MAX_WATCH_FILE_JOB = Integer.getInteger("nacos.watch-file.max-dirs", 16);

    private static final Map<String, WatchDirJob> MANAGER = new HashMap<>(MAX_WATCH_FILE_JOB);

    private static final FileSystem FILE_SYSTEM = FileSystems.getDefault();

    private static final AtomicBoolean CLOSED = new AtomicBoolean(false);

    static {
        ThreadUtils.addShutdownHook(() -> shutdown());
    }

    /**
     * The number of directories that are currently monitored.
     */
    @SuppressWarnings("checkstyle:StaticVariableName")
    private static int NOW_WATCH_JOB_CNT = 0;

    public static synchronized boolean registerWatcher(final String paths, FileWatcher watcher) throws NacosException {
        checkState();
        NOW_WATCH_JOB_CNT++;
        if (NOW_WATCH_JOB_CNT > MAX_WATCH_FILE_JOB) {
            return false;
        }
        WatchDirJob job = MANAGER.get(paths);
        if (job == null) {
            job = new WatchDirJob(paths);
            job.start();
            MANAGER.put(paths, job);
        }
        job.addSubscribe(watcher);
        return true;
    }

    public static synchronized boolean deregisterAllWatcher(final String path) {
        WatchDirJob job = MANAGER.get(path);
        if (job != null) {
            job.shutdown();
            MANAGER.remove(path);
            return true;
        }
        return false;
    }

    public static void shutdown() {
        if (!CLOSED.compareAndSet(false, true)) {
            return;
        }
        LOGGER.warn("[WatchFileCenter] start close");
        for (Map.Entry<String, WatchDirJob> entry : MANAGER.entrySet()) {
            LOGGER.warn("[WatchFileCenter] start to shutdown this watcher which is watch : " + entry.getKey());
            try {
                entry.getValue().shutdown();
            } catch (Throwable e) {
                LOGGER.error("[WatchFileCenter] shutdown has error : {}", e);
            }
        }
        MANAGER.clear();
        LOGGER.warn("[WatchFileCenter] already closed");
    }

    public static synchronized boolean deregisterWatcher(final String path, final FileWatcher watcher) {
        WatchDirJob job = MANAGER.get(path);
        if (job != null) {
            job.watchers.remove(watcher);
            return true;
        }
        return false;
    }

    private static class WatchDirJob extends Thread {

        private ExecutorService callBackExecutor;

        private final String paths;

        private WatchService watchService;

        private volatile boolean watch = true;

        private Set<FileWatcher> watchers = new ConcurrentHashSet<>();

        public WatchDirJob(String paths) throws NacosException {
            setName(paths);
            this.paths = paths;
            final Path p = Paths.get(paths);
            if (!p.toFile().isDirectory()) {
                throw new IllegalArgumentException("Must be a file directory : " + paths);
            }

            this.callBackExecutor = ExecutorFactory.newSingleExecutorService(new NameThreadFactory("com.alibaba.nacos.core.file.watch-" + paths));

            try {
                WatchService service = FILE_SYSTEM.newWatchService();
                p.register(service, StandardWatchEventKinds.OVERFLOW, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
                this.watchService = service;
            } catch (Throwable ex) {
                throw new NacosException(NacosException.SERVER_ERROR, ex);
            }
        }

        void addSubscribe(final FileWatcher watcher) {
            watchers.add(watcher);
        }

        void shutdown() {
            watch = false;
            ThreadUtils.shutdownThreadPool(this.callBackExecutor);
        }

        @Override
        public void run() {
            while (watch) {
                try {
                    final WatchKey watchKey = watchService.take();
                    final List<WatchEvent<?>> events = watchKey.pollEvents();
                    watchKey.reset();
                    if (callBackExecutor.isShutdown()) {
                        return;
                    }
                    if (events.isEmpty()) {
                        continue;
                    }
                    callBackExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            for (WatchEvent<?> event : events) {
                                WatchEvent.Kind<?> kind = event.kind();

                                // Since the OS's event cache may be overflow, a backstop is needed
                                if (StandardWatchEventKinds.OVERFLOW.equals(kind)) {
                                    eventOverflow();
                                } else {
                                    eventProcess(event.context());
                                }
                            }
                        }
                    });
                } catch (InterruptedException ignore) {
                    Thread.interrupted();
                } catch (Throwable ex) {
                    LOGGER.error("An exception occurred during file listening : {}", ex);
                }
            }
        }

        private void eventProcess(Object context) {
            final FileChangeEvent fileChangeEvent = FileChangeEvent.builder().paths(paths).context(context).build();
            final String str = String.valueOf(context);
            for (final FileWatcher watcher : watchers) {
                if (watcher.interest(str)) {
                    Runnable job = () -> watcher.onChange(fileChangeEvent);
                    Executor executor = watcher.executor();
                    if (executor == null) {
                        try {
                            job.run();
                        } catch (Throwable ex) {
                            LOGGER.error("File change event callback error : {}", ex);
                        }
                    } else {
                        executor.execute(job);
                    }
                }
            }
        }

        private void eventOverflow() {
            File dir = Paths.get(paths).toFile();
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                // Subdirectories do not participate in listening
                if (file.isDirectory()) {
                    continue;
                }
                eventProcess(file.getName());
            }
        }
    }

    private static void checkState() {
        if (CLOSED.get()) {
            throw new IllegalStateException("WatchFileCenter already shutdown");
        }
    }
}
