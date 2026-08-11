/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.exception.support;

import kunlun.common.constant.Nil;
import kunlun.exception.ExceptionUtil;
import kunlun.exception.ServletErrorHandler;
import kunlun.util.StrUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static kunlun.common.constant.Charsets.STR_UTF_8;

/**
 * AbstractServletExceptionHandler.
 * @author Kahle
 */
public abstract class AbstractServletErrorHandler implements ServletErrorHandler {
    protected static final String JSON = "application/json";
    protected static final String HTML = "text/html";

    /**
     * writeHtml
     * @param resp response
     * @param errorCode errorCode
     * @param errorMessage errorMessage
     * @return result
     */
    protected Object writeHtml(HttpServletResponse resp, String errorCode, String errorMessage) {
        // create html.
        String html = "<!DOCTYPE HTML>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>An error has occurred. </title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h3>\n" +
                "        An error has occurred. \n" +
                "    </h3>\n" +
                (StrUtil.isNotBlank(errorCode) ?
                        "    Error Code: " + errorCode + "<br />\n" : "") +
                "    Error Message: " + errorMessage + "<br />\n" +
                "    Please check the log for details if necessary. <br />\n" +
                "    Powered by baibao. <br />\n" +
                "</body>\n" +
                "</html>\n";
        // response write html.
        try {
            resp.setContentType(HTML + "; charset=" + STR_UTF_8);
            resp.getWriter().write(html);
        } catch (IOException e) {
            throw ExceptionUtil.wrap(e);
        }
        // no return.
        return null;
    }

    /**
     * transform
     * @param request request
     * @param response response
     * @param th th
     * @return result
     */
    public abstract Object transform(HttpServletRequest request, HttpServletResponse response, Throwable th);

    /**
     * record error
     * @param label label
     * @param request request
     * @param response response
     * @param th th
     */
    public void record(String label, HttpServletRequest request, HttpServletResponse response, Throwable th) {

    }

    /**
     * forHtml
     * @param request request
     * @param response response
     * @param th th
     * @return result
     */
    public Object forHtml(HttpServletRequest request, HttpServletResponse response, Throwable th) {
        // create error message.
        String errorMessage = th != null ? th.getMessage() :
                "An error has occurred. (Response Status: " + response.getStatus() + ") ";
        // response write html.
        return writeHtml(response, Nil.STR, errorMessage);
    }

    /**
     * forJson
     * @param request request
     * @param response response
     * @param th th
     * @return result
     */
    public Object forJson(HttpServletRequest request, HttpServletResponse response, Throwable th) {

        return transform(request, response, th);
    }

    /**
     * forDefault
     * @param request request
     * @param response response
     * @param th th
     * @return result
     */
    public Object forDefault(HttpServletRequest request, HttpServletResponse response, Throwable th) {

        return forJson(request, response, th);
    }

    @Override
    public Object execute(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        // Get accept info.
        String accept = request.getHeader("Accept");
        accept = StrUtil.isNotBlank(accept) ? accept.toLowerCase() : null;
        // Processing error.
        if (accept != null && accept.contains(HTML)) {
            return forHtml(request, response, throwable);
        } else if (accept != null && accept.contains(JSON)) {
            return forJson(request, response, throwable);
        } else { return forDefault(request, response, throwable); }
    }

}
