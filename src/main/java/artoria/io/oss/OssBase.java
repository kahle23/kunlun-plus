package artoria.io.oss;

import artoria.core.Resource;

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
