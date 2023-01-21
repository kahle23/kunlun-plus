package artoria.exception.support;

import artoria.common.Errors;
import artoria.common.Result;
import artoria.data.ErrorCode;
import artoria.exception.BusinessException;
import artoria.exception.ExceptionUtils;
import artoria.exception.ServletErrorHandler;
import artoria.util.Assert;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static artoria.common.Constants.*;
import static java.lang.Boolean.FALSE;

/**
 * Simple error handler.
 * @author Kahle
 */
public class SimpleServletErrorHandler implements ServletErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(SimpleServletErrorHandler.class);
    private static final String DEFAULT_ERROR_MESSAGE = "Internal server error. ";
    private static final String TEXT_HTML = "text/html";
    private final Boolean internalErrorPage;
    private final String baseTemplatePath;

    public SimpleServletErrorHandler(Boolean internalErrorPage,
                                     String baseTemplatePath) {
        Assert.notNull(internalErrorPage, "Parameter \"internalErrorPage\" must not null. ");
        Assert.notBlank(baseTemplatePath, "Parameter \"baseTemplatePath\" must not blank. ");
        this.internalErrorPage = internalErrorPage;
        this.baseTemplatePath = baseTemplatePath;
    }

    protected ErrorCode getErrorCode(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        if (throwable == null) {
            int respStatus = response.getStatus();
            final String description = "An error has occurred. (Response Status: " + respStatus + ") ";
            return new ErrorCode() {
                @Override
                public String getCode() {
                    return EMPTY_STRING;
                }
                @Override
                public String getDescription() {
                    return description;
                }
            };
        }
        if (!(throwable instanceof BusinessException)) {
            return Errors.INTERNAL_SERVER_ERROR;
        }
        BusinessException bizException = (BusinessException) throwable;
        ErrorCode errorCode = bizException.getErrorCode();
        if (errorCode == null) { return Errors.INTERNAL_SERVER_ERROR; }
        return errorCode;
    }

    protected Object pageResult(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        ErrorCode errorCode = getErrorCode(request, response, throwable);
        int responseStatus = response.getStatus();
        if (!internalErrorPage) {
            String viewPath = baseTemplatePath + SLASH + responseStatus;
            ModelAndView modelAndView = new ModelAndView(viewPath);
            modelAndView.addObject("responseStatus", responseStatus);
            modelAndView.addObject("errorCode", errorCode.getCode());
            modelAndView.addObject("errorMessage", errorCode.getDescription());
            return modelAndView;
        }
        response.setContentType(TEXT_HTML + "; charset=" + DEFAULT_ENCODING_NAME);
        String html =
        "<!DOCTYPE HTML>\n" +
        "<html>\n" +
        "<head>\n" +
        "    <title>An error has occurred. </title>\n" +
        "</head>\n" +
        "<body>\n" +
        "    <h3>\n" +
        "        An error has occurred. \n" +
        "    </h3>\n" +
        "    Response Status: " + responseStatus + "<br />\n" +
        "    Error Code: " + errorCode.getCode() + "<br />\n" +
        "    Error Message: " + errorCode.getDescription() + "<br />\n" +
        "    Please check the log for details if necessary. <br />\n" +
        "    Powered by <a href=\"https://github.com/kahlkn/artoria\" target=\"_blank\">Artoria</a>. <br />\n" +
        "</body>\n" +
        "</html>\n";
        try { response.getWriter().write(html); }
        catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
        return null;
    }

    @Override
    public Object handle(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        String accept = request.getHeader("Accept");
        accept = StringUtils.isNotBlank(accept) ? accept.toLowerCase() : null;
        if (accept != null && accept.contains(TEXT_HTML)) {
            return pageResult(request, response, throwable);
        }
        ErrorCode errorCode = getErrorCode(request, response, throwable);
        return new Result<Object>(FALSE, errorCode.getCode(), errorCode.getDescription());
    }

}
