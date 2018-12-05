package artoria.document.common;

import artoria.util.Assert;

import static artoria.common.Constants.SLASH;

/**
 * Default URI resolver.
 * @author Kahle
 */
public class DefaultURIResolver implements URIResolver {
    private final String baseURL;

    public DefaultURIResolver(String baseURL) {
        Assert.notNull(baseURL, "Parameter \"baseURL\" must not null. ");
        if (!baseURL.endsWith(SLASH)) {
            baseURL += SLASH;
        }
        this.baseURL = baseURL;
    }

    @Override
    public String resolve(String uri) {
        if (uri.startsWith(SLASH)) {
            return this.baseURL + uri.substring(1, uri.length());
        }
        return this.baseURL + uri;
    }

}
