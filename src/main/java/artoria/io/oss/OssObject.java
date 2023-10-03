package artoria.io.oss;

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
