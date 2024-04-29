/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support.data.fill;

import kunlun.action.support.AutoActionHandler;
import kunlun.util.Assert;

/**
 * The abstract auto single field fill action handler.
 * @author Kahle
 */
public abstract class AbstractAutoSingleFieldFillHandler
        extends AbstractSingleFieldFillHandler implements AutoActionHandler {
    private final String actionName;

    public AbstractAutoSingleFieldFillHandler(String actionName) {
        Assert.notBlank(actionName, "Parameter \"actionName\" must not blank. ");
        this.actionName = actionName;
    }

    @Override
    public String getName() {

        return actionName;
    }

}
