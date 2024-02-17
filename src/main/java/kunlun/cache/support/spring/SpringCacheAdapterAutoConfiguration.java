/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.cache.support.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "kunlun.cache.spring-adapter", havingValue = "true")
@ConditionalOnMissingBean(org.springframework.cache.CacheManager.class)
public class SpringCacheAdapterAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(SpringCacheAdapterAutoConfiguration.class);

    @Bean
    public CacheManager cacheManager() {

        return new SpringCacheManagerAdapter();
    }

}
