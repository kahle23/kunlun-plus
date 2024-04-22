/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support;

import kunlun.util.Assert;

/**
 * The abstract automated classic action handler.
 * @author Kahle
 */
public abstract class AbstractAutoClassicActionHandler
        extends AbstractClassicActionHandler implements AutoActionHandler {
    private final String actionName;

    public AbstractAutoClassicActionHandler(String actionName) {
        Assert.notBlank(actionName, "Parameter \"actionName\" must not blank. ");
        this.actionName = actionName;
    }

    @Override
    public String getName() {

        return actionName;
    }

}
