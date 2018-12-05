package artoria.document.excel;

import artoria.beans.BeanUtils;
import artoria.random.RandomUtils;
import com.alibaba.fastjson.JSON;
import org.junit.Ignore;
import org.junit.Test;
import artoria.document.entity.Student;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Ignore
public class ExcelTest {
    private static Map<String, String> titleStuMap = new HashMap<String, String>();
    private static Map<String, String> stuTitleMap = new HashMap<String, String>();
    private static List<Student> students = new ArrayList<Student>();
    private static List<Map<String, Object>> studentsMap = new ArrayList<Map<String, Object>>();

    static {
        titleStuMap.put("Student Name", "name");
        titleStuMap.put("Student Age", "age");
        titleStuMap.put("Student Height", "height");
        titleStuMap.put("Student Id", "studentId");
        titleStuMap.put("School Name", "schoolName");
        for (Map.Entry<String, String> entry : titleStuMap.entrySet()) {
            stuTitleMap.put(entry.getValue(), entry.getKey());
        }
        for (int i = 0; i < 5; i++) {
            students.add(RandomUtils.nextObject(Student.class));
        }
        studentsMap.addAll(BeanUtils.beanToMapInList(students));
    }

    @Test
    public void test1() throws Exception {
        Excel excel = Excel.create(new File("e:\\123.xlsx"));
        List<Student> students = excel.readToBeans(Student.class, null, 4, 5);
        System.out.println(JSON.toJSONString(students, true));
    }

    @Test
    public void test2() throws Exception {
        Excel excel = Excel.create(new File("e:\\123_1.xlsx"));
        List<Student> students = excel.readToBeans(Student.class, titleStuMap, 3, 2);
        System.out.println(JSON.toJSONString(students, true));
    }

    @Test
    public void test3() throws Exception {
        System.out.println(JSON.toJSONString(students, true));
        Excel excel = Excel.create(null, Excel.XLSX, students, stuTitleMap, 3, 4);
        excel.write(new File("e:\\456.xlsx"));
    }

    @Test
    public void test4() throws Exception {
        System.out.println(JSON.toJSONString(students, true));
        Excel template = Excel.create(new File("e:\\template.xlsx"));
        Excel excel = Excel.create(template, Excel.XLSX, students, stuTitleMap, 3, 4);
        excel.write(new File("e:\\456.xlsx"));
    }

    @Test
    public void test5() throws Exception {
        System.out.println(JSON.toJSONString(students, true));
        Excel excel = Excel.create(null, Excel.XLSX, studentsMap, stuTitleMap, 0, 0);
        excel.write(new File("e:\\456.xlsx"));
    }

    @Test
    public void test6() throws Exception {
        Excel excel = Excel.create(new File("e:\\456.xlsx"));
        List<Map<String, Object>> list = excel.readToMapList(titleStuMap, 3, 4);
        System.out.println(JSON.toJSONString(list, true));
    }

}
