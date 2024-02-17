/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support.http;

import kunlun.action.ActionHandler;

import java.lang.reflect.Type;

public interface HttpHandler extends ActionHandler {

    <T> T execute(HttpParameters httpParams, Type type);

}
