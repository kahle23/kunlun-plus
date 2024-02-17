/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.io.oss;

import kunlun.io.DataStorage;

/**
 * The storage interface of the object storage service
 * @author Kahle
 */
public interface OssStorage extends DataStorage {

    /**
     * Get the native storage object.
     * @return The native storage object
     */
    Object getNative();

    /**
     * Obtain the oss object based on the resource information (key).
     * @param key The resource information (key)
     * @return The oss object
     */
    @Override
    OssObject get(Object key);

    /**
     * Put the data.
     * @param data The data
     * @return The oss information or null
     */
    @Override
    OssInfo put(Object data);

}
