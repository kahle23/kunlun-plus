package artoria.document.common;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Convert file to html converter.
 * @author Kahle
 */
public interface ToHtmlConverter {

    /**
     * Convert file InputStream to html OutputStream.
     * @param fileInputStream Be converted file's InputStream
     * @param htmlOutputStream Html file OutputStream
     * @param charset Html file charset
     * @param resolver If have image, the image uri will be handle by resolver
     * @param extractor If have image, the image will be save by extractor
     * @throws Exception Have more Exception may happen
     */
    void convertToHtml(InputStream fileInputStream, OutputStream htmlOutputStream
            , String charset, URIResolver resolver, ImageExtractor extractor) throws Exception;

}
