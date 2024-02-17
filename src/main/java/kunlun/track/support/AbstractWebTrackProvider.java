/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.track.support;

import kunlun.servlet.RequestUtils;
import kunlun.spring.RequestContextUtils;
import kunlun.track.SimpleTrackProvider;
import kunlun.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;

/**
 * The abstract web track provider.
 * @author Kahle
 */
public abstract class AbstractWebTrackProvider extends SimpleTrackProvider {

    protected AbstractWebTrackProvider(Map<String, Object> commonProperties) {

        super(commonProperties);
    }

    public AbstractWebTrackProvider() {

    }

    public AbstractWebTrackProvider(Collection<String> showKeys) {

        super(showKeys);
    }

    /**
     * Get the value from the properties based on the key.
     * @param properties The properties
     * @param key The key
     * @param defaultValue The default value
     * @return The value
     */
    protected String takeOut(Map<?, ?> properties, Object key, String defaultValue) {
        Object valueObj = properties.get(key);
        if (valueObj == null) { return defaultValue; }
        String value = String.valueOf(valueObj);
        properties.remove(key);
        return StringUtils.isNotBlank(value) ? value : defaultValue;
    }

    /**
     * Process and fill the principal information.
     * @param event The event data object
     * @param request The HTTP request object
     */
    protected void principalInfo(Event event, HttpServletRequest request, HttpServletResponse response) {
        if (request == null) { return; }
        Map<Object, Object> properties = event.getProperties();
        Object principal = event.getPrincipal();
        // The principal id.
        String principalIdName = takeOut(properties, "principalIdName", "principalId");
        if (principal == null) {
            principal = request.getHeader(principalIdName);
            event.setPrincipal(principal);
        }
        // The token id.
        String tokenIdName = takeOut(properties, "tokenIdName", "authorization");
        String tokenId = request.getHeader(tokenIdName);
        if (StringUtils.isNotBlank(tokenId)) {
            properties.put("tokenId", tokenId);
        }
    }

    /**
     * Process and fill the location information.
     * @param event The event data object
     * @param request The HTTP request object
     */
    protected void locationInfo(Event event, HttpServletRequest request, HttpServletResponse response) {
        //if (request == null) { return; }

    }

    /**
     * Process and fill the request information.
     * @param event The event data object
     * @param request The HTTP request object
     */
    protected void requestInfo(Event event, HttpServletRequest request, HttpServletResponse response) {
        if (request == null) { return; }
        Map<Object, Object> properties = event.getProperties();
        properties.put("requestApiUri", request.getRequestURI());
        //properties.put("requestApiName", EMPTY_STRING)
        properties.put("requestMethod", request.getMethod());
        properties.put("requestAddress", String.valueOf(request.getRequestURL()));
        properties.put("requestReferer", RequestUtils.getReferer(request));
    }

    /**
     * Process and fill the client information.
     * @param event The event data object
     * @param request The HTTP request object
     */
    protected void clientInfo(Event event, HttpServletRequest request, HttpServletResponse response) {
        if (request == null) { return; }
        Map<Object, Object> properties = event.getProperties();
        // The client id.
        String clientIdName = takeOut(properties, "clientIdName", "clientId");
        String clientId = request.getHeader(clientIdName);
        if (StringUtils.isNotBlank(clientId)) {
            properties.put("clientId", clientId);
        }
        // The client app id.
        String clientAppIdName = takeOut(properties, "clientAppIdName", "clientAppId");
        String clientAppId = request.getHeader(clientAppIdName);
        if (StringUtils.isNotBlank(clientAppId)) {
            properties.put("clientAppId", clientAppId);
        }
        // The client net address.
        properties.put("clientNetAddress", RequestUtils.getRemoteAddress(request));
        // The client user agent.
        properties.put("clientUserAgent", RequestUtils.getUserAgent(request));
    }

    /**
     * Process and fill the other information.
     * @param event The event data object
     */
    protected void otherInfo(Event event, HttpServletRequest request, HttpServletResponse response) {
        //if (request == null) { return; }

    }

    @Override
    protected void process(Event event) {
        // Filling common properties has already been done.
        // Get the HTTP request and response objects.
        HttpServletResponse response = RequestContextUtils.getResponse();
        HttpServletRequest request = RequestContextUtils.getRequest();
        // Process server information (such as "serverId", "serverAppId").
        // The server information is fixed in "commonProperties".
        // Process client information.
        clientInfo(event, request, response);
        // Process principal information.
        principalInfo(event, request, response);
        // Process location information.
        locationInfo(event, request, response);
        // Process request information.
        requestInfo(event, request, response);
        // Process other information.
        otherInfo(event, request, response);
    }

}
