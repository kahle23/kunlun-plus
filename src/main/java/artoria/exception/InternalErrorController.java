package artoria.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.web.ErrorAttributes;
//import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import artoria.common.Result;
import artoria.config.ExceptionProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static artoria.common.Constants.NEWLINE;
import static artoria.common.Constants.SLASH;

/**
 * Internal error controller.
 * @author Kahle
 */
@Controller
public class InternalErrorController implements ErrorController {
    private static Logger log = LoggerFactory.getLogger(InternalErrorController.class);
    private static final String ERROR_PATH = "/error";
    private ExceptionProperties exceptionProperties;
    private ErrorAttributes errorAttributes;

    private void writeInbuiltPage(HttpServletRequest request
            , HttpServletResponse response) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE HTML>").append(NEWLINE);
        builder.append("<html>").append(NEWLINE);
        builder.append("<head>").append(NEWLINE);
        builder.append("<title>").append("An error has occurred").append("</title>").append(NEWLINE);
        builder.append("</head>").append(NEWLINE);
        builder.append("<body>").append(NEWLINE);
        builder.append("<h3>").append(NEWLINE);
        builder.append("An error has occurred. ").append(NEWLINE);
        builder.append("</h3>").append(NEWLINE);
        builder.append("Response Status: ").append(response.getStatus()).append("<br />").append(NEWLINE);
        builder.append("Please check the log for details if necessary. ").append("<br />").append(NEWLINE);
        builder.append("Powered by Artoria. ").append("<br />").append(NEWLINE);
        builder.append("</body>").append(NEWLINE);
        builder.append("</html>").append(NEWLINE);
        try {
            PrintWriter writer = response.getWriter();
            writer.write(builder.toString());
        }
        catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Autowired
    public InternalErrorController(ErrorAttributes errorAttributes
            , ExceptionProperties exceptionProperties) {
        this.errorAttributes = errorAttributes;
        this.exceptionProperties = exceptionProperties;
    }

    @Override
    public String getErrorPath() {

        return ERROR_PATH;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = ERROR_PATH, produces = {"text/html"})
    public Object returnPageResult(HttpServletRequest request, HttpServletResponse response) {
        if (exceptionProperties.getInternalErrorPage()) {
            this.writeInbuiltPage(request, response);
            return null;
        }
        else {
            int code = response.getStatus();
            String path = exceptionProperties.getBaseTemplatePath() + SLASH + code;
            return new ModelAndView(path);
        }
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = ERROR_PATH)
    public Object returnJsonResult(HttpServletRequest request, HttpServletResponse response) {
        int code = response.getStatus();
        Result<Object> result = new Result<Object>();
        result.setSuccess(false);
        result.setMessage("An error has occurred. (Response Status: " + code + ") ");
        return result;
    }

}
