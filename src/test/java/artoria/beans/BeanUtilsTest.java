package artoria.beans;

import artoria.random.RandomUtils;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import artoria.entity.Person;
import artoria.entity.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BeanUtilsTest {

    @Test
    public void testIgnoreCglibCopy() {
        Person person = RandomUtils.nextObject(Person.class);
        // BeanUtils.setBeanCopier(new CglibBeanCopier());
        BeanUtils.setBeanCopier(new SpringCglibBeanCopier());
        List<String> ignore = new ArrayList<String>();
        Collections.addAll(ignore, "name", "age", "123test");
        Student student = new Student();
        BeanUtils.copy(person, student, ignore);
        System.out.println(JSON.toJSONString(student));
    }

}
