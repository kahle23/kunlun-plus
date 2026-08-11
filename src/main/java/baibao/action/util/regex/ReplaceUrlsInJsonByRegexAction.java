package baibao.action.util.regex;

import kunlun.core.Action;
import kunlun.core.function.Function;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static kunlun.common.constant.Numbers.*;
import static kunlun.common.constant.Symbols.DOUBLE_QUOTE;
import static kunlun.util.Assert.notBlank;
import static kunlun.util.Assert.notNull;
import static kunlun.util.CastUtil.cast;

/**
 * 基于正则替换Json中的指定前缀的URL.
 * （如果是Json套Json的那种，只处理嵌套的那一层 JSON 是可以的，如果同时处理多层，是不行的）
 * @author Zerox
 */
public class ReplaceUrlsInJsonByRegexAction implements Action {
//    private static final String REGEX_PREFIX = "(\"[^\"]*\"\\s*:\\s*)\"(";
    private static final String REGEX_PREFIX = "(\\s*)\"(";
    private static final String REGEX_SUFFIX = "[^\"]*)\"";

    @Override
    public Object execute(String strategy, Object input, Object[] arguments) {
        // 参数获取
        String jsonString = notBlank((String) input);
        Collection<String> urlPrefixes = cast(notNull(arguments[ZERO]));
        Integer layer = (Integer) arguments[ONE];
        Function<String, String> urlProcessor = cast(notNull(arguments[TWO]));
        // 如果不包含“"”的话，当成一个纯 URL 来处理
        if (!jsonString.contains(DOUBLE_QUOTE)) {
            jsonString = jsonString.trim(); boolean conform = false;
            for (String urlPrefix : urlPrefixes) {
                if (jsonString.startsWith(urlPrefix)) {
                    conform = true; break;
                }
            }
            if (conform) {
                jsonString = urlProcessor.apply(jsonString);
                return jsonString;
            }
        }
        // 进行替换处理
        for (String urlPrefix : urlPrefixes) {
            jsonString = doReplace(jsonString, urlPrefix, layer, urlProcessor);
        }
        // 结果
        return jsonString;
    }

    protected String doReplace(String jsonString, String urlPrefix, Integer layer, Function<String, String> urlProcessor) {
        // 参数判断，默认值处理
        notBlank(jsonString); notBlank(urlPrefix); notNull(urlProcessor);
        if (layer == null) { layer = ZERO; }
        // 根据层数构建 正则的引号 和 层数对应的引号
        // 并且替换默认 0 层的 正则前缀 和 正则后缀
        String regexPrefix = REGEX_PREFIX, regexSuffix = REGEX_SUFFIX;
        StringBuilder quoteRegex = new StringBuilder(DOUBLE_QUOTE);
        StringBuilder quoteLayer = new StringBuilder(DOUBLE_QUOTE);
        if (layer > 0) {
            for (int i = 0; i < layer; i++) {
                quoteRegex.insert(ZERO, "\\\\\\\\");
                quoteLayer.insert(ZERO, "\\");
            }
            String quoteRgxStr = quoteRegex.toString();
            regexPrefix = regexPrefix.replaceAll(DOUBLE_QUOTE, quoteRgxStr);
            regexSuffix = regexSuffix.replaceAll(DOUBLE_QUOTE, quoteRgxStr);
        }
        // 构建匹配引号的正则表达式，使用 Pattern.quote 来确保前缀中的特殊字符（如点 .）被当作普通字符处理
        String regex = regexPrefix + Pattern.quote(urlPrefix) + regexSuffix;
        Matcher matcher = Pattern.compile(regex).matcher(jsonString);
        // 匹配并替换
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            // 获取匹配到的 URL
            // group(1) 是 \"key\": 部分， group(2) 是纯净的URL，不包含引号
            String originalUrl = matcher.group(TWO);
            // 进行 URL 处理
            String processedUrl = urlProcessor.apply(originalUrl);
            // 将处理后的URL用双引号括起来再替换回去，确保JSON格式
            // 替换时，将 "key": "oldUrl" 替换为 "key": "processedUrl"， 即：保留 group(1)（键和结构），只替换URL部分
            String quoteLyrStr = quoteLayer.toString();
            String replacement = matcher.group(ONE) + quoteLyrStr + processedUrl + quoteLyrStr;
            // 注意：appendReplacement 方法会对替换字符串中的反斜杠和美元符号进行特殊处理，Matcher.quoteReplacement 可以避免这个问题
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        // 追加剩余的尾部字符串
        matcher.appendTail(result);
        // 结果
        return result.toString();
    }

}
