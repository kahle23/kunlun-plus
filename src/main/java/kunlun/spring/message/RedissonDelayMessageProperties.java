package kunlun.spring.message;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 基于 Redisson 的延迟消息配置.
 * @author Kahle
 */
@ConfigurationProperties("kunlun.message.redisson.delay")
public class RedissonDelayMessageProperties {
    /**
     * 启用基于 Redisson 的延时消息.
     */
    private Boolean enabled;
    /**
     * 理论上没啥用（不是你们想象中的那个忽略），备用，默认值 false，别随便改，保持默认.
     */
    private Boolean ignoreException;
    /**
     * 当监听器的线程池“满了”的时候，Redisson 的订阅线程的休眠时间（范围：500毫秒-4000毫秒，默认值：500毫秒）.
     */
    private Long    sleepTimeWhenRejected;
    /**
     * 监听器处理时的线程池名称前缀.
     */
    private String  threadNamePrefix = "delay-msg-thread";
    /**
     * 监听器处理时的线程池的核心线程数量.
     */
    private Integer corePoolSize  = 2;
    /**
     * 监听器处理时的线程池的最大线程数量.
     */
    private Integer maxPoolSize   = 16;
    /**
     * 监听器处理时的线程池的队列数量.
     */
    private Integer queueCapacity = 16;
    /**
     * 监听器处理时的线程池的最大线程存活时间（秒）.
     */
    private Integer keepAliveSeconds = 20 * 60;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getIgnoreException() {
        return ignoreException;
    }

    public void setIgnoreException(Boolean ignoreException) {
        this.ignoreException = ignoreException;
    }

    public Long getSleepTimeWhenRejected() {
        return sleepTimeWhenRejected;
    }

    public void setSleepTimeWhenRejected(Long sleepTimeWhenRejected) {
        this.sleepTimeWhenRejected = sleepTimeWhenRejected;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Integer getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(Integer queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public Integer getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(Integer keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }
}
