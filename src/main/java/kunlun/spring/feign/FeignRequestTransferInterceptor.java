/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import kunlun.spring.RequestContextUtils;
import kunlun.util.CollectionUtils;
import kunlun.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * The feign request transfer interceptor.
 * @author Kahle
 */
public class FeignRequestTransferInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        transferHeaders(requestTemplate, Collections.<String>emptyList());
        transferContext(requestTemplate);
        transferOthers(requestTemplate);
    }

    /**
     * Transfer request headers.
     * The request header of the servlet cannot be passed randomly.
     * @param feignRequest The feign request
     * @param headerNames The passed header names
     */
    protected void transferHeaders(RequestTemplate feignRequest, List<String> headerNames) {
        // Transfer request headers.
        if (CollectionUtils.isEmpty(headerNames)) { return; }
        HttpServletRequest request = RequestContextUtils.getRequest();
        if (request == null) { return; }
        Map<String, Collection<String>> headers = new LinkedHashMap<String, Collection<String>>();
        for (String headerName : headerNames) {
            if (StringUtils.isBlank(headerName)) { continue; }
            Enumeration<String> values = request.getHeaders(headerName);
            if (CollectionUtils.isEmpty(values)) { continue; }
            Collection<String> collection = headers.get(headerName);
            if (collection == null) {
                collection = new ArrayList<String>();
                headers.put(headerName, collection);
            }
            CollectionUtils.addAll(collection, values);
        }
        headers.putAll(feignRequest.headers());
        feignRequest.headers(headers);
    }

    /**
     * Transfer the context.
     * @param feignRequest The feign request
     */
    protected void transferContext(RequestTemplate feignRequest) {

    }

    /**
     * Transfer the others.
     * @param feignRequest The feign request
     */
    protected void transferOthers(RequestTemplate feignRequest) {

    }

}
