package kunlun.spring.config.feign;

//import kunlun.spring.feign.ConfigurableFeignClient;
//import feign.Client;
//import feign.Feign;
//import org.springframework.boot.autoconfigure.AutoConfigureBefore;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
//import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
//import org.springframework.cloud.openfeign.FeignAutoConfiguration;
//import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
//import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.util.Assert;
//
//import java.util.HashMap;
//import java.util.Map;

@Deprecated
//@Configuration
//@ConditionalOnClass({ Feign.class })
//@AutoConfigureBefore(FeignAutoConfiguration.class)
//@EnableConfigurationProperties({ FeignHttpClientProperties.class })
public class ConfigurableFeignClientDemoAutoConfiguration {

    /*@Bean
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnMissingClass("org.springframework.retry.support.RetryTemplate")
    public CachingSpringLoadBalancerFactory cachingLBClientFactory(
            SpringClientFactory factory) {
        return new CachingSpringLoadBalancerFactory(factory);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "org.springframework.retry.support.RetryTemplate")
    public CachingSpringLoadBalancerFactory retryabeCachingLBClientFactory(
            SpringClientFactory factory, LoadBalancedRetryFactory retryFactory) {
        return new CachingSpringLoadBalancerFactory(factory, retryFactory);
    }

    @Bean
    public Client feignClient(CachingSpringLoadBalancerFactory cachingFactory,
                              SpringClientFactory clientFactory) {
        Assert.notNull(cachingFactory, "cachingFactory not null");
        Assert.notNull(clientFactory, "clientFactory not null");
        Client delegate = new Client.Default(null, null);
        Map<String, String> configInfo = new HashMap<String, String>();
        configInfo.put("xxxx-server", "http://127.0.0.1:8081");
        return new ConfigurableFeignClient(delegate, cachingFactory, clientFactory, configInfo);
    }*/

}
