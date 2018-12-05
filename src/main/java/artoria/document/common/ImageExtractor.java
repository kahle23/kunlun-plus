package artoria.document.common;

import java.io.IOException;

/**
 * Image extractor.
 * @author Kahle
 */
public interface ImageExtractor {

    /**
     * Extract the given image data with the given image path.
     * @param imagePath Image path, is not full path
     * @param imageData Image data
     * @throws IOException Maybe will happen io error
     */
    void extract(String imagePath, byte[] imageData) throws IOException;

}
