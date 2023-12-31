package artoria.data.bean.support;

import artoria.convert.ConversionService;
import artoria.convert.ConversionUtils;
import artoria.data.bean.BeanMap;
import artoria.data.bean.BeanMapFactory;
import artoria.util.Assert;

/**
 * The cglib bean map factory.
 * @author Kahle
 */
public class CglibBeanMapFactory implements BeanMapFactory {
    private final ConversionService conversionService;

    public CglibBeanMapFactory() {

        this(ConversionUtils.getConversionService());
    }

    public CglibBeanMapFactory(ConversionService conversionService) {
        Assert.notNull(conversionService, "Parameter \"conversionService\" must not null. ");
        this.conversionService = conversionService;
    }

    @Override
    public BeanMap getInstance(Object bean) {
        CglibBeanMap beanMap = new CglibBeanMap(conversionService);
        if (bean != null) { beanMap.setBean(bean); }
        return beanMap;
    }

}
