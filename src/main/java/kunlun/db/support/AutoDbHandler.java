/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.db.support;

import kunlun.db.DbHandler;

public interface AutoDbHandler extends DbHandler {

    /**
     * Get the database handler name.
     * @return The database handler name
     */
    String getName();

}
