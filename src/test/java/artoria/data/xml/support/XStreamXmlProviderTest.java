package artoria.data.xml.support;

import artoria.data.xml.XmlClassAlias;
import artoria.data.xml.XmlFieldAlias;
import artoria.data.xml.XmlProvider;
import artoria.entity.Student;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.mock.MockUtils;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class XStreamXmlProviderTest {
    private static final Logger log = LoggerFactory.getLogger(XStreamXmlProviderTest.class);
    private final XmlProvider xmlProvider = new XStreamXmlProvider();

    @Test
    public void test1() {
        List<Student> list = new ArrayList<Student>();
        list.add(MockUtils.mock(Student.class));
        list.add(MockUtils.mock(Student.class));

        Object[] arguments = new Object[] {
                new XmlFieldAlias("id", Student.class, "studentId"),
                new XmlFieldAlias("schName", Student.class, "schoolName"),
                new XmlClassAlias("student", Student.class),
                new XmlClassAlias("xml", List.class),
        };

        String xmlString = xmlProvider.toXmlString(list, arguments);
        log.info("\n{}", xmlString);
        List<Student> parseList = xmlProvider.parseObject(xmlString, List.class, arguments);
        log.info("\n{}", JSON.toJSONString(parseList, true));
    }

}
