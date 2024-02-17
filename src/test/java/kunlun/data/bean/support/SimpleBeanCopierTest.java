/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.bean.support;

import kunlun.data.bean.BeanCopier;
import kunlun.logging.Logger;
import kunlun.logging.LoggerFactory;
import org.junit.Test;

/**
 * The simple bean copier Test.
 * @author Kahle
 */
public class SimpleBeanCopierTest extends AbstractBeanCopierTest {
    private static final Logger log = LoggerFactory.getLogger(SimpleBeanCopierTest.class);
    private static final BeanCopier beanCopier = new SimpleBeanCopier();

    @Test
    public void testCopyObjectToMap() {

        doCopyObjectToMap(beanCopier);
    }

    @Test
    public void testCopyMapToObject() {

        doCopyMapToObject(beanCopier);
    }

    @Test
    public void testCopyNullToValue() {

        doCopyNullToValue(beanCopier);
    }

    @Test
    public void testCopyObjToObjToTestTypeConvert() {
        ((SimpleBeanCopier) beanCopier).setIgnoreException(false);
        doCopyObjToObjToTestTypeConvert(beanCopier);
    }

    @Test
    public void testCopyObjToOtherObjToTestPropertyList() {

        doCopyObjToOtherObjToTestPropertyList(beanCopier);
    }

}
