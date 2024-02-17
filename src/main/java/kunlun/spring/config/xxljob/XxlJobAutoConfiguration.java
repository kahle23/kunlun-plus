/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.config.xxljob;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import kunlun.util.Assert;
import kunlun.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The xxl-job spring executor auto configuration.
 * @see <a href="https://github.com/xuxueli/xxl-job">Xxl-Job</a>
 * @author Kahle
 */
@Configuration
@ConditionalOnClass({XxlJobSpringExecutor.class})
@ConditionalOnProperty(name = "spring.extension.xxl-job.enabled", havingValue = "true")
@EnableConfigurationProperties({XxlJobProperties.class})
public class XxlJobAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(XxlJobAutoConfiguration.class);

    @Bean
    public XxlJobSpringExecutor xxlJobSpringExecutor(XxlJobProperties xxlJobProperties) {
        Assert.notNull(xxlJobProperties, "Parameter \"xxlJobProperties\" must not null. ");
        // Get the arguments.
        XxlJobProperties.ExecutorConfig executorConfig = xxlJobProperties.getExecutor();
        Assert.notNull(executorConfig, "Parameter \"executorConfig\" must not null. ");
        String  adminAddresses =   xxlJobProperties.getAdminAddresses();
        String  accessToken =      xxlJobProperties.getAccessToken();
        String  appName =          executorConfig.getAppName();
        String  address =          executorConfig.getAddress();
        String  ip =               executorConfig.getIp();
        Integer port =             executorConfig.getPort();
        String  logPath =          executorConfig.getLogPath();
        Integer logRetentionDays = executorConfig.getLogRetentionDays();
        // Check and set defaults.
        Assert.notBlank(adminAddresses, "Parameter \"adminAddresses\" must not blank. ");
        Assert.notBlank(appName, "Parameter \"appName\" must not blank. ");
        if (port == null) { port = -1; }
        // Build the executor.
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppname(appName);
        if (StringUtils.isNotBlank(address)) {
            xxlJobSpringExecutor.setAddress(address);
        }
        if (StringUtils.isNotBlank(ip)) {
            xxlJobSpringExecutor.setIp(ip);
        }
        xxlJobSpringExecutor.setPort(port);
        if (StringUtils.isNotBlank(accessToken)) {
            xxlJobSpringExecutor.setAccessToken(accessToken);
        }
        if (StringUtils.isNotBlank(logPath)) {
            xxlJobSpringExecutor.setLogPath(logPath);
        }
        if (logRetentionDays != null) {
            xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
        }
        log.info("The spring xxl-job was initialized success. ");
        return xxlJobSpringExecutor;
    }

}
