package artoria.data.bean.support;

import artoria.convert.ConversionService;
import artoria.data.bean.BeanCopier;
import artoria.util.Assert;

/**
 * The cglib bean copier.
 * @author Kahle
 */
public class CglibBeanCopier implements BeanCopier {

    /**
     * The cglib converter adapter.
     * @author Kahle
     */
    private static class CglibConverterAdapter implements net.sf.cglib.core.Converter {
        private final ConversionService conversionService;
        private final Boolean useConversion;

        public CglibConverterAdapter(ConversionService conversionService) {
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
        net.sf.cglib.beans.BeanCopier copier =
                net.sf.cglib.beans.BeanCopier.create(fromClass, toClass, useConverter);
        CglibConverterAdapter adapter = new CglibConverterAdapter(conversionService);
        copier.copy(from, to, adapter);
    }

}
