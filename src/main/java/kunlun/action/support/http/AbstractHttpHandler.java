/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support.http;

import kunlun.action.support.AbstractAutoClassicActionHandler;
import kunlun.util.Assert;

import java.lang.reflect.Type;

public abstract class AbstractHttpHandler extends AbstractAutoClassicActionHandler implements HttpHandler {

    public AbstractHttpHandler(String actionName) {

        super(actionName);
    }

    protected abstract Object execute(HttpParameters httpParams);

    @Override
    public <T> T execute(Object input, Class<T> clazz) {
        Assert.isInstanceOf(HttpParameters.class, input
                , "Parameter \"input\" must instance of \"HttpParameters\". ");
        return execute((HttpParameters) input, (Type) clazz);
    }

}
