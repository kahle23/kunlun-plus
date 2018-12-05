package artoria.document.common;

/**
 * URI resolver.
 * @author Kahle
 */
public interface URIResolver {

    /**
     * Returns the resolved uri.
     * @param uri Image path, is not full path
     * @return Resolved uri
     */
    String resolve(String uri);

}
