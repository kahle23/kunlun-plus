/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.data.json.support.jackson.util.scene;

import baibao.io.oss.support.aliyun.OssUtils;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JavaType;
import kunlun.action.ActionUtil;
import kunlun.common.constant.Nil;
import kunlun.core.function.Function;
import kunlun.data.json.JsonUtil;
import kunlun.data.json.support.jackson.util.JsonSceneManager.SceneDeserializer;
import kunlun.util.StrUtil;

import java.util.List;
import java.util.Map;

import static baibao.common.constant.Actions.REPLACE_URLS_IN_JSON_BY_REGEX;
import static kunlun.common.constant.Numbers.ZERO;
import static kunlun.util.Assert.notBlank;
import static kunlun.util.Assert.notEmpty;

/**
 * ObjToJsonDeserializer.
 * @see kunlun.data.json.support.jackson.model.Scene#FILES_JSON_STR
 * @see baibao.action.util.regex.ReplaceUrlsInJsonByRegexAction
 * @author Zerox
 */
public class ObjToUrlsJsonDeserializer implements SceneDeserializer {
    private static final String NAME = "jackson";
    private final List<String> urlPrefixes;
    private final Integer layer;

    public ObjToUrlsJsonDeserializer(List<String> urlPrefixes, Integer layer) {
        if (layer == null) { layer = ZERO; }
        notEmpty(urlPrefixes);
        for (String urlPrefix : urlPrefixes) {
            notBlank(urlPrefix);
        }
        this.urlPrefixes = urlPrefixes;
        this.layer = layer;
    }

    public ObjToUrlsJsonDeserializer(List<String> urlPrefixes) {

        this(urlPrefixes, Nil.INT);
    }

    @Override
    public Object deserialize(Object rawData, JavaType fieldType, Map<String, String> configs, ObjectCodec codec) {
        if (rawData == null) { return null; }
        // 待接收的字段类型是 字符串，传入的数据是 非字符串
        // 如果不是 集合 和 文件对象 的话，直接走默认处理逻辑
        if (fieldType.isTypeOrSubTypeOf(String.class) && !(rawData instanceof String)) {
            String jsonString = JsonUtil.toJsonString(NAME, rawData);
            // 进行替换处理
            Function<String, String> function = new Function<String, String>() {
                @Override
                public String apply(String str) {

                    return processSingleUrl(str);
                }
            };
            return ActionUtil.execute(REPLACE_URLS_IN_JSON_BY_REGEX, jsonString, urlPrefixes, layer, function);
        }
        return null;
    }

    protected String processSingleUrl(String originalUrl) {
        if (StrUtil.isBlank(originalUrl)) { return originalUrl; }
        return OssUtils.removeUrlSign(originalUrl);
    }

}
