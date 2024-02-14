package artoria.message.delay.support;

import artoria.data.tuple.Pair;
import artoria.exception.ExceptionUtils;
import artoria.message.MessageListener;
import artoria.message.delay.DelayMessage;
import artoria.message.handler.AbstractClassicMessageHandler;
import artoria.time.DateUtils;
import artoria.util.Assert;
import artoria.util.ThreadUtils;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * The redisson delay message handler.
 * @author Kahle
 */
public class RedissonDelayMessageHandler extends AbstractClassicMessageHandler {
    protected static Logger log = LoggerFactory.getLogger(RedissonDelayMessageHandler.class);
    protected static final Long DEFAULT_SLEEP_TIME = 500L;
    protected final Boolean ignoreException;
    protected final Long    sleepTimeWhenRejected;
    protected Map<String, QueuePair> delayedQueues;
    protected ExecutorService executorService;
    protected RedissonClient redissonClient;

    public RedissonDelayMessageHandler(RedissonClient redissonClient,
                                       ExecutorService executorService) {

        this(redissonClient, executorService, null, null);
    }

    public RedissonDelayMessageHandler(RedissonClient redissonClient,
                                       ExecutorService executorService,
                                       Long sleepTimeWhenRejected,
                                       Boolean ignoreException) {
        this(redissonClient, executorService, sleepTimeWhenRejected
                , ignoreException, new ConcurrentHashMap<String, QueuePair>());
    }

    protected RedissonDelayMessageHandler(RedissonClient redissonClient,
                                          ExecutorService executorService,
                                          Long sleepTimeWhenRejected,
                                          Boolean ignoreException,
                                          Map<String, QueuePair> delayedQueues) {
        Assert.notNull(executorService, "Parameter \"executorService\" must not null. ");
        Assert.notNull(redissonClient, "Parameter \"redissonClient\" must not null. ");
        Assert.notNull(delayedQueues, "Parameter \"delayedQueues\" must not null. ");
        if (ignoreException == null) { ignoreException = false; }
        if (sleepTimeWhenRejected == null) { sleepTimeWhenRejected = DEFAULT_SLEEP_TIME; }
        sleepTimeWhenRejected = sleepTimeWhenRejected > 4000 ? 4000 : sleepTimeWhenRejected;
        sleepTimeWhenRejected = sleepTimeWhenRejected < 100 ? 100 : sleepTimeWhenRejected;
        this.sleepTimeWhenRejected = sleepTimeWhenRejected;
        this.ignoreException = ignoreException;
        this.executorService = executorService;
        this.redissonClient = redissonClient;
        this.delayedQueues = delayedQueues;
    }

    protected QueuePair getQueuePair(String topic) {
        Assert.notBlank(topic, "Parameter \"topic\" must not blank. ");
        QueuePair pair = delayedQueues.get(topic);
        if (pair != null) { return pair; }
        synchronized (topic.intern()) {
            if ((pair = delayedQueues.get(topic)) != null) { return pair; }
            RBlockingDeque<Object> blockingDeque = redissonClient.getBlockingDeque(topic);
            RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
            delayedQueues.put(topic, pair = new QueuePair(delayedQueue, blockingDeque));
            return pair;
        }
    }

    @Override
    public Object send(Object message, Type type) {
        Assert.notNull(message, "Parameter \"message\" must not null. ");
        Assert.isInstanceOf(DelayMessage.class, message);
        DelayMessage delayMsg = (DelayMessage) message;
        delayMsg.setTimestamp(DateUtils.getTimeInMillis());
        TimeUnit timeUnit = delayMsg.getTimeUnit();
        Long delayTime = delayMsg.getDelayTime();
        QueuePair pair = getQueuePair(delayMsg.getTopic());
        pair.getLeft().offer(delayMsg, delayTime, timeUnit);
        return null;
    }

    @Override
    public Object receive(Object condition, Type type) {
        Assert.notNull(condition, "Parameter \"condition\" must not null. ");
        Assert.isInstanceOf(CharSequence.class, condition);
        QueuePair pair = getQueuePair((String) condition);
        return pair.getRight().pollFirst();
    }

    @Override
    public Object subscribe(Object condition, Object messageListener) {
        Assert.notNull(messageListener, "Parameter \"messageListener\" must not null. ");
        Assert.notNull(condition, "Parameter \"condition\" must not null. ");
        Assert.isInstanceOf(MessageListener.class, messageListener);
        Assert.isInstanceOf(CharSequence.class, condition);
        QueuePair pair = getQueuePair((String) condition);
        MessageListener listener = (MessageListener) messageListener;
        MessageConsumer consumer = new MessageConsumer(
                executorService, listener, sleepTimeWhenRejected, ignoreException, pair);
        return pair.getRight().subscribeOnFirstElements(consumer);
    }

    @Override
    public Object operate(Object input, String name, Class<?> clazz) {

        throw new UnsupportedOperationException("This method is not supported! ");
    }

    /**
     * The inner redisson queue pair.
     * @author Kahle
     */
    protected static class QueuePair implements Pair<RDelayedQueue<Object>, RBlockingDeque<Object>> {
        private RBlockingDeque<Object> right;
        private RDelayedQueue<Object> left;
        protected QueuePair(RDelayedQueue<Object> left, RBlockingDeque<Object> right) {
            this.right = right;
            this.left = left;
        }
        @Override
        public RDelayedQueue<Object> getLeft() { return left; }
        @Override
        public RBlockingDeque<Object> getRight() { return right; }
    }

    /**
     * The inner message consumer.
     * @author Kahle
     */
    protected static class MessageConsumer implements Consumer<Object> {
        private static Logger log = LoggerFactory.getLogger(MessageConsumer.class);
        private final Boolean ignoreException;
        private final Long    sleepTimeWhenRejected;
        private ExecutorService executorService;
        private MessageListener messageListener;
        private QueuePair queuePair;
        protected MessageConsumer(ExecutorService executorService,
                                  MessageListener messageListener,
                                  Long sleepTimeWhenRejected,
                                  Boolean ignoreException,
                                  QueuePair queuePair) {
            if (sleepTimeWhenRejected == null) { sleepTimeWhenRejected = DEFAULT_SLEEP_TIME; }
            if (ignoreException == null) { ignoreException = false; }
            this.sleepTimeWhenRejected = sleepTimeWhenRejected;
            this.executorService = executorService;
            this.messageListener = messageListener;
            this.ignoreException = ignoreException;
            this.queuePair = queuePair;
        }
        @Override
        public void accept(final Object message) {
            try {
                // The redisson's subscribe use "takeFirstAsync"'s RFuture (like callback).
                // So another thread pool needs to handle the listener logic.
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        messageListener.onMessage(message);
                    }
                });
            }
            catch (RejectedExecutionException e) {
                log.warn("Submitting a task to the thread pool was rejected. " +
                        "The rejected message is \"{}\". " +
                        "The message object is \"{}\". ", e.getMessage(), message);
                queuePair.getLeft().offerAsync(message, sleepTimeWhenRejected, TimeUnit.MILLISECONDS);
                ThreadUtils.sleepQuietly(sleepTimeWhenRejected);
            }
            catch (Exception e) {
                // If the exception is thrown, the subscription will stop.
                log.error("Message consumer accept error! ", e);
                if (!ignoreException) { throw ExceptionUtils.wrap(e); }
            }
        }
    }

}
