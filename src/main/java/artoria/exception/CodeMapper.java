package artoria.exception;

import artoria.util.Assert;

import javax.validation.Validation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mapping of error code and message.
 * @author Kahle
 */
public class CodeMapper {
    private static final Map<String, String> CODE_MAP = new ConcurrentHashMap<String, String>();

    public static void register(String code, String message) {
        Assert.notBlank(code, "Parameter \"code\" must not blank. ");
        Assert.notNull(message, "Parameter \"message\" must not null. ");
        CODE_MAP.put(code, message);
    }

    public static String getMessage(String code) {
        return CODE_MAP.get(code);
    }

}
