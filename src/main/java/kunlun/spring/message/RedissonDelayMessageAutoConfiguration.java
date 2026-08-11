package kunlun.spring.message;

import kunlun.action.ActionUtil;
import kunlun.action.message.support.redisson.AbstractDelayMessageListener;
import kunlun.action.message.support.redisson.RedissonDelayMessageHandler;
import kunlun.message.model.DelayMessage;
import kunlun.message.model.Subscribe;
import kunlun.util.Assert;
import kunlun.util.concurrent.SimpleThreadFactory;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 基于 Redisson 的延迟消息自动化配置.
 * @author Kahle
 */
@Configuration
@EnableConfigurationProperties({RedissonDelayMessageProperties.class})
@ConditionalOnProperty(name = "kunlun.message.redisson.delay.enabled", havingValue = "true")
public class RedissonDelayMessageAutoConfiguration implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(RedissonDelayMessageAutoConfiguration.class);

    @Resource
    private RedissonDelayMessageProperties delayMsgProperties;
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 必要的参数校验、变量获取
        Assert.notNull(delayMsgProperties, "Parameter \"delayMsgProperties\" must not null. ");
        Assert.notNull(applicationContext, "Parameter \"applicationContext\" must not null. ");
        Assert.notNull(redissonClient, "Parameter \"redissonClient\" must not null. ");
        Long sleepTimeWhenRejected = delayMsgProperties.getSleepTimeWhenRejected();
        Boolean ignoreException = delayMsgProperties.getIgnoreException();
        String threadNamePrefix = delayMsgProperties.getThreadNamePrefix();
        Integer corePoolSize = delayMsgProperties.getCorePoolSize();
        Integer maxPoolSize = delayMsgProperties.getMaxPoolSize();
        Integer queueCapacity = delayMsgProperties.getQueueCapacity();
        Integer keepAliveSeconds = delayMsgProperties.getKeepAliveSeconds();
        // 根据配置创建线程池
        ThreadFactory threadFactory = new SimpleThreadFactory(threadNamePrefix, Boolean.FALSE);
        ExecutorService threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
                keepAliveSeconds, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(queueCapacity), threadFactory);
        // 初始化延时消息处理器，并进行注册
        RedissonDelayMessageHandler handler = new RedissonDelayMessageHandler(
                redissonClient, threadPool, sleepTimeWhenRejected, ignoreException);
        ActionUtil.registerAction("delay-msg-redis", handler);
        ActionUtil.registerShortcut(DelayMessage.class, "delay-msg-redis");
        // 订阅延时消息的监听器
        Map<String, AbstractDelayMessageListener> beansOfType =
                applicationContext.getBeansOfType(AbstractDelayMessageListener.class);
        for (Map.Entry<String, AbstractDelayMessageListener> entry : beansOfType.entrySet()) {
            AbstractDelayMessageListener value = entry.getValue();
            String key = entry.getKey();
            String topic = value.getTopic();
            Assert.notBlank(topic, "名称为“" + key + "”的 bean 的 topic 为空！");
            handler.subscribe(Subscribe.Builder.of(topic).setMessageListener(value).build());
            log.info("The topic \"{}\" is subscribed to \"{}\". ", topic, value);
        }
    }

}
