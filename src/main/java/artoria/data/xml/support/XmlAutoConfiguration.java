package artoria.data.xml.support;

import artoria.data.xml.XmlUtils;
import artoria.util.ClassLoaderUtils;
import artoria.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import static artoria.data.xml.XmlUtils.getDefaultHandlerName;

/**
 * The xml tools auto-configuration.
 * @author Kahle
 */
@Configuration
public class XmlAutoConfiguration implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(XmlAutoConfiguration.class);
    private static final String X_STREAM_CLASS = "com.thoughtworks.xstream.XStream";

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        if (ClassUtils.isPresent(X_STREAM_CLASS, classLoader)) {
            XStreamXmlHandler xmlHandler = new XStreamXmlHandler();
            XmlUtils.registerHandler(getDefaultHandlerName(), xmlHandler);
            XmlUtils.registerHandler("xstream", xmlHandler);
        }
//        else if (ClassUtils.isPresent("", classLoader)) {
//            XmlUtils.registerHandler();
//        }
        else { log.warn("Can not found any implementation, will keep default. "); }
    }

    @Override
    public void destroy() throws Exception {

    }

}
