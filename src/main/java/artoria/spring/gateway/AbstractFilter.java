package artoria.spring.gateway;

import artoria.util.CollectionUtils;
import artoria.util.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.List;

import static artoria.common.constant.Symbols.BLANK_SPACE;
import static artoria.common.constant.Symbols.COMMA;
import static artoria.common.constant.Words.UNKNOWN;

public abstract class AbstractFilter {

    protected String getHeader(HttpHeaders headers, String headerName) {
        List<String> list = headers.get(headerName);
        if (CollectionUtils.isEmpty(list)) { return null; }
        // Concat.
        StringBuilder builder = new StringBuilder();
        boolean allIsBlank = true, first = true;
        for (String str : list) {
            // Handle comma.
            if (first) { first = false; }
            else { builder.append(COMMA); }
            // Handle blank.
            boolean isBlank = StringUtils.isBlank(str);
            if (isBlank) { continue; }
            else { allIsBlank = false; }
            // Handle append.
            builder.append(str.trim());
        }
        return allIsBlank ? null : builder.toString();
    }

    protected String getRealAddress(ServerHttpRequest request) {
        if (request == null) { return null; }
        HttpHeaders headers = request.getHeaders();
        String address = getHeader(headers, "X-Forwarded-For");
        if(StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = getHeader(headers, "X-Real-IP");
        }
        if(StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = getHeader(headers, "Proxy-Client-IP");
        }
        if(StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = getHeader(headers, "WL-Proxy-Client-IP");
        }
        if(StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = getHeader(headers, "HTTP_CLIENT_IP");
        }
        if(StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = getHeader(headers, "HTTP_X_FORWARDED_FOR");
        }
        if(StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = String.valueOf(request.getRemoteAddress());
        }
        return address;
    }

    protected String printRequest(ServerHttpRequest request, List<String> headerNames) {
        if (request == null) { return null; }
        // Get info.
        String remoteAddress = String.valueOf(request.getRemoteAddress());
        String realAddress = getRealAddress(request);
        String method = request.getMethodValue();
        String path = String.valueOf(request.getPath());
        // Get request's headers.
        HttpHeaders headers = request.getHeaders();
        // Build text.
        StringBuilder builder = new StringBuilder();
        builder.append("request-method: ").append(method).append("\n");
        builder.append("request-path:   ").append(path).append("\n");
        builder.append("remote-address: ").append(remoteAddress).append("\n");
        builder.append("real-address:   ").append(realAddress).append("\n");
        if (CollectionUtils.isNotEmpty(headerNames)) {
            for (String headerName : headerNames) {
                if (StringUtils.isBlank(headerName)) { continue; }
                String header = getHeader(headers, headerName);
                // Build header name.
                StringBuilder str = new StringBuilder(headerName + ":");
                while (str.length() < 16) { str.append(BLANK_SPACE); }
                // Append name and value.
                builder.append(str).append(header).append("\n");
            }
        }
        // end
        return builder.toString();
    }

}
