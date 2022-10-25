package artoria.data.bean.support;

import artoria.data.bean.BeanCopier;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import org.junit.Test;

/**
 * The apache bean copier Test.
 * @author Kahle
 */
public class ApacheBeanCopierTest extends AbstractBeanCopierTest {
    private static final Logger log = LoggerFactory.getLogger(ApacheBeanCopierTest.class);
    private static final BeanCopier beanCopier = new ApacheBeanCopier();

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
