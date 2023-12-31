package artoria.data.bean.support;

import artoria.convert.ConversionService;
import artoria.data.bean.BeanCopier;
import artoria.util.Assert;

/**
 * The spring cglib bean copier.
 * @author Kahle
 */
public class SpringCglibBeanCopier implements BeanCopier {

    /**
     * The spring cglib converter adapter.
     * @author Kahle
     */
    private static class SpringCglibConverterAdapter implements org.springframework.cglib.core.Converter {
        private final ConversionService conversionService;
        private final Boolean useConversion;

        public SpringCglibConverterAdapter(ConversionService conversionService) {
            this.conversionService = conversionService;
            this.useConversion = conversionService != null;
        }

        @Override
        public Object convert(Object value, Class target, Object context) {

            return useConversion ? conversionService.convert(value, target) : value;
        }

    }

    @Override
    public void copy(Object from, Object to, ConversionService conversionService) {
        Assert.notNull(from, "Parameter \"from\" must is not null. ");
        Assert.notNull(to, "Parameter \"to\" must is not null. ");
        Class<?> fromClass = from.getClass();
        Class<?> toClass = to.getClass();
        boolean useConverter = conversionService != null;
        org.springframework.cglib.beans.BeanCopier copier =
                org.springframework.cglib.beans.BeanCopier.create(fromClass, toClass, useConverter);
        SpringCglibConverterAdapter adapter = new SpringCglibConverterAdapter(conversionService);
        copier.copy(from, to, adapter);
    }

}
