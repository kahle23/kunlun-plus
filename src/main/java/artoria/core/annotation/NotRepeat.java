package artoria.core.annotation;

import java.lang.annotation.*;

/**
 * It is used to intercept repeated invokes of methods in concurrent scenarios.
 * @author Kahle
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotRepeat {

    /**
     * The strategy to prevent repeated invokes.
     * @return The strategy name
     */
    String strategy() default "default";

    /**
     * The configuration to prevent repeated invokes.
     * @return The configuration string
     */
    String config() default "lock=default&ignoreFields=&message=Repeat invoke! ";

}
