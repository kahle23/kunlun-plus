package artoria.spring.config.web.cors;

import artoria.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Map;

/**
 * The cors filter auto configuration.
 * @author Kahle
 */
@Configuration
@ConditionalOnClass({CorsFilter.class})
@ConditionalOnProperty(name = "spring.extension.web.cors.enabled", havingValue = "true")
@EnableConfigurationProperties({SpringWebCorsProperties.class})
public class SpringWebCorsFilterAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(SpringWebCorsFilterAutoConfiguration.class);
    private final SpringWebCorsProperties corsProperties;

    @Autowired
    public SpringWebCorsFilterAutoConfiguration(SpringWebCorsProperties corsProperties) {
        Assert.notNull(corsProperties, "Parameter \"corsProperties\" must not null. ");
        this.corsProperties = corsProperties;
    }

    @Bean
    public CorsFilter corsFilter() {
        Map<String, CorsConfiguration> configMap = corsProperties.getConfigurations();
        Assert.notEmpty(configMap, "Parameter \"corsConfigurations\" must not empty. ");
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        for (Map.Entry<String, CorsConfiguration> entry : configMap.entrySet()) {
            CorsConfiguration config = entry.getValue();
            String urlPattern = entry.getKey();
            Assert.notBlank(urlPattern, "Parameter \"urlPattern\" must not blank. ");
            Assert.notNull(config, "Parameter \"configuration\" must not null. ");
            configurationSource.registerCorsConfiguration(urlPattern, config);
        }
        CorsFilter corsFilter = new CorsFilter(configurationSource);
        log.info("The cors filter was initialized success. ");
        return corsFilter;
    }

}
