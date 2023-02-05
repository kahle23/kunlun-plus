package artoria.action.http;

import artoria.action.handler.AbstractAutoClassicActionHandler;
import artoria.util.Assert;

import java.lang.reflect.Type;

public abstract class AbstractHttpHandler extends AbstractAutoClassicActionHandler implements HttpHandler {

    public AbstractHttpHandler(String actionName, Class<?> inputType) {

        super(actionName, inputType);
    }

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
