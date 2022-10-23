package artoria.data.bean.support;

import artoria.convert.ConversionProvider;
import artoria.convert.ConversionUtils;
import artoria.data.bean.BeanMap;
import artoria.data.bean.BeanMapFactory;
import artoria.util.Assert;

/**
 * The spring cglib bean map factory.
 * @author Kahle
 */
public class SpringCglibBeanMapFactory implements BeanMapFactory {
    private final ConversionProvider conversionProvider;

    public SpringCglibBeanMapFactory() {

        this(ConversionUtils.getConversionProvider());
    }

    public SpringCglibBeanMapFactory(ConversionProvider conversionProvider) {
        Assert.notNull(conversionProvider, "Parameter \"conversionProvider\" must not null. ");
        this.conversionProvider = conversionProvider;
    }

    @Override
    public BeanMap getInstance(Object bean) {
        SpringCglibBeanMap beanMap = new SpringCglibBeanMap(conversionProvider);
        if (bean != null) { beanMap.setBean(bean); }
        return beanMap;
    }

}
