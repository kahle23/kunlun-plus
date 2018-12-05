package artoria.document.common;

import artoria.exception.ExceptionUtils;
import artoria.document.DocumentProcessException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Abstract converter, and will implement all converter interface.
 * @author Kahle
 */
public abstract class AbstractConverter implements ToHtmlConverter {

    public String convertToHtmlString(InputStream fileInputStream, String charset
            , String imageUrlPrefix, File imageSaveBaseDir) {
        ImageExtractor extractor = new DefaultImageExtractor(imageSaveBaseDir);
        URIResolver resolver = new DefaultURIResolver(imageUrlPrefix);
        return this.convertToHtmlString(fileInputStream, charset, resolver, extractor);
    }

    public String convertToHtmlString(InputStream fileInputStream, String charset
            , URIResolver resolver, ImageExtractor extractor) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            this.convertToHtml(fileInputStream, out, charset, resolver, extractor);
            return new String(out.toByteArray(), charset);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e, DocumentProcessException.class);
        }
    }

    public void convertToHtml(InputStream fileInputStream, OutputStream htmlOutputStream
            , String charset, String imageUrlPrefix, File imageSaveBaseDir) {
        try {
            ImageExtractor extractor = new DefaultImageExtractor(imageSaveBaseDir);
            URIResolver resolver = new DefaultURIResolver(imageUrlPrefix);
            this.convertToHtml(fileInputStream, htmlOutputStream, charset, resolver, extractor);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e, DocumentProcessException.class);
        }
    }

}
