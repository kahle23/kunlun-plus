package kunlun.spring.security;

import kunlun.core.handler.DataFieldsFilteringHandler;
import kunlun.spring.context.ContextServletAutoConfiguration;
import kunlun.spring.security.support.DataFieldsFilteringResponseBodyAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties({SecurityProperties.class})
@AutoConfigureAfter(ContextServletAutoConfiguration.class)
@ConditionalOnBean(DataFieldsFilteringHandler.class)
@Import({DataFieldsFilteringResponseBodyAdvice.class})
@ConditionalOnProperty(name = {"kunlun.context.servlet.enabled",
        "kunlun.security.servlet.dataField.enabled"}, havingValue = "true")
public class ResponseDataFilteringConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ResponseDataFilteringConfiguration.class);

    public ResponseDataFilteringConfiguration() {

        log.info("Response Body Data Filtering init success. ");
    }

}
