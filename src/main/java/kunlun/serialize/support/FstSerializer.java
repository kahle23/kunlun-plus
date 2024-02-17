/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.serialize.support;

import kunlun.core.Serializer;
import kunlun.exception.ExceptionUtils;
import kunlun.util.Assert;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Fst serializer.
 * @author Kahle
 */
@Deprecated
public class FstSerializer implements Serializer {

    @Override
    public Object serialize(Object object) {
        Assert.notNull(object, "Parameter \"object\" must not null. ");
        Assert.isInstanceOf(Serializable.class, object, FstSerializer.class.getSimpleName()
                + " requires a Serializable payload but received an object of type ["
                + object.getClass().getName() + "]. ");
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            FSTObjectOutput output = new FSTObjectOutput(outputStream);
            output.writeObject(object);
            output.flush();
            return outputStream.toByteArray();
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Override
    public Object deserialize(Object data) {
        Assert.notNull(data, "Parameter \"data\" must not null. ");
        Assert.isInstanceOf(byte[].class, data
                , "Parameter \"data\" must is instance of byte[]. ");
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream((byte[]) data);
            FSTObjectInput input = new FSTObjectInput(inputStream);
            return input.readObject();
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

}
