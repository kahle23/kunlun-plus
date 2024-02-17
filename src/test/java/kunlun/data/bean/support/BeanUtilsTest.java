/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.bean.support;

import com.alibaba.fastjson.JSON;
import kunlun.data.bean.BeanUtils;
import kunlun.entity.Person;
import kunlun.entity.Student;
import kunlun.mock.MockUtils;
import org.junit.Test;

/**
 * The bean tools Test.
 * @author Kahle
 */
public class BeanUtilsTest {

    @Test
    public void testIgnoreCglibCopy() {
        Person person = MockUtils.mock(Person.class);
        // BeanUtils.setBeanCopier(new CglibBeanCopier());
        BeanUtils.setBeanCopier(new SpringCglibBeanCopier());
        Student student = new Student();
        BeanUtils.copy(person, student);
        System.out.println(JSON.toJSONString(student));
    }

}
