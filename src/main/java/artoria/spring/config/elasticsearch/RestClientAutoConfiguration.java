package artoria.spring.config.elasticsearch;

import artoria.util.Assert;
import artoria.util.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static artoria.common.Constants.ZERO;

/**
 * The elasticsearch rest client auto configuration.
 * @author Kahle
 */
@Configuration
@ConditionalOnClass(RestClient.class)
@ConditionalOnMissingClass({"org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration"})
@EnableConfigurationProperties(RestClientProperties.class)
public class RestClientAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(RestClientAutoConfiguration.class);
    private final RestClientProperties restClientProperties;

    public RestClientAutoConfiguration(RestClientProperties restClientProperties) {
        Assert.notNull(restClientProperties, "Parameter \"restClientProperties\" must not null. ");
        this.restClientProperties = restClientProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public RestClientBuilder restClientBuilder() {
        List<String> uris = restClientProperties.getUris();
        String username = restClientProperties.getUsername();
        String password = restClientProperties.getPassword();
        int size = uris.size();
        HttpHost[] hosts = new HttpHost[size];
        for (int i = ZERO; i < size; i++) {
            String uri = uris.get(i);
            if (StringUtils.isBlank(uri)) {
                continue;
            }
            hosts[i] = HttpHost.create(uri);
        }
        RestClientBuilder restClientBuilder = RestClient.builder(hosts);
        if (StringUtils.isNotBlank(username)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            Credentials credentials = new UsernamePasswordCredentials(username, password);
            credentialsProvider.setCredentials(AuthScope.ANY, credentials);
            restClientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                    return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            });
        }
        return restClientBuilder;
    }

    @Bean
    @ConditionalOnMissingBean
    public RestClient restClient(RestClientBuilder restClientBuilder) {

        return restClientBuilder.build();
    }

    @Bean
    @ConditionalOnClass(RestHighLevelClient.class)
    @ConditionalOnMissingBean
    public RestHighLevelClient restHighLevelClient(RestClientBuilder restClientBuilder) {

        return new RestHighLevelClient(restClientBuilder);
    }

}
