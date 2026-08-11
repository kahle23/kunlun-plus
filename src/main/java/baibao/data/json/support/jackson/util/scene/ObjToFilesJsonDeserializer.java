/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.data.json.support.jackson.util.scene;

import baibao.io.oss.support.aliyun.OssUtils;
import cn.hutool.core.convert.Convert;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JavaType;
import kunlun.data.json.JsonUtil;
import kunlun.data.json.support.jackson.util.JsonSceneManager.SceneDeserializer;

import java.util.Collection;
import java.util.Map;

import static kunlun.util.CastUtil.cast;

/**
 * ObjToJsonDeserializer.
 * @see kunlun.data.json.support.jackson.model.Scene#FILES_JSON_STR
 * @see baibao.common.dto.FileDTO
 * @author Zerox
 */
public class ObjToFilesJsonDeserializer implements SceneDeserializer {
    private static final String NAME = "jackson";

    @Override
    public Object deserialize(Object rawData, JavaType fieldType, Map<String, String> configs, ObjectCodec codec) {
        if (rawData == null) { return null; }
        // 待接收的字段类型是 字符串，传入的数据是 非字符串
        // 如果不是 集合 和 文件对象 的话，直接走默认处理逻辑
        if (fieldType.isTypeOrSubTypeOf(String.class) && !(rawData instanceof String)) {
            if (rawData instanceof Collection) {
                // 数组
                Collection<Object> coll = cast(rawData);
                for (Object data : coll) { removeSign(cast(data)); }
            } else if (rawData instanceof Map) {
                // 对象
                removeSign(cast(rawData));
            } else { return null; }
            // 转换为字符串
            return JsonUtil.toJsonString(NAME, rawData);
        }
        return null;
    }

    private void removeSign(Map<String, Object> file) {
        String addr = Convert.toStr(file.get("addr"));
        file.put("addr", OssUtils.removeUrlSign(addr));
    }

}
