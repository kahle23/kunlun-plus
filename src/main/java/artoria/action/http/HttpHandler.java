package artoria.action.http;

import artoria.action.ActionHandler;

import java.lang.reflect.Type;

public interface HttpHandler extends ActionHandler {

    <T> T execute(HttpParameters httpParams, Type type);

}
