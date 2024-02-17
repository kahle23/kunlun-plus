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
 * The cglib bean copier Test.
 * @author Kahle
 */
public class CglibBeanCopierTest extends AbstractBeanCopierTest {
    private static final Logger log = LoggerFactory.getLogger(CglibBeanCopierTest.class);
    private static final BeanCopier beanCopier = new CglibBeanCopier();

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

        doCopyObjToObjToTestTypeConvert(beanCopier);
    }

    @Test
    public void testCopyObjToOtherObjToTestPropertyList() {

        doCopyObjToOtherObjToTestPropertyList(beanCopier);
    }

}
