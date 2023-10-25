package artoria.data.desensitize;

/**
 * Provide a high-level abstract of the desensitize tools.
 * @author Kahle
 */
public interface Desensitizer {

    /**
     * Data desensitize.
     * @param data Data to be desensitized
     * @return Desensitized results
     */
    String desensitize(CharSequence data);

}
