/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.data.json.support.jackson.util.scene;

import baibao.common.dto.FileDTO;
import baibao.io.oss.support.aliyun.OssUtils;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JavaType;
import kunlun.common.constant.Nil;
import kunlun.data.Dict;
import kunlun.data.json.JsonUtil;
import kunlun.data.json.support.jackson.util.JsonSceneManager.SceneSerializer;
import kunlun.util.StrUtil;
import kunlun.util.TypeUtil;

import java.util.List;
import java.util.Map;

/**
 * FilesJsonToObjSerializer.
 * @see kunlun.data.json.support.jackson.model.Scene#FILES_JSON_STR
 * @see baibao.common.dto.FileDTO
 * @author Zerox
 */
public class FilesJsonToObjSerializer implements SceneSerializer {
    private static final String NAME = "jackson";

    @Override
    public Object serialize(Object rawData, JavaType fieldType, Map<String, String> configs, ObjectCodec codec) {
        // 仅针对于类型是 字符串 类型的数据，进行处理
        if (!(rawData instanceof String)) { return null; }
        String data = (String) rawData;
        // 仅针对于 JSON 中的对象类型是 FileDTO 的，这个目前只能人工控制
        // 如果字符串不是 Json 数组 和 Json 对象的话，直接走默认处理逻辑
        if (JsonUtil.isJsonArray(NAME, data)) {
            List<FileDTO> list = JsonUtil.parseObject(NAME, data, TypeUtil.parameterizedOf(List.class, FileDTO.class));
            for (FileDTO file : list) { addSign(file); }
            return list;
        } else if (JsonUtil.isJsonObject(NAME, data)) {
            FileDTO file = JsonUtil.parseObject(NAME, data, FileDTO.class);
            addSign(file);
            return file;
        } else { return null; }
    }

    private void addSign(FileDTO file) {
        if (file == null) { return; }
        if (StrUtil.isBlank(file.getAddr())) { return; }
        file.setAddr(OssUtils.createSignedUrl(file.getAddr(), Nil.g(), Dict.of("originalFilename", file.getName())));
    }

}
