package artoria.exception;

import artoria.common.Result;
import artoria.config.ExceptionProperties;
import artoria.servlet.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static artoria.common.Constants.EMPTY_STRING;
import static artoria.common.Constants.NEWLINE;
import static artoria.exception.InternalErrorCode.INTERNAL_SERVER_ERROR;

/**
 * Internal exception handler.
 * @author Kahle
 */
@ControllerAdvice
public class InternalExceptionHandler {
    private static Logger log = LoggerFactory.getLogger(InternalExceptionHandler.class);
    private ExceptionProperties exceptionProperties;

    private void writeInbuiltPage(HttpServletRequest request
            , HttpServletResponse response, Exception ex) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE HTML>").append(NEWLINE);
        builder.append("<html>").append(NEWLINE);
        builder.append("<head>").append(NEWLINE);
        builder.append("<title>").append("Internal server error").append("</title>").append(NEWLINE);
        builder.append("</head>").append(NEWLINE);
        builder.append("<body>").append(NEWLINE);
        builder.append("<h3>").append(NEWLINE);
        builder.append("Internal server error. ").append(NEWLINE);
        builder.append("</h3>").append(NEWLINE);
        builder.append("Response Status: ").append(response.getStatus()).append("<br />").append(NEWLINE);
        if (exceptionProperties.getShowErrorMessage()) {
            String message = ex.getMessage();
            message = message == null ? EMPTY_STRING : message;
            builder.append("Error Message: ").append(message).append("<br />").append(NEWLINE);
        }
        builder.append("Please check the log for details if necessary. ").append("<br />").append(NEWLINE);
        builder.append("Powered by Artoria. ").append("<br />").append(NEWLINE);
        builder.append("</body>").append(NEWLINE);
        builder.append("</html>").append(NEWLINE);
        try {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(builder.toString());
        }
        catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Autowired
    public InternalExceptionHandler(ExceptionProperties exceptionProperties) {

        this.exceptionProperties = exceptionProperties;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public Object handleException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        log.error("Caught an unhandled exception. ", ex);
        if (RequestUtils.isXmlHttpRequest(request)) {
            return this.createJsonResult(request, response, ex);
        }
        else {
            return this.createPageResult(request, response, ex);
        }
    }

    private Object createPageResult(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        if (exceptionProperties.getInternalErrorPage()) {
            this.writeInbuiltPage(request, response, ex);
            return null;
        }
        else {
            String path = exceptionProperties.getBaseTemplatePath() + "/500";
            ModelAndView modelAndView = new ModelAndView(path);
            if (exceptionProperties.getShowErrorMessage()) {
                String message = ex.getMessage();
                message = message == null ? EMPTY_STRING : message;
                modelAndView.addObject("errorMessage", message);
            }
            return modelAndView;
        }
    }

    private Object createJsonResult(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        Result<Object> result = new Result<Object>();
        result.setSuccess(false);
        if (ex instanceof BusinessException) {
            BusinessException be = (BusinessException) ex;
            ErrorCode errorCode = be.getErrorCode();
            boolean hasErrorCode = errorCode != null;
            result.setCode(hasErrorCode
                    ? errorCode.getCode() : INTERNAL_SERVER_ERROR.getCode());
            String msg = hasErrorCode ? errorCode.getDescription() : null;
            msg = msg != null ? msg : be.getLocalizedMessage();
            msg = msg != null ? msg : INTERNAL_SERVER_ERROR.getDescription();
            result.setMessage(msg);
        }
        else {
            result.setCode(INTERNAL_SERVER_ERROR.getCode());
            result.setMessage(INTERNAL_SERVER_ERROR.getDescription());
        }
        String code = result.getCode();
        String msg;
        if (code != null &&
                (msg = CodeMapper.getMessage(code)) != null) {
            result.setMessage(msg);
        }
        if (exceptionProperties.getShowErrorMessage()) {
            msg = ex.getMessage();
            msg = msg == null ? EMPTY_STRING : msg;
            msg = result.getMessage() + " (Error Message: " + msg + ")";
            result.setMessage(msg);
        }
        return result;
    }

}
