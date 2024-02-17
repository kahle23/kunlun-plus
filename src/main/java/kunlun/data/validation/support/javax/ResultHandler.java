/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.validation.support.javax;

import kunlun.core.Handler;

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
