package artoria.generator.java;

import artoria.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static artoria.common.Constants.LEFT_PARENTHESIS;
import static artoria.io.IOUtils.EOF;

/**
 * Column field type mapper.
 * @author Kahle
 */
public class TypeMapper {
    private static final String OBJECT_CLASS = "java.lang.Object";
    private static Map<String, String> map = new HashMap<String, String>();

    static {
        // java.util.Data can not be returned
        // java.sql.Date, java.sql.Time, java.sql.Timestamp all extends java.util.Data so getDate can return the three types data
        map.put("java.util.Date", "java.util.Date");

        // date, year
        map.put("java.sql.Date", "java.util.Date");

        // time
        map.put("java.sql.Time", "java.util.Date");

        // timestamp, datetime
        map.put("java.sql.Timestamp", "java.util.Date");

        // binary, varbinary, tinyblob, blob, mediumblob, longblob
        // qjd project: print_info.content varbinary(61800);
        map.put("[B", "byte[]");

        // varchar, char, enum, set, text, tinytext, mediumtext, longtext
        map.put("java.lang.String", "java.lang.String");

        // int, integer, tinyint, smallint, mediumint
        map.put("java.lang.Integer", "java.lang.Integer");

        // bigint
        map.put("java.lang.Long", "java.lang.Long");

        // real, double
        map.put("java.lang.Double", "java.lang.Double");

        // float
        map.put("java.lang.Float", "java.lang.Float");

        // bit
        map.put("java.lang.Boolean", "java.lang.Boolean");

        // decimal, numeric
        map.put("java.math.BigDecimal", "java.math.BigDecimal");

        // unsigned bigint
        map.put("java.math.BigInteger", "java.math.BigInteger");

        // short
        map.put("java.lang.Short", "java.lang.Short");

        // object
        map.put("java.lang.Object", "java.lang.Object");
    }

    public static String getType(String columnClassName, String columnType) {
        String result = map.get(columnClassName);
        boolean checkColumnType = result == null || OBJECT_CLASS.equals(result);
        checkColumnType = checkColumnType && StringUtils.isNotBlank(columnType);
        if (checkColumnType) {
            int index = columnType.indexOf(LEFT_PARENTHESIS);
            if (index != EOF) {
                columnType = columnType.substring(0, index);
            }
            result = map.get(columnType);
        }
        return result == null ? columnClassName : result;
    }

    public static void unregister(String columnClassNameOrType) {

        map.remove(columnClassNameOrType);
    }

    public static void register(String columnClassNameOrType, String javaClassName) {

        map.put(columnClassNameOrType, javaClassName);
    }

}
