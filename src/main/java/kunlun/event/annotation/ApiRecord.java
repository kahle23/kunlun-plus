/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.event.annotation;

import java.lang.annotation.*;

/**
 * Record API access information and print parameters logs.
 * @author Kahle
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiRecord {

    /**
     * The name of the API.
     * @return The name
     */
    String name();

    /**
     * Whether to record the input parameters.
     * @return True or false
     */
    boolean input() default true;

    /**
     * Whether to record the output result.
     * @return True or false
     */
    boolean output() default false;

    /**
     * Whether to print parameters logs.
     * @return True or false
     */
    boolean print() default true;

    /**
     * Whether to print more information.
     * @return True or false
     */
    boolean more() default false;

    /**
     * Whether to record the corresponding data.
     * @return True or false
     */
    boolean record() default true;

}
