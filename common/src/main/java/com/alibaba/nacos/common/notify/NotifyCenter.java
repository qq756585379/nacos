
package com.alibaba.nacos.common.notify;

import com.alibaba.nacos.api.exception.runtime.NacosRuntimeException;
import com.alibaba.nacos.common.JustForTest;
import com.alibaba.nacos.common.notify.listener.Subscriber;
import com.alibaba.nacos.common.notify.listener.SmartSubscriber;
import com.alibaba.nacos.common.utils.BiFunction;
import com.alibaba.nacos.common.utils.ClassUtils;
import com.alibaba.nacos.common.utils.MapUtils;
import com.alibaba.nacos.common.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.alibaba.nacos.api.exception.NacosException.SERVER_ERROR;

public class NotifyCenter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyCenter.class);

    public static int ringBufferSize;

    public static int shareBufferSize;

    private static final AtomicBoolean CLOSED = new AtomicBoolean(false);

    private static BiFunction<Class<? extends Event>, Integer, EventPublisher> publisherFactory = null;

    private static final NotifyCenter INSTANCE = new NotifyCenter();

    private DefaultSharePublisher sharePublisher;

    private static Class<? extends EventPublisher> clazz = null;

    /**
     * Publisher management container.
     */
    private final Map<String, EventPublisher> publisherMap = new ConcurrentHashMap<String, EventPublisher>(16);

    static {
        // Internal ArrayBlockingQueue buffer size. For applications with high write throughput,
        // this value needs to be increased appropriately. default value is 16384
        String ringBufferSizeProperty = "nacos.core.notify.ring-buffer-size";
        ringBufferSize = Integer.getInteger(ringBufferSizeProperty, 16384);

        // The size of the public publisher's message staging queue buffer
        String shareBufferSizeProperty = "nacos.core.notify.share-buffer-size";
        shareBufferSize = Integer.getInteger(shareBufferSizeProperty, 1024);

        final ServiceLoader<EventPublisher> loader = ServiceLoader.load(EventPublisher.class);
        Iterator<EventPublisher> iterator = loader.iterator();

        if (iterator.hasNext()) {
            clazz = iterator.next().getClass();
        } else {
            clazz = DefaultPublisher.class;
        }

        publisherFactory = new BiFunction<Class<? extends Event>, Integer, EventPublisher>() {
            @Override
            public EventPublisher apply(Class<? extends Event> cls, Integer buffer) {
                try {
                    EventPublisher publisher = clazz.newInstance();
                    publisher.init(cls, buffer);
                    return publisher;
                } catch (Throwable ex) {
                    LOGGER.error("Service class newInstance has error : {}", ex);
                    throw new NacosRuntimeException(SERVER_ERROR, ex);
                }
            }
        };

        try {
            // Create and init DefaultSharePublisher instance.
            INSTANCE.sharePublisher = new DefaultSharePublisher();
            INSTANCE.sharePublisher.init(SlowEvent.class, shareBufferSize);
        } catch (Throwable ex) {
            LOGGER.error("Service class newInstance has error : {}", ex);
        }

        ThreadUtils.addShutdownHook(new Runnable() {
            @Override
            public void run() {
                shutdown();
            }
        });
    }

    @JustForTest
    public static Map<String, EventPublisher> getPublisherMap() {
        return INSTANCE.publisherMap;
    }

    @JustForTest
    public static EventPublisher getPublisher(Class<? extends Event> topic) {
        if (ClassUtils.isAssignableFrom(SlowEvent.class, topic)) {
            return INSTANCE.sharePublisher;
        }
        return INSTANCE.publisherMap.get(topic.getCanonicalName());
    }

    @JustForTest
    public static EventPublisher getSharePublisher() {
        return INSTANCE.sharePublisher;
    }

    /**
     * Shutdown the serveral publisher instance which notifycenter has.
     */
    public static void shutdown() {
        if (!CLOSED.compareAndSet(false, true)) {
            return;
        }
        LOGGER.warn("[NotifyCenter] Start destroying Publisher");

        for (Map.Entry<String, EventPublisher> entry : INSTANCE.publisherMap.entrySet()) {
            try {
                EventPublisher eventPublisher = entry.getValue();
                eventPublisher.shutdown();
            } catch (Throwable e) {
                LOGGER.error("[EventPublisher] shutdown has error : {}", e);
            }
        }

        try {
            INSTANCE.sharePublisher.shutdown();
        } catch (Throwable e) {
            LOGGER.error("[SharePublisher] shutdown has error : {}", e);
        }

        LOGGER.warn("[NotifyCenter] Destruction of the end");
    }

    public static <T> void registerSubscriber(final Subscriber consumer) {
        final Class<? extends Event> cls = consumer.subscribeType();
        // If you want to listen to multiple events, you do it separately,
        // based on subclass's subscribeTypes method return list, it can register to publisher.
        if (consumer instanceof SmartSubscriber) {
            for (Class<? extends Event> subscribeType : ((SmartSubscriber) consumer).subscribeTypes()) {
                // For case, producer: defaultSharePublisher -> consumer: smartSubscriber.
                if (ClassUtils.isAssignableFrom(SlowEvent.class, subscribeType)) {
                    INSTANCE.sharePublisher.addSubscriber(consumer, subscribeType);
                } else {
                    // For case, producer: defaultPublisher -> consumer: subscriber.
                    addSubscriber(consumer, subscribeType);
                }
            }
            return;
        }

        if (ClassUtils.isAssignableFrom(SlowEvent.class, cls)) {
            INSTANCE.sharePublisher.addSubscriber(consumer, cls);
            return;
        }

        addSubscriber(consumer, consumer.subscribeType());
    }

    private static void addSubscriber(final Subscriber consumer, Class<? extends Event> subscribeType) {
        final String topic = ClassUtils.getCanonicalName(subscribeType);
        synchronized (NotifyCenter.class) {
            // MapUtils.computeIfAbsent is a unsafe method.
            MapUtils.computeIfAbsent(INSTANCE.publisherMap, topic, publisherFactory, subscribeType, ringBufferSize);
        }
        EventPublisher publisher = INSTANCE.publisherMap.get(topic);
        publisher.addSubscriber(consumer);
    }

    public static <T> void deregisterSubscriber(final Subscriber consumer) {
        final Class<? extends Event> cls = consumer.subscribeType();
        if (consumer instanceof SmartSubscriber) {
            for (Class<? extends Event> subscribeType : ((SmartSubscriber) consumer).subscribeTypes()) {
                if (ClassUtils.isAssignableFrom(SlowEvent.class, subscribeType)) {
                    INSTANCE.sharePublisher.removeSubscriber(consumer, subscribeType);
                } else {
                    removeSubscriber(consumer, subscribeType);
                }
            }
            return;
        }

        if (ClassUtils.isAssignableFrom(SlowEvent.class, cls)) {
            INSTANCE.sharePublisher.removeSubscriber(consumer, cls);
            return;
        }

        if (removeSubscriber(consumer, consumer.subscribeType())) {
            return;
        }
        throw new NoSuchElementException("The subcriber has no event publisher");
    }

    /**
     * Remove subscriber.
     *
     * @param consumer      subscriber instance.
     * @param subscribeType subscribeType.
     * @return whether remove subscriber successfully or not.
     */
    private static boolean removeSubscriber(final Subscriber consumer, Class<? extends Event> subscribeType) {

        final String topic = ClassUtils.getCanonicalName(subscribeType);
        if (INSTANCE.publisherMap.containsKey(topic)) {
            EventPublisher publisher = INSTANCE.publisherMap.get(topic);
            publisher.removeSubscriber(consumer);
            return true;
        }

        return false;
    }

    /**
     * Request publisher publish event Publishers load lazily, calling publisher. Start () only when the event is
     * actually published.
     *
     * @param event class Instances of the event.
     */
    public static boolean publishEvent(final Event event) {
        try {
            return publishEvent(event.getClass(), event);
        } catch (Throwable ex) {
            LOGGER.error("There was an exception to the message publishing : {}", ex);
            return false;
        }
    }

    /**
     * Request publisher publish event Publishers load lazily, calling publisher.
     *
     * @param eventType class Instances type of the event type.
     * @param event     event instance.
     */
    private static boolean publishEvent(final Class<? extends Event> eventType, final Event event) {
        final String topic = ClassUtils.getCanonicalName(eventType);
        if (ClassUtils.isAssignableFrom(SlowEvent.class, eventType)) {
            return INSTANCE.sharePublisher.publish(event);
        }

        if (INSTANCE.publisherMap.containsKey(topic)) {
            EventPublisher publisher = INSTANCE.publisherMap.get(topic);
            return publisher.publish(event);
        }

        LOGGER.warn("There are no [{}] publishers for this event, please register", topic);
        return false;
    }

    public static EventPublisher registerToSharePublisher(final Class<? extends SlowEvent> eventType) {
        return INSTANCE.sharePublisher;
    }

    public static EventPublisher registerToPublisher(final Class<? extends Event> eventType, final int queueMaxSize) {
        if (ClassUtils.isAssignableFrom(SlowEvent.class, eventType)) {
            return INSTANCE.sharePublisher;
        }

        final String topic = ClassUtils.getCanonicalName(eventType);
        synchronized (NotifyCenter.class) {
            // MapUtils.computeIfAbsent is a unsafe method.
            MapUtils.computeIfAbsent(INSTANCE.publisherMap, topic, publisherFactory, eventType, queueMaxSize);
        }
        return INSTANCE.publisherMap.get(topic);
    }

    /**
     * Deregister publisher.
     *
     * @param eventType class Instances type of the event type.
     */
    public static void deregisterPublisher(final Class<? extends Event> eventType) {
        final String topic = ClassUtils.getCanonicalName(eventType);
        EventPublisher publisher = INSTANCE.publisherMap.remove(topic);
        try {
            publisher.shutdown();
        } catch (Throwable ex) {
            LOGGER.error("There was an exception when publisher shutdown : {}", ex);
        }
    }

}
