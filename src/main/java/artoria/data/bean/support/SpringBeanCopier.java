package artoria.data.bean.support;

import artoria.convert.ConversionService;
import artoria.data.bean.BeanCopier;
import org.springframework.beans.BeanUtils;

/**
 * The spring bean copier.
 * @author Kahle
 */
public class SpringBeanCopier implements BeanCopier {

    @Override
    public void copy(Object from, Object to, ConversionService conversionService) {

        BeanUtils.copyProperties(from, to);
    }

}
