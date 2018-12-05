package artoria.beans;

import artoria.converter.TypeConverter;
import artoria.util.Assert;
import artoria.util.CollectionUtils;
import artoria.util.StringUtils;

import java.util.List;

/**
 * Cglib bean copier.
 * @author Kahle
 */
public class CglibBeanCopier implements BeanCopier {
    private static final Integer GET_OR_SET_LENGTH = 3;

    private static class CglibConverterAdapter implements net.sf.cglib.core.Converter {
        private TypeConverter typeConverter;
        private List<String> ignoreProperties;
        private boolean hasIgnore;

        public void setTypeConverter(TypeConverter typeConverter) {
            Assert.notNull(typeConverter, "Parameter \"typeConverter\" must not null. ");
            this.typeConverter = typeConverter;
        }

        public CglibConverterAdapter(TypeConverter typeConverter, List<String> ignoreProperties) {
            this.setTypeConverter(typeConverter);
            this.ignoreProperties = ignoreProperties;
            this.hasIgnore = CollectionUtils.isNotEmpty(ignoreProperties);
        }

        @Override
        public Object convert(Object value, Class valClass, Object methodName) {
            // Method name must be setter, because cglib already processed.
            if (this.hasIgnore && this.ignoreProperties.contains(
                    StringUtils.uncapitalize(((String) methodName).substring(GET_OR_SET_LENGTH))
            )) {
                return null;
            }
            return this.typeConverter.convert(value, valClass);
        }

    }

    @Override
    public void copy(Object from, Object to, List<String> ignoreProperties, TypeConverter typeConverter) {
        Class<?> fromClass = from.getClass();
        Class<?> toClass = to.getClass();
        net.sf.cglib.beans.BeanCopier copier =
                net.sf.cglib.beans.BeanCopier.create(fromClass, toClass, true);
        CglibConverterAdapter adapter = new CglibConverterAdapter(typeConverter, ignoreProperties);
        copier.copy(from, to, adapter);
    }

}
