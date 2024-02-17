/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.validation;

public interface AutoValidator extends Validator {

    /**
     * Return the validator name.
     * @return The validator name
     */
    String getName();

}
