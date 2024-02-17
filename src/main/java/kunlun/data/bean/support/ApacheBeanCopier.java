/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.bean.support;

import kunlun.convert.ConversionService;
import kunlun.data.bean.BeanCopier;
import kunlun.exception.ExceptionUtils;
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
