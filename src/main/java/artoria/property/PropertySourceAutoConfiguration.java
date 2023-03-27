package artoria.property;

import artoria.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class PropertySourceAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(PropertySourceAutoConfiguration.class);

    @Autowired
    public PropertySourceAutoConfiguration(Environment env) {
        if (ClassUtils.isPresent("org.slf4j.MDC", getClass().getClassLoader())) {
            PropertyUtils.registerSource(new MdcPropertySource("mdc"));
        }
        if (env != null) {
            PropertyUtils.registerSource(new SpringEnvPropertySource("spring", env));
        }
    }

}
