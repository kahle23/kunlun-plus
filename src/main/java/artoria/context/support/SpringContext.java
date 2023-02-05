package artoria.context.support;

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
