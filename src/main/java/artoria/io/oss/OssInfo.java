package artoria.io.oss;

/**
 * The information about the object storage service.
 * @author Kahle
 */
public interface OssInfo extends OssBase {

    /**
     * Get the object url.
     * @return The object url
     */
    String getObjectUrl();

    /**
     * Get the original object.
     * @return The original object
     */
    Object getOriginal();

}
