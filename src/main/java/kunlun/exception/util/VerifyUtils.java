/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.exception.util;

import kunlun.data.CodeDefinition;
import kunlun.data.validation.ValidatorUtils;
import kunlun.exception.BusinessException;
import kunlun.util.ArrayUtils;
import kunlun.util.CollectionUtils;
import kunlun.util.MapUtils;
import kunlun.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * The verify tools.
 * @author Kahle
 */
public class VerifyUtils {

    public static void isFalse(boolean expression, CodeDefinition errorCode, Object... arguments) {
        if (expression) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new BusinessException(message);
        }
    }


    public static void isTrue(boolean expression, CodeDefinition errorCode, Object... arguments) {
        if (!expression) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new BusinessException(message);
        }
    }


    public static void isNull(Object object, CodeDefinition errorCode, Object... arguments) {
        if (object != null) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new BusinessException(message);
        }
    }


    public static void notNull(Object object, CodeDefinition errorCode, Object... arguments) {
        if (object == null) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new BusinessException(message);
        }
    }


    public static void isEmpty(byte[] array, CodeDefinition errorCode, Object... arguments) {
        if (ArrayUtils.isNotEmpty(array)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isEmpty(byte[] array, String message) {
        if (ArrayUtils.isNotEmpty(array)) {
            throw new BusinessException(message);
        }
    }


    public static void notEmpty(byte[] array, CodeDefinition errorCode, Object... arguments) {
        if (ArrayUtils.isEmpty(array)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void notEmpty(byte[] array, String message) {
        if (ArrayUtils.isEmpty(array)) {
            throw new BusinessException(message);
        }
    }


    public static void isEmpty(Object[] array, CodeDefinition errorCode, Object... arguments) {
        if (ArrayUtils.isNotEmpty(array)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isEmpty(Object[] array, String message) {
        if (ArrayUtils.isNotEmpty(array)) {
            throw new BusinessException(message);
        }
    }


    public static void notEmpty(Object[] array, CodeDefinition errorCode, Object... arguments) {
        if (ArrayUtils.isEmpty(array)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void notEmpty(Object[] array, String message) {
        if (ArrayUtils.isEmpty(array)) {
            throw new BusinessException(message);
        }
    }


    public static void isEmpty(Collection<?> collection, CodeDefinition errorCode, Object... arguments) {
        if (CollectionUtils.isNotEmpty(collection)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isNotEmpty(collection)) {
            throw new BusinessException(message);
        }
    }


    public static void notEmpty(Collection<?> collection, CodeDefinition errorCode, Object... arguments) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void notEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(message);
        }
    }


    public static void isEmpty(Map<?, ?> map, CodeDefinition errorCode, Object... arguments) {
        if (MapUtils.isNotEmpty(map)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isEmpty(Map<?, ?> map, String message) {
        if (MapUtils.isNotEmpty(map)) {
            throw new BusinessException(message);
        }
    }


    public static void notEmpty(Map<?, ?> map, CodeDefinition errorCode, Object... arguments) {
        if (MapUtils.isEmpty(map)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void notEmpty(Map<?, ?> map, String message) {
        if (MapUtils.isEmpty(map)) {
            throw new BusinessException(message);
        }
    }


    public static void isEmpty(String text, CodeDefinition errorCode, Object... arguments) {
        if (StringUtils.isNotEmpty(text)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isEmpty(String text, String message) {
        if (StringUtils.isNotEmpty(text)) {
            throw new BusinessException(message);
        }
    }


    public static void notEmpty(String text, CodeDefinition errorCode, Object... arguments) {
        if (StringUtils.isEmpty(text)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void notEmpty(String text, String message) {
        if (StringUtils.isEmpty(text)) {
            throw new BusinessException(message);
        }
    }


    public static void isBlank(String text, CodeDefinition errorCode, Object... arguments) {
        if (StringUtils.isNotBlank(text)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isBlank(String text, String message) {
        if (StringUtils.isNotBlank(text)) {
            throw new BusinessException(message);
        }
    }


    public static void notBlank(String text, CodeDefinition errorCode, Object... arguments) {
        if (StringUtils.isBlank(text)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void notBlank(String text, String message) {
        if (StringUtils.isBlank(text)) {
            throw new BusinessException(message);
        }
    }


    public static void isContain(String textToSearch, String substring, CodeDefinition errorCode, Object... arguments) {
        if (!textToSearch.contains(substring)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isContain(String textToSearch, String substring, String message) {
        if (!textToSearch.contains(substring)) {
            throw new BusinessException(message);
        }
    }


    public static void notContain(String textToSearch, String substring, CodeDefinition errorCode, Object... arguments) {
        if (textToSearch.contains(substring)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void notContain(String textToSearch, String substring, String message) {
        if (textToSearch.contains(substring)) {
            throw new BusinessException(message);
        }
    }


    public static void isAssignable(Class<?> superType, Class<?> subType, CodeDefinition errorCode, Object... arguments) {
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isAssignable(Class<?> superType, Class<?> subType, String message) {
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new BusinessException(message);
        }
    }


    public static void isInstanceOf(Class<?> type, Object object, CodeDefinition errorCode, Object... arguments) {
        if (!type.isInstance(object)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isInstanceOf(Class<?> type, Object object, String message) {
        if (!type.isInstance(object)) {
            throw new BusinessException(message);
        }
    }


    public static void noNullElements(Object[] array, CodeDefinition errorCode, Object... arguments) {
        for (Object element : array) {
            if (element == null) {
                throw new BusinessException(errorCode, arguments);
            }
        }
    }

    public static void noNullElements(Object[] array, String message) {
        for (Object element : array) {
            if (element == null) {
                throw new BusinessException(message);
            }
        }
    }


    public static void validate(String name, Object target, CodeDefinition errorCode, Object... arguments) {
        if (!ValidatorUtils.validateToBoolean(name, target)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void validate(String name, Object target, String message) {
        if (!ValidatorUtils.validateToBoolean(name, target)) {
            throw new BusinessException(message);
        }
    }

}
