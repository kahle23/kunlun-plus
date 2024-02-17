/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import kunlun.data.json.JsonFormat;
import kunlun.exception.ExceptionUtils;
import kunlun.util.ArrayUtils;
import kunlun.util.Assert;

import java.lang.reflect.Type;

import static kunlun.data.json.JsonFormat.PRETTY_FORMAT;

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
