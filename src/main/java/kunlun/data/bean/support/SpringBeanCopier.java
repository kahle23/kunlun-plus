/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.bean.support;

import kunlun.convert.ConversionService;
import kunlun.data.bean.BeanCopier;
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
