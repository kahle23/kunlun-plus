/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.serialize.support;

import kunlun.codec.CodecUtils;
import kunlun.core.Serializer;
import kunlun.data.serialize.support.SimpleSerializer;
import kunlun.util.Assert;
import kunlun.util.StringUtils;

import static kunlun.codec.CodecUtils.BASE64;

@Deprecated
public class Base64TextSerializer implements Serializer {
    private final Serializer serializer;

    public Base64TextSerializer(SimpleSerializer serializer) {
        Assert.notNull(serializer, "Parameter \"serializer\" must not null. ");
        this.serializer = serializer;
    }

    public Base64TextSerializer() {

        this(new SimpleSerializer());
    }

    @Override
    public Object serialize(Object object) {
        if (object == null) { return null; }
        byte[] serialize = (byte[]) serializer.serialize(object);
        return CodecUtils.encodeToString(BASE64, serialize);
    }

    @Override
    public Object deserialize(Object data) {
        if (data == null) { return null; }
        String text = (String) data;
        if (StringUtils.isBlank(text)) { return null; }
        byte[] bytes = CodecUtils.decodeFromString(BASE64, (String) data);
        return serializer.deserialize(bytes);
    }

}
