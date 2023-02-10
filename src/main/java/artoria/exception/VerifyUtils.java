package artoria.exception;

import artoria.common.SimpleErrorCode;
import artoria.data.ErrorCode;
import artoria.data.validation.ValidatorUtils;
import artoria.util.ArrayUtils;
import artoria.util.CollectionUtils;
import artoria.util.MapUtils;
import artoria.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Verify tools.
 * @author Kahle
 */
public class VerifyUtils {
    private static final String DEFAULT_CODE = "500";

    public static void isFalse(boolean expression, String code, String description, Object... arguments) {
        if (expression) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void isFalse(boolean expression, ErrorCode errorCode, Object... arguments) {
        if (expression) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void isTrue(boolean expression, String code, String description, Object... arguments) {
        if (!expression) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void isTrue(boolean expression, ErrorCode errorCode, Object... arguments) {
        if (!expression) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void isNull(Object object, String code, String description, Object... arguments) {
        if (object != null) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void isNull(Object object, ErrorCode errorCode, Object... arguments) {
        if (object != null) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void notNull(Object object, String code, String description, Object... arguments) {
        if (object == null) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void notNull(Object object, ErrorCode errorCode, Object... arguments) {
        if (object == null) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void isEmpty(byte[] array, String code, String description, Object... arguments) {
        if (ArrayUtils.isNotEmpty(array)) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void isEmpty(byte[] array, ErrorCode errorCode, Object... arguments) {
        if (ArrayUtils.isNotEmpty(array)) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void isEmpty(byte[] array, String message) {
        if (ArrayUtils.isNotEmpty(array)) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void notEmpty(byte[] array, String code, String description, Object... arguments) {
        if (ArrayUtils.isEmpty(array)) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void notEmpty(byte[] array, ErrorCode errorCode, Object... arguments) {
        if (ArrayUtils.isEmpty(array)) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void notEmpty(byte[] array, String message) {
        if (ArrayUtils.isEmpty(array)) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void isEmpty(Object[] array, String code, String description, Object... arguments) {
        if (ArrayUtils.isNotEmpty(array)) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void isEmpty(Object[] array, ErrorCode errorCode, Object... arguments) {
        if (ArrayUtils.isNotEmpty(array)) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void isEmpty(Object[] array, String message) {
        if (ArrayUtils.isNotEmpty(array)) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void notEmpty(Object[] array, String code, String description, Object... arguments) {
        if (ArrayUtils.isEmpty(array)) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void notEmpty(Object[] array, ErrorCode errorCode, Object... arguments) {
        if (ArrayUtils.isEmpty(array)) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void notEmpty(Object[] array, String message) {
        if (ArrayUtils.isEmpty(array)) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void isEmpty(Collection<?> collection, String code, String description, Object... arguments) {
        if (CollectionUtils.isNotEmpty(collection)) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void isEmpty(Collection<?> collection, ErrorCode errorCode, Object... arguments) {
        if (CollectionUtils.isNotEmpty(collection)) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void isEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isNotEmpty(collection)) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }



    public static void notEmpty(Collection<?> collection, String code, String description, Object... arguments) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void notEmpty(Collection<?> collection, ErrorCode errorCode, Object... arguments) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void notEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void isEmpty(Map<?, ?> map, String code, String description, Object... arguments) {
        if (MapUtils.isNotEmpty(map)) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void isEmpty(Map<?, ?> map, ErrorCode errorCode, Object... arguments) {
        if (MapUtils.isNotEmpty(map)) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void isEmpty(Map<?, ?> map, String message) {
        if (MapUtils.isNotEmpty(map)) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void notEmpty(Map<?, ?> map, String code, String description, Object... arguments) {
        if (MapUtils.isEmpty(map)) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void notEmpty(Map<?, ?> map, ErrorCode errorCode, Object... arguments) {
        if (MapUtils.isEmpty(map)) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void notEmpty(Map<?, ?> map, String message) {
        if (MapUtils.isEmpty(map)) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void isEmpty(String text, String code, String description, Object... arguments) {
        if (StringUtils.isNotEmpty(text)) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void isEmpty(String text, ErrorCode errorCode, Object... arguments) {
        if (StringUtils.isNotEmpty(text)) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void isEmpty(String text, String message) {
        if (StringUtils.isNotEmpty(text)) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void notEmpty(String text, String code, String description, Object... arguments) {
        if (StringUtils.isEmpty(text)) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void notEmpty(String text, ErrorCode errorCode, Object... arguments) {
        if (StringUtils.isEmpty(text)) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void notEmpty(String text, String message) {
        if (StringUtils.isEmpty(text)) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void isBlank(String text, String code, String description, Object... arguments) {
        if (StringUtils.isNotBlank(text)) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void isBlank(String text, ErrorCode errorCode, Object... arguments) {
        if (StringUtils.isNotBlank(text)) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void isBlank(String text, String message) {
        if (StringUtils.isNotBlank(text)) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void notBlank(String text, String code, String description, Object... arguments) {
        if (StringUtils.isBlank(text)) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void notBlank(String text, ErrorCode errorCode, Object... arguments) {
        if (StringUtils.isBlank(text)) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void notBlank(String text, String message) {
        if (StringUtils.isBlank(text)) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void isContain(String textToSearch, String substring, String code, String description, Object... arguments) {
        if (!textToSearch.contains(substring)) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void isContain(String textToSearch, String substring, ErrorCode errorCode, Object... arguments) {
        if (!textToSearch.contains(substring)) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void isContain(String textToSearch, String substring, String message) {
        if (!textToSearch.contains(substring)) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void notContain(String textToSearch, String substring, String code, String description, Object... arguments) {
        if (textToSearch.contains(substring)) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void notContain(String textToSearch, String substring, ErrorCode errorCode, Object... arguments) {
        if (textToSearch.contains(substring)) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void notContain(String textToSearch, String substring, String message) {
        if (textToSearch.contains(substring)) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void isAssignable(Class<?> superType, Class<?> subType, String code, String description, Object... arguments) {
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void isAssignable(Class<?> superType, Class<?> subType, ErrorCode errorCode, Object... arguments) {
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void isAssignable(Class<?> superType, Class<?> subType, String message) {
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void isInstanceOf(Class<?> type, Object object, String code, String description, Object... arguments) {
        if (!type.isInstance(object)) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void isInstanceOf(Class<?> type, Object object, ErrorCode errorCode, Object... arguments) {
        if (!type.isInstance(object)) {
            throw new BusinessException(errorCode, arguments);
        }
    }


    public static void isInstanceOf(Class<?> type, Object object, String message) {
        if (!type.isInstance(object)) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }


    public static void noNullElements(Object[] array, String code, String description, Object... arguments) {
        for (Object element : array) {
            if (element == null) {
                throw new BusinessException(new SimpleErrorCode(code, description), arguments);
            }
        }
    }


    public static void noNullElements(Object[] array, ErrorCode errorCode, Object... arguments) {
        for (Object element : array) {
            if (element == null) {
                throw new BusinessException(errorCode, arguments);
            }
        }
    }


    public static void noNullElements(Object[] array, String message) {
        for (Object element : array) {
            if (element == null) {
                throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
            }
        }
    }


    public static void validate(String name, Object target, String code, String description, Object... arguments) {
        if (!ValidatorUtils.validateToBoolean(name, target)) {
            throw new BusinessException(new SimpleErrorCode(code, description), arguments);
        }
    }


    public static void validate(String name, Object target, ErrorCode errorCode, Object... arguments) {
        if (!ValidatorUtils.validateToBoolean(name, target)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void validate(String name, Object target, String message) {
        if (!ValidatorUtils.validateToBoolean(name, target)) {
            throw new BusinessException(new SimpleErrorCode(DEFAULT_CODE, message));
        }
    }

}
