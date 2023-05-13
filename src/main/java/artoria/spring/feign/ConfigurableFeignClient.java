package artoria.spring.feign;

import feign.Client;
import feign.Request;
import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import static artoria.common.Constants.EMPTY_STRING;
import static artoria.common.Constants.QUESTION_MARK;
import static artoria.util.StringUtils.isNotBlank;

/**
 * The configurable feign client.
 * @deprecated This is not recommended. Because frameworks like "seata" only use the delegate class, not the current object. So for scalability, this is not a good idea.
 * @author Kahle
 */
@Deprecated
public class ConfigurableFeignClient extends LoadBalancerFeignClient {
    private static final Logger log = LoggerFactory.getLogger(ConfigurableFeignClient.class);
    private final Map<String, String> configInfo;
    private final Client delegate;

    public ConfigurableFeignClient(Client delegate,
                                   CachingSpringLoadBalancerFactory lbClientFactory,
                                   SpringClientFactory clientFactory,
                                   Map<String, String> configInfo) {
        super(delegate, lbClientFactory, clientFactory);
        if (configInfo == null) { configInfo = Collections.emptyMap(); }
        this.configInfo = configInfo;
        this.delegate = delegate;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        // Variable definition.
        URI nowUri = URI.create(request.url());
        String query = nowUri.getQuery();
        String path = nowUri.getPath();
        String host = nowUri.getHost();
        String target;
        query = isNotBlank(query)
                ? QUESTION_MARK + query : EMPTY_STRING;
        // Check whether configuration information exists.
        boolean existConfig = isNotBlank(target = configInfo.get(host))
                || isNotBlank(target = configInfo.get(host + path));
        // Go to the address information in the configuration.
        if (existConfig) {
            // Rebuild the request object.
            String url = target + path + query;
            request = Request.create(request.httpMethod(), url,
                    request.headers(), request.body(), request.charset(), request.requestTemplate());
            log.info("Feign executing {} {}", request.httpMethod(), request.url());
            return delegate.execute(request, options);
        }
        return super.execute(request, options);
    }

}
