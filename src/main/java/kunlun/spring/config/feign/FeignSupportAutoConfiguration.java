/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.config.feign;

import feign.RequestInterceptor;
import kunlun.spring.feign.FeignRequestTransferInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The feign support auto configuration.
 * @author Kahle
 */
@Configuration
@ConditionalOnClass({RequestInterceptor.class})
@EnableConfigurationProperties({FeignSupportProperties.class})
@ConditionalOnProperty(name = "spring.extension.feign.request-transfer", havingValue = "true")
public class FeignSupportAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(FeignSupportAutoConfiguration.class);

    @Bean
    public RequestInterceptor requestInterceptor(){
        RequestInterceptor requestInterceptor = new FeignRequestTransferInterceptor();
        log.info("The feign request transfer interceptor was initialized success. ");
        return requestInterceptor;
    }

}
