package artoria.document.word;

import artoria.exception.ExceptionUtils;
import artoria.util.Assert;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.usermodel.PictureType;
import artoria.document.DocumentProcessException;
import artoria.document.common.ImageExtractor;
import artoria.document.common.URIResolver;

import java.io.IOException;

/**
 * Pictures manager adapter.
 * @author Kahle
 */
public class PicturesManagerAdapter implements PicturesManager {
    private static final String DOC_PREFIX = "doc_image_";
    private URIResolver resolver;
    private ImageExtractor extractor;

    public PicturesManagerAdapter(URIResolver resolver, ImageExtractor extractor) {
        Assert.notNull(resolver, "Parameter \"resolver\" must not null. ");
        Assert.notNull(extractor, "Parameter \"extractor\" must not null. ");
        this.resolver = resolver;
        this.extractor = extractor;
    }

    @Override
    public String savePicture(byte[] content, PictureType pictureType
            , String suggestedName, float widthInches, float heightInches) {
        try {
            suggestedName = DOC_PREFIX + suggestedName;
            Assert.notBlank(suggestedName, "Parameter \"suggestedName\" must not blank. ");
            Assert.notNull(content, "Parameter \"content\" must not null. ");
            this.extractor.extract(suggestedName, content);
            return this.resolver.resolve(suggestedName);
        }
        catch (IOException e) {
            throw ExceptionUtils.wrap(e, DocumentProcessException.class);
        }
    }

}
