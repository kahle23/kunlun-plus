/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.serialize.support;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import kunlun.codec.CodecUtils;
import kunlun.core.Serializer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static kunlun.codec.CodecUtils.HEX;

public class KryoTest {
    private static final Logger log = LoggerFactory.getLogger(KryoTest.class);
    private static final Serializer serializer;

    static  {
        KryoFactory factory = new KryoFactory() {
            @Override
            public Kryo create() {
                return new Kryo();
            }
        };
        KryoPool pool = new KryoPool.Builder(factory).softReferences().build();
        serializer = new KryoSerializer(pool);
    }

    @Test
    public void test1() {
        KryoTest kryoTest = new KryoTest();
        log.info("Object: {}", kryoTest);
        byte[] bytes = (byte[]) serializer.serialize(kryoTest);
        log.info("Hex: {}", CodecUtils.encodeToString(HEX, bytes));
        Object obj = serializer.deserialize(bytes);
        log.info("Object: {}", obj);
    }

}
