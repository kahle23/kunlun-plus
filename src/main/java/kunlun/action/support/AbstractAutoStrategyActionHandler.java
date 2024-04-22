/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support;

import kunlun.util.Assert;

/**
 * The abstract automated strategy action handler.
 * @author Kahle
 */
public abstract class AbstractAutoStrategyActionHandler
        extends AbstractStrategyActionHandler implements AutoActionHandler {
    private final String actionName;

    public AbstractAutoStrategyActionHandler(String actionName) {
        Assert.notBlank(actionName, "Parameter \"actionName\" must not blank. ");
        this.actionName = actionName;
    }

    @Override
    public String getName() {

        return actionName;
    }

}
