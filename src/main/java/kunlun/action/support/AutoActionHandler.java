/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support;

import kunlun.action.ActionHandler;

/**
 * The automated action handler.
 * @author Kahle
 */
public interface AutoActionHandler extends ActionHandler {

    /**
     * Get the action handler name.
     * @return The action name
     */
    String getName();

}
