/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.comparison.support.difference;

import kunlun.core.Comparator;
import kunlun.data.annotation.FieldInfo;
import kunlun.exception.ExceptionUtils;
import kunlun.reflect.ReflectUtils;
import kunlun.util.ArrayUtils;
import kunlun.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static kunlun.common.constant.Symbols.EMPTY_STRING;

/**
 * The abstract field comparator.
 * @author Kahle
 */
public abstract class AbstractFieldComparator implements Comparator {

    protected String describe(Field field) {
        FieldInfo annotation = field.getAnnotation(FieldInfo.class);
        return annotation != null ? annotation.description() : EMPTY_STRING;
    }

    protected List<FieldEntity> convert(Object object) {
        try {
            Field[] fields = ReflectUtils.getDeclaredFields(object.getClass());
            if (ArrayUtils.isEmpty(fields)) { return Collections.emptyList(); }
            List<FieldEntity> result = new ArrayList<FieldEntity>();
            for (Field field : fields) {
                ReflectUtils.makeAccessible(field);
                FieldEntity entity = new FieldEntity();
                entity.setName(field.getName());
                entity.setValue(field.get(object));
                entity.setDescription(describe(field));
                result.add(entity);
            }
            return result;
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    protected boolean equals(Object leftValue, Object rightValue) {

        return ObjectUtils.equals(leftValue, rightValue);
    }

    protected static class FieldEntity {
        private String description;
        private String name;
        private Object value;

        public String getName() {

            return name;
        }

        public void setName(String name) {

            this.name = name;
        }

        public String getDescription() {

            return description;
        }

        public void setDescription(String description) {

            this.description = description;
        }

        public Object getValue() {

            return value;
        }

        public void setValue(Object value) {

            this.value = value;
        }
    }

}
