/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.io.oss;

import java.io.InputStream;

/**
 * The object data in the object storage service.
 * @author Kahle
 */
public interface OssObject extends OssBase {

    /**
     * Get the metadata.
     * @return The metadata
     */
    Object getMetadata();

    /**
     * Get the object content.
     * @return The object content
     */
    InputStream getObjectContent();

}
