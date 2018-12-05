package artoria.document.pdf;

import artoria.util.Assert;
import artoria.util.StringUtils;
import org.fit.pdfdom.resource.HtmlResource;
import org.fit.pdfdom.resource.HtmlResourceHandler;
import artoria.document.common.ImageExtractor;
import artoria.document.common.URIResolver;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static artoria.common.Constants.*;

/**
 * Html resource handler adapter.
 * @author Kahle
 */
public class HtmlResourceHandlerAdapter implements HtmlResourceHandler {
    private static final String STRING_IMAGE = "image";
    private List<String> writtenFileNames = new LinkedList<String>();
    private URIResolver resolver;
    private ImageExtractor extractor;

    public HtmlResourceHandlerAdapter(URIResolver resolver, ImageExtractor extractor) {
        Assert.notNull(resolver, "Parameter \"resolver\" must not null. ");
        Assert.notNull(extractor, "Parameter \"extractor\" must not null. ");
        this.resolver = resolver;
        this.extractor = extractor;
    }

    @Override
    public String handleResource(HtmlResource resource) throws IOException {
        Assert.notNull(resource, "Parameter \"resource\" must not null. ");
        String fileName = this.findNextUnusedFileName(resource.getName());
        String imagePath = fileName + DOT + resource.getFileEnding();
        byte[] imageData = resource.getData();
        Assert.notNull(imageData, "Parameter \"imageData\" must not null. ");
        this.extractor.extract(imagePath, imageData);
        String resolve = this.resolver.resolve(imagePath);
        this.writtenFileNames.add(fileName);
        return resolve;
    }

    private String findNextUnusedFileName(String fileName) {
        Assert.notBlank(fileName, "Parameter \"fileName\" must not blank. ");
        fileName = StringUtils.replace(fileName, BLANK_SPACE, UNDERLINE);
        fileName = fileName.toLowerCase();
        if (!fileName.endsWith(STRING_IMAGE)) {
            fileName = fileName.endsWith(UNDERLINE) ? fileName : fileName + UNDERLINE;
            fileName += STRING_IMAGE;
        }
        int i = 0;
        String usedName = fileName + i;
        for(; this.writtenFileNames.contains(usedName); i++) {
            usedName = fileName + i;
        }
        return usedName;
    }

}
