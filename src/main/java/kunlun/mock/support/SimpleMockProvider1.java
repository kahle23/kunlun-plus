/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.mock.support;

import kunlun.mock.SimpleMockProvider;
import kunlun.util.ArrayUtils;
import kunlun.util.StringUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;

public class SimpleMockProvider1 extends SimpleMockProvider {

    protected Map<Class, ClassMockerConfig> parseFeatures(Object[] arguments) {
        if (ArrayUtils.isEmpty(arguments)) { return emptyMap(); }
        Map<Class, ClassMockerConfig> map = new HashMap<Class, ClassMockerConfig>();
        for (Object feature : arguments) {
            if (feature == null) { continue; }
            if (feature instanceof ClassMockerConfig) {
                ClassMockerConfig config = (ClassMockerConfig) feature;
                Class<?> type = config.getType();
                map.put(type, config);
            }
        }
        return map;
    }

    @Override
    protected Object mockClassData(Class<?> attrType, String attrName
            , Object[] arguments, int nested, Class<?> originalType, Type... genericTypes) {
        if (StringUtils.isNotBlank(attrName) && originalType != null) {
            Map<Class, ClassMockerConfig> map = parseFeatures(arguments);
            ClassMockerConfig config = map.get(originalType);
            if (config!=null&&config.getMockerMap().get(attrName)!=null) {
                Mocker mocker = config.getMockerMap().get(attrName);
                return mocker.mock();
            }
        }
        return super.mockClassData(attrType, attrName, arguments, nested, originalType, genericTypes);
    }

}
