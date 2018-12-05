package artoria.util;

import artoria.exception.UncheckedException;

import java.lang.reflect.Method;
import java.util.*;

import static artoria.common.Constants.*;

/**
 * Data handle extend tools.
 * @author Kahle
 */
@Deprecated
public class DataUtils1 extends DataUtils {

    public static <T> List<T> multilevelList(List<T> data, String sonList, String sign, String parentSign, String... relationProperties) {
        try {
            if (CollectionUtils.isEmpty(data)) { return null; }
            Class<?> clazz = data.get(0).getClass();
            Method signMethod = clazz.getMethod(GET + StringUtils.capitalize(sign));
            Method parentMethod = clazz.getMethod(GET + StringUtils.capitalize(parentSign));
            Method sonListMethod = clazz.getMethod(SET + StringUtils.capitalize(sonList), List.class);
            List<Method> relations = new ArrayList<Method>();
            for (String relationProperty : relationProperties) {
                Method method = clazz.getMethod(GET + StringUtils.capitalize(relationProperty));
                relations.add(method);
            }
            boolean hasRelation = relations.size() > 0;

            // key = Parent + relation， value = son list
            Map<String, List<T>> map = new HashMap<String, List<T>>(data.size() + 1);
            map.put(ROOT, new ArrayList<T>());
            for (T obj : data) {
                Object parent = parentMethod.invoke(obj);
                Object signObj = signMethod.invoke(obj);
                StringBuilder relation = new StringBuilder();
                if (hasRelation) {
                    for (Method method : relations) {
                        relation.append(method.invoke(obj));
                    }
                }
                String parentKey = parent == null ? "" : parent + "";
                // when parent is blank, obj is root element
                if (StringUtils.isBlank(parentKey)) { map.get(ROOT).add(obj); }
                parentKey = hasRelation ? parentKey + relation : parentKey;

                // create parent list
                List<T> list = map.get(parentKey);
                if (list == null) {
                    list = new ArrayList<T>();
                    map.put(parentKey, list);
                }
                list.add(obj);

                // create sign list
                String signKey = signObj == null ? "" : signObj + "";
                Assert.notBlank(signKey, String.format("Sign \"%s\"'s value \"%s\" must not blank. ", sign, signObj));
                signKey = hasRelation ? signKey + relation : signKey;
                list = map.get(signKey);
                if (list == null) {
                    list = new ArrayList<T>();
                    map.put(signKey, list);
                }
                sonListMethod.invoke(obj, list);
            }

            return map.get(ROOT);
        }
        catch (Exception e) {
            throw new UncheckedException(e);
        }
    }

    public static <T> List<T> distinct(List<T> data, String... propertyNames) {
        try {
            if (CollectionUtils.isEmpty(data)) { return data; }
            Class<?> clazz = data.get(0).getClass();
            Assert.notEmpty(propertyNames, "Parameter \"propertyNames\" must not empty. ");
            List<Method> referMethods = new ArrayList<Method>();
            for (String relationProperty : propertyNames) {
                Method method = clazz.getMethod(GET + StringUtils.capitalize(relationProperty));
                referMethods.add(method);
            }

            LinkedHashMap<String, T> map = new LinkedHashMap<String, T>();
            StringBuilder key = new StringBuilder();
            for (T t : data) {
                key.setLength(0);
                for (Method m : referMethods) {
                    key.append(m.invoke(t));
                }
                map.put(key.toString(), t);
            }

            data = new ArrayList<T>();
            data.addAll(map.values());
            return data;
        }
        catch (Exception e) {
            throw new UncheckedException(e);
        }
    }

}
