package artoria.spring.config.web.cors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Map;

/**
 * The cross-origin resource sharing (cors) configuration (suitable for spring web).
 * @author Kahle
 */
@ConfigurationProperties("spring.extension.web.cors")
public class SpringWebCorsProperties {
    private Map<String, CorsConfiguration> configurations;
    private Boolean enabled;

    public Boolean getEnabled() {

        return enabled;
    }

    public void setEnabled(Boolean enabled) {

        this.enabled = enabled;
    }

    public Map<String, CorsConfiguration> getConfigurations() {

        return configurations;
    }

    public void setConfigurations(Map<String, CorsConfiguration> configurations) {

        this.configurations = configurations;
    }

}
