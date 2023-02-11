package artoria.data.validation.support.javax;

import artoria.core.Handler;

/**
 * The result handler of the javax validator.
 * @author Kahle
 * @see javax.validation.Validator
 */
public interface ResultHandler extends Handler {

    /**
     * Performs this operation on the given arguments.
     *
     * @param result the first input argument
     * @return The result of handling
     */
    Object handle(Object result);

}
