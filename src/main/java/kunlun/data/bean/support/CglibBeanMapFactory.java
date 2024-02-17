/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.bean.support;

import kunlun.convert.ConversionService;
import kunlun.convert.ConversionUtils;
import kunlun.data.bean.BeanMap;
import kunlun.data.bean.BeanMapFactory;
import kunlun.util.Assert;

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
