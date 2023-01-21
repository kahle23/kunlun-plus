package artoria.exception.spring;

import artoria.exception.ServletErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple error controller.
 * @author Kahle
 */
@Controller
public class ServletErrorHandlerErrorController implements ErrorController {
    private static final Logger log = LoggerFactory.getLogger(ServletErrorHandlerErrorController.class);
    private static final String ERROR_PATH = "/error";
    private final ServletErrorHandler servletErrorHandler;
    private final ErrorAttributes errorAttributes;

    @Autowired
    public ServletErrorHandlerErrorController(ServletErrorHandler servletErrorHandler,
                                              ErrorAttributes errorAttributes) {
        this.servletErrorHandler = servletErrorHandler;
        this.errorAttributes = errorAttributes;
        log.info("The error controller based on servlet error handler was initialized success. ");
    }

    @Override
    public String getErrorPath() {

        return ERROR_PATH;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = ERROR_PATH, produces = {"text/html"})
    public Object returnPageResult(HttpServletRequest request, HttpServletResponse response) {
        WebRequest webRequest = new ServletWebRequest(request);
        Throwable throwable = errorAttributes.getError(webRequest);
        return servletErrorHandler.handle(request, response, throwable);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = ERROR_PATH)
    public Object returnJsonResult(HttpServletRequest request, HttpServletResponse response) {
        WebRequest webRequest = new ServletWebRequest(request);
        Throwable throwable = errorAttributes.getError(webRequest);
        return servletErrorHandler.handle(request, response, throwable);
    }

}
