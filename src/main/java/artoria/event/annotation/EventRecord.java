package artoria.event.annotation;

import java.lang.annotation.*;

/**
 * Event record.
 * If you want to desensitize the input or output parameters.
 * You can desensitize the data before saving it to the database.
 * Because data desensitization is complex and biased towards business logic.
 * So there will be no data desensitization support here.
 * @author Kahle
 */
@Deprecated // TODO: Deletable
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventRecord {

    /**
     * EventRecord code.
     */
    String code();

    /**
     * EventRecord input.
     */
    boolean input() default false;

    /**
     * EventRecord output.
     */
    boolean output() default false;

    /**
     * Whether to record the corresponding data.
     * @return True or false
     */
    boolean record() default true;

}
