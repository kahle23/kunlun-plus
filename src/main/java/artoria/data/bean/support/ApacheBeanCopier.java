package artoria.data.bean.support;

import artoria.convert.ConversionService;
import artoria.data.bean.BeanCopier;
import artoria.exception.ExceptionUtils;
import org.apache.commons.beanutils.BeanUtils;

/**
 * The apache bean copier.
 * @author Kahle
 */
public class ApacheBeanCopier implements BeanCopier {

    @Override
    public void copy(Object from, Object to, ConversionService conversionService) {
        try {
            BeanUtils.copyProperties(to, from);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

}
