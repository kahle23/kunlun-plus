/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.config.async;

import kunlun.util.Assert;
import kunlun.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * The spring async support auto configuration.
 * @author Kahle
 * @see org.springframework.scheduling.annotation.AsyncConfigurer
 */
@EnableAsync
@Configuration
@ConditionalOnProperty(name = "spring.extension.async.enabled", havingValue = "true")
@EnableConfigurationProperties({SpringAsyncProperties.class})
public class SpringAsyncConfigurerSupport extends AsyncConfigurerSupport {
    private static final Logger log = LoggerFactory.getLogger(SpringAsyncConfigurerSupport.class);
    private final SpringAsyncProperties springAsyncProperties;

    @Autowired
    public SpringAsyncConfigurerSupport(SpringAsyncProperties springAsyncProperties) {
        Assert.notNull(springAsyncProperties, "Parameter \"springAsyncProperties\" must not null. ");
        this.springAsyncProperties = springAsyncProperties;
        log.info("The spring async support was initialized success. ");
    }

    @Override
    public Executor getAsyncExecutor() {
        // Get the parameters.
        SpringAsyncProperties.RejectPolicy rejectPolicy = springAsyncProperties.getRejectPolicy();
        String  threadNamePrefix = springAsyncProperties.getThreadNamePrefix();
        Integer corePoolSize = springAsyncProperties.getCorePoolSize();
        Integer maxPoolSize = springAsyncProperties.getMaxPoolSize();
        Integer queueCapacity = springAsyncProperties.getQueueCapacity();
        Integer keepAliveSeconds = springAsyncProperties.getKeepAliveSeconds();
        // Build thread pool.
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        if (StringUtils.isNotBlank(threadNamePrefix)) {
            executor.setThreadNamePrefix(threadNamePrefix);
        }
        if (corePoolSize != null) {
            executor.setCorePoolSize(corePoolSize);
        }
        if (maxPoolSize != null) {
            executor.setMaxPoolSize(maxPoolSize);
        }
        if (queueCapacity != null) {
            executor.setQueueCapacity(queueCapacity);
        }
        if (keepAliveSeconds != null) {
            executor.setKeepAliveSeconds(keepAliveSeconds);
        }
        if (rejectPolicy != null) {
            executor.setRejectedExecutionHandler(rejectPolicy.getRejectedExecutionHandler());
        }
        executor.initialize();
        // End.
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {

        return new SimpleAsyncExceptionHandler();
    }

    /**
     * The simple async uncaught exception handler.
     * @author Kahle
     */
    public static class SimpleAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        private static final Logger log = LoggerFactory.getLogger(SimpleAsyncExceptionHandler.class);
        @Override
        public void handleUncaughtException(@NonNull Throwable ex,
                                            @NonNull Method method,
                                            @NonNull Object... params) {
            log.error("The async method \"{}\" occurred an error. ", method.getName(), ex);
        }
    }

}
