/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.exception.support;

import kunlun.common.Result;
import kunlun.exception.ExceptionUtils;
import kunlun.exception.ServletErrorHandler;
import kunlun.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static kunlun.common.constant.Charsets.STR_UTF_8;

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
        "    Error Code: " + errorCode + "<br />\n" : "") +
        "    Error Message: " + errorMessage + "<br />\n" +
        "    Please check the log for details if necessary. <br />\n" +
        "    Powered by kunlun-plus. <br />\n" +
        "</body>\n" +
        "</html>\n";
    }

    protected Object writeHtmlString(HttpServletRequest request,
                                     HttpServletResponse response,
                                     String htmlString) {
        // response write html.
        response.setContentType(TEXT_HTML + "; charset=" + STR_UTF_8);
        try { response.getWriter().write(htmlString); }
        catch (IOException e) { throw ExceptionUtils.wrap(e); }
        // no return.
        return null;
    }

    protected Object buildHtmlResult(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Throwable throwable) {
        // create html.
        String errorMessage = throwable != null ? throwable.getMessage() :
                "An error has occurred. (Response Status: " + response.getStatus() + ") ";
        String htmlString = createHtmlString(null, errorMessage);
        // response write html.
        return writeHtmlString(request, response, htmlString);
    }

    protected Object buildOtherResult(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Throwable throwable) {
        String errorMessage = throwable != null ? throwable.getMessage() :
                "An error has occurred. (Response Status: " + response.getStatus() + ") ";
        return Result.failure(errorMessage);
    }

    @Override
    public Object handle(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        // Get accept info.
        String accept = request.getHeader("Accept");
        accept = StringUtils.isNotBlank(accept) ? accept.toLowerCase() : null;
        // Build result.
        if (accept != null && accept.contains(TEXT_HTML)) {
            return buildHtmlResult(request, response, throwable);
        }
        else { return buildOtherResult(request, response, throwable); }
    }

}
