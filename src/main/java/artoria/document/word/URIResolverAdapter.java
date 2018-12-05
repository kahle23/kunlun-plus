package artoria.document.word;

import artoria.util.Assert;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.converter.core.IURIResolver;
import artoria.document.common.URIResolver;

import static artoria.common.Constants.EMPTY_STRING;

/**
 * URI resolver adapter.
 * @author Kahle
 */
public class URIResolverAdapter implements IURIResolver {
    private static final String WORD_MEDIA = "word/media/";
    private static final String DOCX_PREFIX = "docx_";
    private URIResolver resolver;

    public URIResolverAdapter(URIResolver resolver) {
        Assert.notNull(resolver, "Parameter \"resolver\" must not null. ");
        this.resolver = resolver;
    }

    @Override
    public String resolve(String uri) {
        if (uri.startsWith(WORD_MEDIA)) {
            // In XHTMLMapper will auto add "word/media/"
            uri = StringUtils.replace(uri, WORD_MEDIA, EMPTY_STRING);
        }
        uri = DOCX_PREFIX + uri;
        Assert.notBlank(uri, "Parameter \"uri\" must not blank. ");
        return this.resolver.resolve(uri);
    }

}
