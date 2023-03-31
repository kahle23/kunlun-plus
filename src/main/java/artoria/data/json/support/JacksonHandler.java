package artoria.data.json.support;

import artoria.data.json.JsonFormat;
import artoria.exception.ExceptionUtils;
import artoria.util.ArrayUtils;
import artoria.util.Assert;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.lang.reflect.Type;

import static artoria.data.json.JsonFormat.PRETTY_FORMAT;

/**
 * The json handler simple implement by jackson.
 * @author Kahle
 */
public class JacksonHandler extends AbstractJsonHandler {
    private final ObjectMapper objectMapper;

    public JacksonHandler() {

        this(new ObjectMapper());
    }

    public JacksonHandler(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "Parameter \"objectMapper\" must not null. ");
        this.objectMapper = objectMapper;
    }

    @Override
    public String toJsonString(Object object, Object... arguments) {
        try {
            if (ArrayUtils.isEmpty(arguments)) {
                return objectMapper.writeValueAsString(object);
            }
            ObjectWriter objectWriter = null;
            for (Object arg : arguments) {
                if (arg == null) { continue; }
                if (arg instanceof JsonFormat
                        && PRETTY_FORMAT.equals(arg)) {
                    objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
                }
            }
            if (objectWriter == null) {
                objectWriter = objectMapper.writer();
            }
            return objectWriter.writeValueAsString(object);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Override
    public <T> T parseObject(String jsonString, Type type, Object... arguments) {
        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            JavaType javaType = typeFactory.constructType(type);
            return objectMapper.readValue(jsonString, javaType);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

}
