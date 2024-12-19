package artoria.option;

import artoria.cache.Cache;
import artoria.cache.CacheUtils;
import artoria.cache.support.NoCache;
import artoria.cache.support.SpringSimpleCache;
import artoria.data.Dict;
import artoria.data.ReferenceType;
import artoria.util.Assert;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Option auto configuration.
 * @author Kahle
 */
@Deprecated // TODO: can delete
@Configuration
@EnableConfigurationProperties({OptionProperties.class})
public class OptionAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(OptionAutoConfiguration.class);

    @Autowired
    public OptionAutoConfiguration(ApplicationContext context, OptionProperties properties) {
        OptionProperties.ProviderType providerType = properties.getProviderType();
        Assert.notNull(providerType, "Parameter \"providerType\" must not null. ");
        OptionProvider optionProvider = null;
        if (OptionProperties.ProviderType.JDBC.equals(providerType)) {
            optionProvider = handleJdbc(context, properties);
        }
        else if (OptionProperties.ProviderType.CUSTOM.equals(providerType)) {
            optionProvider = handleCustom(context, properties);
        }
        else {
        }
        if (optionProvider != null) {
            OptionUtils.setOptionProvider(optionProvider);
        }
    }

    protected OptionProvider handleJdbc(ApplicationContext context, OptionProperties properties) {
        OptionProperties.JdbcConfig jdbcConfig = properties.getJdbcConfig();
        Assert.notNull(jdbcConfig, "Parameter \"jdbcConfig\" must not null. ");
        JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
        String ownerColumnName = jdbcConfig.getOwnerColumnName();
        String nameColumnName = jdbcConfig.getNameColumnName();
        String valueColumnName = jdbcConfig.getValueColumnName();
        String tableName = jdbcConfig.getTableName();
        String whereContent = jdbcConfig.getWhereContent();
        OptionProvider optionProvider = new JdbcOptionProvider(jdbcTemplate,
                ownerColumnName, nameColumnName, valueColumnName, tableName, whereContent);
        OptionProperties.CacheConfig cacheConfig = jdbcConfig.getCacheConfig();
        if (cacheConfig == null) {
            cacheConfig = new OptionProperties.CacheConfig();
        }
        String cacheName = cacheConfig.getCacheName();
        Long timeToLive = cacheConfig.getTimeToLive();
        TimeUnit timeUnit = cacheConfig.getTimeUnit();
        if (StringUtils.isBlank(cacheName)) {
            cacheName = "jdbc-option-provider-cache";
        }
        if (timeToLive == null || timeUnit == null) {
            timeToLive = 3L;
            timeUnit = TimeUnit.MINUTES;
        }
        Cache cache = CacheUtils.getCache(cacheName);
        if (cache == null || cache instanceof NoCache) {
            CacheUtils.registerCache(new SpringSimpleCache(cacheName,
                    Dict.of("referenceType", ReferenceType.SOFT)
                            .set("timeToLive", timeToLive)
                            .set("timeToLiveUnit", timeUnit)));
        }
        optionProvider = new CacheOptionProvider(optionProvider, cacheName, timeToLive, timeUnit);
        return optionProvider;
    }

    protected OptionProvider handleCustom(ApplicationContext context, OptionProperties properties) {
        OptionProperties.CustomConfig customConfig = properties.getCustomConfig();
        Assert.notNull(customConfig, "Parameter \"customConfig\" must not null. ");
        Class<? extends OptionProvider> beanType = customConfig.getSpringContextBeanType();
        String beanName = customConfig.getSpringContextBeanName();
        boolean notBlank = StringUtils.isNotBlank(beanName);
        OptionProvider optionProvider;
        if (notBlank && beanType == null) {
            optionProvider = context.getBean(beanName, OptionProvider.class);
        }
        else if (notBlank) {
            optionProvider = context.getBean(beanName, beanType);
        }
        else if (beanType != null) {
            optionProvider = context.getBean(beanType);
        }
        else {
            throw new IllegalArgumentException(
                "Configuration items \"spring-context-bean-name\" and \"spring-context-bean-type\" cannot both be empty. "
            );
        }
        return optionProvider;
    }

}
