/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import kunlun.data.json.JsonFormat;
import kunlun.util.ArrayUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static kunlun.common.constant.Numbers.ZERO;
import static kunlun.data.json.JsonFormat.PRETTY_FORMAT;

/**
 * The json handler simple implement by fastjson.
 * @author Kahle
 */
public class FastJsonHandler extends AbstractJsonHandler {

    protected Feature[] deserializerFeatures(Object[] features) {

        return new Feature[ZERO];
    }

    protected SerializerFeature[] serializerFeatures(Object[] arguments) {
        List<SerializerFeature> list = new ArrayList<SerializerFeature>();
        if (ArrayUtils.isEmpty(arguments)) {
            return list.toArray(new SerializerFeature[ZERO]);
        }
        for (Object arg : arguments) {
            if (arg == null) { continue; }
            if (arg instanceof JsonFormat
                    && PRETTY_FORMAT.equals(arg)) {
                list.add(SerializerFeature.PrettyFormat);
            }
        }
        return list.toArray(new SerializerFeature[ZERO]);
    }

    @Override
    public String toJsonString(Object object, Object... arguments) {

        return JSON.toJSONString(object, serializerFeatures(arguments));
    }

    @Override
    public <T> T parseObject(String jsonString, Type type, Object... arguments) {

        return JSON.parseObject(jsonString, type, deserializerFeatures(arguments));
    }

}
