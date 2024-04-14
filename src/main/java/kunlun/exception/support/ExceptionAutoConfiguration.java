/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.exception.support;

import kunlun.exception.ServletErrorHandler;
import kunlun.exception.support.spring.ServletErrorHandlerErrorController;
import kunlun.exception.support.spring.ServletErrorHandlerExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The exception auto-configuration.
 * @author Kahle
 */
@Configuration
@AutoConfigureBefore({ErrorMvcAutoConfiguration.class})
@EnableConfigurationProperties({ExceptionProperties.class})
@ConditionalOnProperty(name = "kunlun.exception.enabled", havingValue = "true")
@Import({ServletErrorHandlerExceptionHandler.class, ServletErrorHandlerErrorController.class})
public class ExceptionAutoConfiguration /*implements InitializingBean, DisposableBean*/ {
    private static final Logger log = LoggerFactory.getLogger(ExceptionAutoConfiguration.class);
    private final ServletErrorHandler errorHandler;

    @Autowired
    public ExceptionAutoConfiguration(ApplicationContext context, ExceptionProperties properties) {
        Boolean internalErrorPage = properties.getInternalErrorPage();
        String  baseTemplatePath = properties.getBaseTemplatePath();
        errorHandler = new SimpleServletErrorHandler(internalErrorPage, baseTemplatePath);
    }

    //@Override
    //public void afterPropertiesSet() throws Exception {}
    //@Override
    //public void destroy() throws Exception {}

    @Bean
    @ConditionalOnMissingBean
    public ServletErrorHandler errorHandler() {

        return errorHandler;
    }

}
