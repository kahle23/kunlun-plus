/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.mock.support;

import com.github.jsonzou.jmockdata.JMockData;
import kunlun.mock.MockUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({JMockData.class})
public class JMockDataAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(JMockDataAutoConfiguration.class);

    public JMockDataAutoConfiguration() {
        MockUtils.setMockProvider(new JMockDataProvider());
        log.info("The \"JMockData\" was initialized success. ");
    }

}
