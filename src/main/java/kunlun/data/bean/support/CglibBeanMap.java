/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.bean.support;

import kunlun.convert.ConversionService;
import kunlun.data.bean.BeanMap;
import kunlun.util.ObjectUtils;
import org.springframework.lang.NonNull;

import java.util.Set;

/**
 * The cglib bean map.
 * @author Kahle
 */
public class CglibBeanMap extends BeanMap {
    private net.sf.cglib.beans.BeanMap beanMap;

    public CglibBeanMap() {
    }

    public CglibBeanMap(Object bean) {

        setBean(bean);
    }

    public CglibBeanMap(ConversionService conversionService) {

        setConversionService(conversionService);
    }

    public CglibBeanMap(ConversionService conversionService, Object bean) {
        setConversionService(conversionService);
        setBean(bean);
    }

    @Override
    public void setBean(Object bean) {
        super.setBean(bean);
        this.beanMap = net.sf.cglib.beans.BeanMap.create(bean);
    }

    @Override
    protected Object get(Object bean, Object key) {

        return beanMap.get(key);
    }

    @Override
    protected Object put(Object bean, Object key, Object value) {
        if (key != null && getConversionService() != null) {
            Class type = beanMap.getPropertyType((String) key);
            value = getConversionService().convert(value, type);
        }
        return beanMap.put(key, value);
    }

    @NonNull
    @Override
    public Set<Object> keySet() {

        return ObjectUtils.cast(beanMap.keySet());
    }

}
