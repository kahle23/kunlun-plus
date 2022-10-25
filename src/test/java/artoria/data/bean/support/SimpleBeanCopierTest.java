package artoria.data.bean.support;

import artoria.data.bean.BeanCopier;
import artoria.data.bean.SimpleBeanCopier;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
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
