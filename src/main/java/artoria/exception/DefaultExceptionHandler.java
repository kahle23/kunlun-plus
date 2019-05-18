package artoria.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Default exception handler.
 * @author Kahle
 */
@ControllerAdvice
public class DefaultExceptionHandler {
    private static Logger log = LoggerFactory.getLogger(DefaultExceptionHandler.class);
    private ErrorProcessor errorProcessor;

    @Autowired
    public DefaultExceptionHandler(ErrorProcessor errorProcessor) {
        this.errorProcessor = errorProcessor;
        log.info("The internal exception handler was initialized success. ");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public Object handleException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        log.error("Caught an unhandled exception. ", ex);
        return errorProcessor.process(request, response, ex);
    }

}
