package artoria.exception.support;

import artoria.common.Result;
import artoria.exception.ExceptionUtils;
import artoria.exception.ServletErrorHandler;
import artoria.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static artoria.common.Constants.DEFAULT_ENCODING_NAME;
import static java.lang.Boolean.FALSE;

public abstract class AbstractServletErrorHandler implements ServletErrorHandler {
    protected static final String TEXT_HTML = "text/html";

    protected String createHtmlString(String errorCode, String errorMessage) {
        return "<!DOCTYPE HTML>\n" +
        "<html>\n" +
        "<head>\n" +
        "    <title>An error has occurred. </title>\n" +
        "</head>\n" +
        "<body>\n" +
        "    <h3>\n" +
        "        An error has occurred. \n" +
        "    </h3>\n" +
        (StringUtils.isNotBlank(errorCode) ?
        "    Error Code: " + errorCode + "<br />\n" : ""
        ) +
        "    Error Message: " + errorMessage + "<br />\n" +
        "    Please check the log for details if necessary. <br />\n" +
        "</body>\n" +
        "</html>\n";
    }

    protected Object writeHtmlString(HttpServletRequest request,
                                     HttpServletResponse response,
                                     String htmlString) {
        // response write html
        response.setContentType(TEXT_HTML + "; charset=" + DEFAULT_ENCODING_NAME);
        try { response.getWriter().write(htmlString); }
        catch (IOException e) { throw ExceptionUtils.wrap(e); }
        // no return
        return null;
    }

    protected Object buildHtmlResult(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Throwable throwable) {
        // create html
        String errorMessage = throwable != null ? throwable.getMessage() :
                "An error has occurred. (Response Status: " + response.getStatus() + ") ";
        String htmlString = createHtmlString(null, errorMessage);
        // response write html
        return writeHtmlString(request, response, htmlString);
    }

    protected Object buildOtherResult(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Throwable throwable) {
        String errorMessage; int respStatus = response.getStatus();
        errorMessage = throwable != null ? throwable.getMessage() :
                "An error has occurred. (Response Status: " + respStatus + ") ";
        return new Result<Object>(FALSE, null, errorMessage);
    }

    @Override
    public Object handle(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        // Get accept info
        String accept = request.getHeader("Accept");
        accept = StringUtils.isNotBlank(accept) ? accept.toLowerCase() : null;
        // Build result
        if (accept != null && accept.contains(TEXT_HTML)) {
            return buildHtmlResult(request, response, throwable);
        }
        else {
            return buildOtherResult(request, response, throwable);
        }
    }

}
