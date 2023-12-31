package artoria.data.bean.support;

import artoria.convert.ConversionService;
import artoria.data.bean.BeanMap;
import artoria.util.ObjectUtils;
import org.springframework.lang.NonNull;

import java.util.Set;

/**
 * The spring cglib bean map.
 * @author Kahle
 */
public class SpringCglibBeanMap extends BeanMap {
    private org.springframework.cglib.beans.BeanMap beanMap;

    public SpringCglibBeanMap() {
    }

    public SpringCglibBeanMap(Object bean) {

        setBean(bean);
    }

    public SpringCglibBeanMap(ConversionService conversionService) {

        setConversionService(conversionService);
    }

    public SpringCglibBeanMap(ConversionService conversionService, Object bean) {
        setConversionService(conversionService);
        setBean(bean);
    }

    @Override
    public void setBean(Object bean) {
        super.setBean(bean);
        this.beanMap = org.springframework.cglib.beans.BeanMap.create(bean);
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
