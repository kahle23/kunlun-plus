/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.validation.support;

import kunlun.data.validation.AutoValidator;
import kunlun.util.Assert;

/**
 * The abstract automated validator.
 * @author Kahle
 */
public abstract class AbstractAutoValidator implements AutoValidator {
    private final String name;

    public AbstractAutoValidator(String name) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        this.name = name;
    }

    @Override
    public String getName() {

        return name;
    }

}
