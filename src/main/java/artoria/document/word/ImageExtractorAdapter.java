package artoria.document.word;

import artoria.util.Assert;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.converter.core.IImageExtractor;
import artoria.document.common.ImageExtractor;

import java.io.IOException;

import static artoria.common.Constants.EMPTY_STRING;

/**
 * Image extractor adapter.
 * @author Kahle
 */
public class ImageExtractorAdapter implements IImageExtractor {
    private static final String WORD_MEDIA = "word/media/";
    private static final String DOCX_PREFIX = "docx_";
    private ImageExtractor extractor;

    public ImageExtractorAdapter(ImageExtractor extractor) {
        Assert.notNull(extractor, "Parameter \"extractor\" must not null. ");
        this.extractor = extractor;
    }

    @Override
    public void extract(String imagePath, byte[] imageData) throws IOException {
        if (imagePath.startsWith(WORD_MEDIA)) {
            // In XHTMLMapper will auto add "word/media/"
            imagePath = StringUtils.replace(imagePath, WORD_MEDIA, EMPTY_STRING);
        }
        imagePath = DOCX_PREFIX + imagePath;
        Assert.notBlank(imagePath, "Parameter \"imagePath\" must not blank. ");
        Assert.notNull(imageData, "Parameter \"imageData\" must not null. ");
        this.extractor.extract(imagePath, imageData);
    }

}
