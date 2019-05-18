package artoria.servlet;

import artoria.time.DateUtils;
import artoria.util.ArrayUtils;
import artoria.util.MapUtils;
import artoria.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import static artoria.common.Constants.*;

/**
 * Request tools.
 * @author Kahle
 */
public class RequestUtils {

    public static String getUserAgent(HttpServletRequest request) {
        if (request == null) { return null; }
        String userAgent = request.getHeader("User-Agent");
        return StringUtils.isBlank(userAgent) ? null : userAgent;
    }

    public static String getRemoteAddr(HttpServletRequest request) {
        if (request == null) { return null; }
        String address = request.getHeader("X-Forwarded-For");
        if(StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = request.getHeader("X-Real-IP");
        }
        if(StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = request.getHeader("Proxy-Client-IP");
        }
        if(StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = request.getHeader("WL-Proxy-Client-IP");
        }
        if(StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = request.getHeader("HTTP_CLIENT_IP");
        }
        if(StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if(StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = request.getRemoteAddr();
        }
        return address;
    }

    public static String toString(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder()
                .append("$time").append(COLON).append(BLANK_SPACE)
                .append(DateUtils.format()).append(NEWLINE);

        builder.append("$from").append(COLON).append(BLANK_SPACE)
                .append(request.getRemoteAddr()).append(COLON)
                .append(request.getRemotePort()).append(NEWLINE);

        builder.append("$method").append(COLON).append(BLANK_SPACE)
                .append(request.getMethod()).append(NEWLINE);

        builder.append("$target").append(COLON).append(BLANK_SPACE)
                .append(request.getRequestURL().toString()).append(NEWLINE);

        String contentType = request.getContentType();
        if (StringUtils.isNotBlank(contentType)) {
            builder.append("$content-type").append(LEFT_SQUARE_BRACKET)
                    .append(contentType).append(RIGHT_SQUARE_BRACKET).append(BLANK_SPACE);
        }

        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)) {
            builder.append("$cookie").append(LEFT_SQUARE_BRACKET)
                    .append(RequestUtils.toString(cookies)).append(RIGHT_SQUARE_BRACKET)
                    .append(BLANK_SPACE);
        }

        Map<String, String[]> parameterMap = request.getParameterMap();
        if (MapUtils.isNotEmpty(parameterMap)) {
            builder.append("$parameter").append(COLON).append(BLANK_SPACE);
            builder.append(RequestUtils.toString(parameterMap)).append(NEWLINE);
        }

        String characterEncoding = request.getCharacterEncoding();
        if (StringUtils.isNotBlank(characterEncoding)) {
            builder.append("$character-encoding").append(COLON)
                    .append(BLANK_SPACE).append(characterEncoding).append(NEWLINE);
        }

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            builder.append(headerName).append(COLON).append(BLANK_SPACE)
                    .append(request.getHeader(headerName)).append(NEWLINE);
        }

        return builder.toString();
    }

    public static String toString(Cookie[] cookies) {
        StringBuilder builder = new StringBuilder();
        if (ArrayUtils.isEmpty(cookies)) { return EMPTY_STRING; }
        for (Cookie cookie : cookies) {
            builder.append(cookie.getName()).append(EQUAL)
                    .append(cookie.getValue()).append(COMMA);
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static String toString(Map<String, String[]> params) {
        StringBuilder builder = new StringBuilder();
        if (params == null) { return EMPTY_STRING; }
        Set<Map.Entry<String, String[]>> entries = params.entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            String[] values = entry.getValue();
            if (values.length == 1) {
                builder.append(entry.getKey())
                        .append(EQUAL)
                        .append(values[0])
                        .append(COMMA);
            }
            else {
                builder.append(entry.getKey())
                        .append(LEFT_SQUARE_BRACKET)
                        .append(RIGHT_SQUARE_BRACKET)
                        .append(EQUAL)
                        .append(LEFT_CURLY_BRACKET);
                for (String value : values) {
                    builder.append(value)
                            .append(COMMA);
                }
                builder.deleteCharAt(builder.length() - 1)
                        .append(RIGHT_CURLY_BRACKET)
                        .append(COMMA);
            }
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

}
