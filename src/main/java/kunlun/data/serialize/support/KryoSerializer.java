/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.serialize.support;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoPool;
import kunlun.core.Serializer;
import kunlun.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Kryo serializer.
 * @author Kahle
 */
public class KryoSerializer implements Serializer {
    private final KryoPool kryoPool;

    public KryoSerializer(KryoPool kryoPool) {
        Assert.notNull(kryoPool, "Parameter \"kryoPool\" must not null. ");
        this.kryoPool = kryoPool;
    }

    @Override
    public Object serialize(final Object object) {
        Assert.notNull(object, "Parameter \"object\" must not null. ");
        return kryoPool.run(new KryoCallback<Object>() {
            @Override
            public Object execute(Kryo kryo) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Output output = new Output(outputStream);
                kryo.writeClassAndObject(output, object);
                output.flush();
                return outputStream.toByteArray();
            }
        });
    }

    @Override
    public Object deserialize(final Object data) {
        Assert.notNull(data, "Parameter \"data\" must not null. ");
        return kryoPool.run(new KryoCallback<Object>() {
            @Override
            public Object execute(Kryo kryo) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream((byte[]) data);
            Input input = new Input(inputStream);
            return kryo.readClassAndObject(input);
            }
        });
    }

}
