/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.context;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * The context object with a spring context.
 * @author Kahle
 */
public interface SpringContext {

    /**
     * Get spring context.
     * @return The spring context
     */
    ApplicationContext getSpringContext();

    /**
     * Get spring environment.
     * @return The spring environment
     */
    Environment getEnvironment();

}
