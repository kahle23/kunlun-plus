/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.validation.support;

import kunlun.data.validation.BooleanValidator;

/**
 * The is null validator.
 * @author Kahle
 */
public class IsNullValidator implements BooleanValidator {

    @Override
    public Boolean validate(Object target) {

        return target == null;
    }

}
