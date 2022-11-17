package artoria.spring.config.async;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * The spring async support configuration.
 * @author Kahle
 */
@ConfigurationProperties("spring.extension.async")
public class SpringAsyncProperties {
    private Boolean enabled;
    private String  threadNamePrefix;
    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer queueCapacity;
    private Integer keepAliveSeconds;
    private RejectPolicy rejectPolicy;

    public Boolean getEnabled() {

        return enabled;
    }

    public void setEnabled(Boolean enabled) {

        this.enabled = enabled;
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

    public RejectPolicy getRejectPolicy() {

        return rejectPolicy;
    }

    public void setRejectPolicy(RejectPolicy rejectPolicy) {

        this.rejectPolicy = rejectPolicy;
    }

    /**
     * The thread pool reject policy.
     * @author Kahle
     */
    public enum RejectPolicy {
        /**
         * The abort policy.
         */
        ABORT(new ThreadPoolExecutor.AbortPolicy()),
        /**
         * The discard policy.
         */
        DISCARD(new ThreadPoolExecutor.DiscardPolicy()),
        /**
         * The discard oldest policy.
         */
        DISCARD_OLDEST(new ThreadPoolExecutor.DiscardOldestPolicy()),
        /**
         * The caller runs policy.
         */
        CALLER_RUNS(new ThreadPoolExecutor.CallerRunsPolicy()),
        ;

        private RejectedExecutionHandler rejectedExecutionHandler;

        RejectPolicy(RejectedExecutionHandler rejectedExecutionHandler) {

            this.rejectedExecutionHandler = rejectedExecutionHandler;
        }

        public RejectedExecutionHandler getRejectedExecutionHandler() {

            return rejectedExecutionHandler;
        }

    }

}
