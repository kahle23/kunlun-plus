package artoria.data.bean.support;

import artoria.convert.ConversionService;
import artoria.convert.ConversionUtils;
import artoria.data.bean.BeanMap;
import artoria.data.bean.BeanMapFactory;
import artoria.util.Assert;

/**
 * The spring cglib bean map factory.
 * @author Kahle
 */
public class SpringCglibBeanMapFactory implements BeanMapFactory {
    private final ConversionService conversionService;

    public SpringCglibBeanMapFactory() {

        this(ConversionUtils.getConversionService());
    }

    public SpringCglibBeanMapFactory(ConversionService conversionService) {
        Assert.notNull(conversionService, "Parameter \"conversionService\" must not null. ");
        this.conversionService = conversionService;
    }

    @Override
    public BeanMap getInstance(Object bean) {
        SpringCglibBeanMap beanMap = new SpringCglibBeanMap(conversionService);
        if (bean != null) { beanMap.setBean(bean); }
        return beanMap;
    }

}
