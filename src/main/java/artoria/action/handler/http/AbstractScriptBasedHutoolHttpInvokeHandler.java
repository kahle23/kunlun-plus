package artoria.action.handler.http;

import artoria.data.Dict;
import artoria.data.json.JsonUtils;
import artoria.data.tuple.KeyValue;
import artoria.data.tuple.KeyValueImpl;
import artoria.time.DateUtils;
import artoria.util.CollectionUtils;
import artoria.util.StringUtils;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static artoria.common.constant.TimePatterns.NORM_DATETIME_MS;

public abstract class AbstractScriptBasedHutoolHttpInvokeHandler extends AbstractScriptBasedHttpInvokeHandler {

    /*
    * about  ScriptObjectMirror
    *
    * use like
    * java.util.Arrays.asList(["inner is js array"])
    * */

    @Override
    protected void doInvoke(InvokeContext context) {
        HttpInvokeConfig config = (HttpInvokeConfig) context.getConfig();
        //
        ConvertedInput convertedInput = (ConvertedInput) context.getConvertedInput();
        Integer methodInt = convertedInput.getMethod();
        Method method;
        switch (methodInt) {
            case 1: method = Method.GET; break;
            case 2: method = Method.POST; break;
            default: throw new UnsupportedOperationException("method is unsupported! ");
        }

        HttpRequest request = HttpUtil.createRequest(method, convertedInput.getUrl());

        String charset = convertedInput.getCharset();
        if (StrUtil.isNotBlank(charset)) {
            request.charset(charset);
        }

        Collection<KeyValue<String, String>> headers1 = convertedInput.getHeaders();
        if (CollUtil.isNotEmpty(headers1)) {
            for (KeyValue<String, String> keyValue : headers1) {
                request.header(keyValue.getKey(), keyValue.getValue());
            }
        }

        Collection<KeyValue<String, Object>> parameters = convertedInput.getParameters();
        if (CollectionUtils.isNotEmpty(parameters)) {
            for (KeyValue<String, Object> keyValue : parameters) {
                request.form(keyValue.getKey(), keyValue.getValue());
            }
        }

        Object body = convertedInput.getBody();
        if (body != null) {
            if (body instanceof String) {
                request.body((String) body);
            }
            else if (body instanceof byte[]) {
                request.body((byte[]) body);
            }
            else {
                throw new UnsupportedOperationException("body is unsupported! ");
            }
        }
        HttpResponse execute = request.execute();

        RawOutput rawOutput = new RawOutput();
        rawOutput.setTime(DateUtils.format(NORM_DATETIME_MS));
        rawOutput.setCharset(execute.charset());
        rawOutput.setRawString(execute.body());
        rawOutput.setStatusCode(execute.getStatus());
        Integer outputType = config.getOutputType();
        rawOutput.setOutputType(outputType);
        context.setRawOutput(rawOutput);

        Collection<KeyValue<String, String>> headersColl = new ArrayList<KeyValue<String, String>>();
        Map<String, List<String>> headers = execute.headers();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            List<String> values = entry.getValue();
            String key = entry.getKey();
            if (CollUtil.isEmpty(values)) {
                headersColl.add(new KeyValueImpl<String, String>(key, null));
                continue;
            }
            for (String value : values) {
                headersColl.add(new KeyValueImpl<String, String>(key, value));
            }
        }
        rawOutput.setHeaders(headersColl);

        // RawObject must init in doInvoke.
        if (outputType == 3) {
            String rawString = rawOutput.getRawString();
            boolean notBlank = StringUtils.isNotBlank(rawString);
            if (notBlank && JsonUtils.isJsonObject(rawString)) {
                rawOutput.setRawObject(JsonUtils.parseObject(rawString, Dict.class));
            }
            else if (notBlank && JsonUtils.isJsonArray(rawString)) {
                rawOutput.setRawObject(JsonUtils.parseObject(rawString, List.class));
            }
        }
    }

}
