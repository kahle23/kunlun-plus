package artoria.document;

import artoria.exception.UncheckedException;

/**
 * Document process exception.
 * @author Kahle
 */
public class DocumentProcessException extends UncheckedException {

    public DocumentProcessException() {

        super();
    }

    public DocumentProcessException(String message) {

        super(message);
    }

    public DocumentProcessException(Throwable cause) {

        super(cause);
    }

    public DocumentProcessException(String message, Throwable cause) {

        super(message, cause);
    }

}
