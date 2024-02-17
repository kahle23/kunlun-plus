/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.xml.support;

import com.alibaba.fastjson.JSON;
import kunlun.data.xml.XmlClassAlias;
import kunlun.data.xml.XmlFieldAlias;
import kunlun.data.xml.XmlHandler;
import kunlun.entity.Student;
import kunlun.logging.Logger;
import kunlun.logging.LoggerFactory;
import kunlun.mock.MockUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class XStreamXmlHandlerTest {
    private static final Logger log = LoggerFactory.getLogger(XStreamXmlHandlerTest.class);
    private final XmlHandler xmlHandler = new XStreamXmlHandler();

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

        String xmlString = xmlHandler.toXmlString(list, arguments);
        log.info("\n{}", xmlString);
        List<Student> parseList = xmlHandler.parseObject(xmlString, List.class, arguments);
        log.info("\n{}", JSON.toJSONString(parseList, true));
    }

}
