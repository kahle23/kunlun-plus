package artoria.convert;

import artoria.cache.CacheUtils;
import artoria.cache.support.SpringSimpleCache;
import artoria.data.Dict;
import artoria.data.ReferenceType;
import artoria.util.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
//@AutoConfigureAfter({CacheAutoConfiguration.class, SpringCacheAdapterAutoConfiguration.class})
//@ConditionalOnProperty(name = "artoria.cache.enabled", havingValue = "true")
//@EnableConfigurationProperties({CacheProperties.class})
public class ConversionAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ConversionAutoConfiguration.class);

    @Autowired
    public ConversionAutoConfiguration(ApplicationContext applicationContext) {
        log.info("Start handling the conversion service provider's cache...");
        String cacheName="cache-conversion-provider-cache";
        // Build cache configuration.
        Dict cacheConfig = Dict.of("referenceType", ReferenceType.SOFT);
        cacheConfig.set("timeToLive", 4L);
        cacheConfig.set("timeToLiveUnit", TimeUnit.HOURS);
        //
        CacheUtils.registerCache(cacheName, new SpringSimpleCache(cacheConfig));
        //
        ConversionService conversionService = ConversionUtils.getConversionService();
        ConversionUtils.setConversionService(new CacheConversionService(conversionService, cacheName));
        log.info(">> The conversion service provider increased cache successfully. ");
        //
        registerSpringConverter(applicationContext);
    }

    private void registerSpringConverter(ApplicationContext applicationContext) {
        Map<String, GenericConverter> beansOfType = applicationContext.getBeansOfType(GenericConverter.class);
        if (MapUtils.isEmpty(beansOfType)) { return; }
        for (Map.Entry<String, GenericConverter> entry : beansOfType.entrySet()) {
            GenericConverter converter = entry.getValue();
            if (converter == null) { continue; }
            ConversionUtils.registerConverter(converter);
        }
    }

}
