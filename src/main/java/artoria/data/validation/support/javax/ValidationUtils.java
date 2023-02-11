package artoria.data.validation.support.javax;

import artoria.util.Assert;
import artoria.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static artoria.util.ObjectUtils.cast;

/**
 * The javax validator based validation tools.
 * @author Kahle
 * @see javax.validation.Validator
 */
public class ValidationUtils {
    private static final Logger log = LoggerFactory.getLogger(ValidationUtils.class);
    private static volatile ResultHandler resultHandler;
    private static volatile Validator validator;

    public static Validator getValidator() {
        if (validator != null) { return validator; }
        synchronized (ValidationUtils.class) {
            if (validator != null) { return validator; }
            ValidationUtils.setValidator(Validation
                    .buildDefaultValidatorFactory().getValidator());
            return validator;
        }
    }

    public static void setValidator(Validator validator) {
        Assert.notNull(validator, "Parameter \"validator\" must not null. ");
        log.info("Set javax validator: {}", validator.getClass().getName());
        ValidationUtils.validator = validator;
    }

    public static ResultHandler getResultHandler() {
        if (resultHandler != null) { return resultHandler; }
        synchronized (ValidationUtils.class) {
            if (resultHandler != null) { return resultHandler; }
            ValidationUtils.setResultHandler(new InnerResultHandler());
            return resultHandler;
        }
    }

    public static void setResultHandler(ResultHandler handler) {
        Assert.notNull(handler, "Parameter \"handler\" must not null. ");
        log.info("Set result handler: {}", handler.getClass().getName());
        ValidationUtils.resultHandler = handler;
    }

    public static <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {

        return getValidator().validate(object, groups);
    }

    public static <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName,
                                                                   Class<?>... groups) {
        return getValidator().validateProperty(object, propertyName, groups);
    }

    public static <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName,
                                                                Object value, Class<?>... groups) {
        return getValidator().validateValue(beanType, propertyName, value, groups);
    }

    public static <T> void validateToThrow(T object, Class<?>... groups) {

        getResultHandler().handle(validate(object, groups));
    }

    public static <T> void validatePropertyToThrow(T object, String propertyName,
                                                                   Class<?>... groups) {
        getResultHandler().handle(validateProperty(object, propertyName, groups));
    }

    public static <T> void validateValueToThrow(Class<T> beanType, String propertyName,
                                                                Object value, Class<?>... groups) {
        getResultHandler().handle(validateValue(beanType, propertyName, value, groups));
    }

    /**
     * The inner result handler of the javax validator.
     * @author Kahle
     */
    public static class InnerResultHandler implements ResultHandler {
        @Override
        public Object handle(Object result) {
            Set<ConstraintViolation<?>> set = cast(result);
            if (CollectionUtils.isEmpty(set)) {
                return null;
            }
            throw new ConstraintViolationException(set);
        }
    }

}
