package artoria.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Error processor.
 * @author Kahle
 */
public interface ErrorProcessor {

    /**
     * Process unhandled exceptions, and return the corresponding result.
     * @param request Http request
     * @param response Http response
     * @param throwable Caught exception
     * @return The result of exception handling
     */
    Object process(HttpServletRequest request, HttpServletResponse response, Throwable throwable);

}
