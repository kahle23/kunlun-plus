package artoria.data.bean.support;

import artoria.data.bean.BeanCopier;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
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
