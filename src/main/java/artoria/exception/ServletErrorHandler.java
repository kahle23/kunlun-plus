package artoria.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The servlet error handler.
 * @author Kahle
 */
public interface ServletErrorHandler {

    /**
     * Handle unhandled exceptions, and return the corresponding result.
     * Maybe "Throwable" is null, if "ErrorController" is enabled.
     * @param request The http request
     * @param response The http response
     * @param throwable The caught exception
     * @return The result of exception handling
     */
    Object handle(HttpServletRequest request, HttpServletResponse response, Throwable throwable);

}
