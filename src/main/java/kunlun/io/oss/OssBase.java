/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.io.oss;

import kunlun.core.Resource;

/**
 * The base information about the object storage service.
 * @author Kahle
 */
public interface OssBase extends Resource {

    /**
     * Get the bucket name.
     * @return The bucket name
     */
    String getBucketName();

    /**
     * Get the object key.
     * @return The object key
     */
    String getObjectKey();

}
