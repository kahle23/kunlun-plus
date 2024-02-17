/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.serialize.support;

import kunlun.codec.CodecUtils;
import kunlun.core.Serializer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import static kunlun.codec.CodecUtils.HEX;

public class FstTest implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(FstTest.class);
    private static final Serializer serializer = new FstSerializer();

    @Test
    public void test1() {
        FstTest fstTest = new FstTest();
        log.info("Object: {}", fstTest);
        byte[] bytes = (byte[]) serializer.serialize(fstTest);
        log.info("Hex: {}", CodecUtils.encodeToString(HEX, bytes));
        Object obj = serializer.deserialize(bytes);
        log.info("Object: {}", obj);
    }

}
