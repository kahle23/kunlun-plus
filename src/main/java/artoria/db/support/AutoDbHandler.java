package artoria.db.support;

import artoria.db.DbHandler;

public interface AutoDbHandler extends DbHandler {

    /**
     * Get the database handler name.
     * @return The database handler name
     */
    String getName();

}
