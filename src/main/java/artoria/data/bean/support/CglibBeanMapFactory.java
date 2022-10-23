package artoria.data.bean.support;

import artoria.convert.ConversionProvider;
import artoria.convert.ConversionUtils;
import artoria.data.bean.BeanMap;
import artoria.data.bean.BeanMapFactory;
import artoria.util.Assert;

/**
 * The cglib bean map factory.
 * @author Kahle
 */
public class CglibBeanMapFactory implements BeanMapFactory {
    private final ConversionProvider conversionProvider;

    public CglibBeanMapFactory() {

        this(ConversionUtils.getConversionProvider());
    }

    public CglibBeanMapFactory(ConversionProvider conversionProvider) {
        Assert.notNull(conversionProvider, "Parameter \"conversionProvider\" must not null. ");
        this.conversionProvider = conversionProvider;
    }

    @Override
    public BeanMap getInstance(Object bean) {
        CglibBeanMap beanMap = new CglibBeanMap(conversionProvider);
        if (bean != null) { beanMap.setBean(bean); }
        return beanMap;
    }

}
