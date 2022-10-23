package artoria.data.bean.support;

import artoria.convert.ConversionProvider;
import artoria.data.bean.BeanCopier;
import org.springframework.beans.BeanUtils;

/**
 * The spring bean copier.
 * @author Kahle
 */
public class SpringBeanCopier implements BeanCopier {

    @Override
    public void copy(Object from, Object to, ConversionProvider conversionProvider) {

        BeanUtils.copyProperties(from, to);
    }

}
