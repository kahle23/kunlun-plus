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
import kunlun.data.Dict;
import kunlun.data.json.JsonUtil;
import kunlun.data.json.support.jackson.util.JsonSceneManager.SceneSerializer;
import kunlun.util.StrUtil;

import java.util.List;
import java.util.Map;

import static baibao.common.constant.Actions.REPLACE_URLS_IN_JSON_BY_REGEX;
import static kunlun.common.constant.Numbers.ZERO;
import static kunlun.util.Assert.notBlank;
import static kunlun.util.Assert.notEmpty;

/**
 * FilesJsonToObjSerializer.
 * @see kunlun.data.json.support.jackson.model.Scene#FILES_JSON_STR
 * @see baibao.action.util.regex.ReplaceUrlsInJsonByRegexAction
 * @author Zerox
 */
public class UrlsJsonToObjSerializer implements SceneSerializer {
    private static final String NAME = "jackson";
    private final List<String> urlPrefixes;
    private final Integer layer;

    public UrlsJsonToObjSerializer(List<String> urlPrefixes, Integer layer) {
        if (layer == null) { layer = ZERO; }
        notEmpty(urlPrefixes);
        for (String urlPrefix : urlPrefixes) {
            notBlank(urlPrefix);
        }
        this.urlPrefixes = urlPrefixes;
        this.layer = layer;
    }

    public UrlsJsonToObjSerializer(List<String> urlPrefixes) {

        this(urlPrefixes, Nil.INT);
    }

    @Override
    public Object serialize(Object rawData, JavaType fieldType, Map<String, String> configs, ObjectCodec codec) {
        // 仅针对于类型是 字符串 类型的数据，进行处理
        if (!(rawData instanceof String)) { return null; }
        String data = (String) rawData;
        if (StrUtil.isBlank(data)) { return null; }
        // 进行替换处理
        Function<String, String> function = new Function<String, String>() {
            @Override
            public String apply(String str) {

                return processSingleUrl(str, configs);
            }
        };
        data = ActionUtil.execute(REPLACE_URLS_IN_JSON_BY_REGEX, data, urlPrefixes, layer, function);
        //
        if (JsonUtil.isJsonArray(NAME, data)) {
            return JsonUtil.parseObject(NAME, data, List.class);
        } else if (JsonUtil.isJsonObject(NAME, data)) {
            return JsonUtil.parseObject(NAME, data, Map.class);
        } else { return null; }
    }

    protected String processSingleUrl(String originalUrl, Map<String, String> configs) {
        if (StrUtil.isBlank(originalUrl)) { return originalUrl; }
        return OssUtils.createSignedUrl(originalUrl, Nil.g(), Dict.of(configs));
    }

}
