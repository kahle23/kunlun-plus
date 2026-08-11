package baibao.action.ai.support.openai;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import kunlun.action.ActionUtil;
import kunlun.ai.model.ChatRequest;
import kunlun.ai.model.ChatResponse;
import kunlun.ai.model.EmbedRequest;
import kunlun.ai.model.EmbedResponse;
import kunlun.core.function.Consumer;
import kunlun.data.Dict;
import kunlun.data.json.JsonUtil;
import kunlun.data.json.support.FastJsonProcessor;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.net.Proxy;

import static kunlun.common.constant.Symbols.DOT;

@Ignore
public class OpenAIActionTest {
    private static final Logger log = LoggerFactory.getLogger(OpenAIActionTest.class);
    private static final String handlerName = "openai";
    private static final String embedModel = "text-embedding-ada-002";
    private static final String chatModel = "gpt-3.5-turbo-0613";

    static {
        JsonUtil.registerProcessor(JsonUtil.getDefaultProcessorName(), new FastJsonProcessor());
        ActionUtil.registerAction(handlerName, new AbstractOpenAIAction() {
            @Override
            protected Config getConfig(String strategy, Object input) {
                Config config = new Config();
                config.setApiKey("apiKey");
                config.setProxyType(Proxy.Type.HTTP.name());
                config.setProxyHostname("127.0.0.1");
                config.setProxyPort(58591);
                config.setDebug(Boolean.FALSE);
                return config;
            }
        });
    }

    @Test
    public void testChat() {
        ChatRequest req = ChatRequest.Builder.of(chatModel)
//                .setTemperature(1.9)
                .addMessage("system", "You are a helpful assistant.")
                .addMessage("user", "what is AI?")
                .build();
        String command = handlerName + DOT + "chat";
        ChatResponse resp = ActionUtil.execute(command, req);
        log.info("result: {}", JSON.toJSONString(resp, Boolean.TRUE));
    }

    @Test
    public void testChat1() {
        ChatRequest req = ChatRequest.Builder.of(chatModel)
//                .setTemperature(1.9)
                .addMessage("system", "You are a helpful assistant.")
                .addMessage("user", "what is AI?")
                .setStream(true)
                .setStreamConsumer(new Consumer<Object>() {
                    @Override
                    public void accept(Object param) {
                        String line = String.valueOf(param);
                        System.out.println(line);
//                        if (StrUtil.isBlank(line)) { return; }
//                        line = line.trim();
//                        String substring = line.substring("data: ".length());
//                        Dict dict = JSON.parseObject(substring, Dict.class);
//                        Array choices = Array.of((List) dict.get("choices"));
//                        Object delta = Dict.of(BeanUtil.beanToMap(choices.get(0))).get("delta");
//                        String content = Dict.of(BeanUtil.beanToMap(delta)).getString("content");
//                        System.out.print(content);
                    }
                })
                .build();
        String command = handlerName + DOT + "chat";
        ActionUtil.execute(command, req);
    }

    @Test
    public void testEmbeddings() {
        EmbedRequest request = EmbedRequest.Builder.of(embedModel)
                .setEncodingFormat("float")
                .setInput("this is a test")
                .build();
        String command = handlerName + DOT + "embeddings";
        EmbedResponse execute = ActionUtil.execute(command, request);
        log.info("result: {}", JSON.toJSONString(execute, Boolean.TRUE));
    }

    @Test
    public void testSpeechCreate() {
        // TTS models: tts-1 or tts-1-hd
        Dict args = Dict.of("model", "tts-1")
                // alloy, echo, fable, onyx, nova, shimmer
                .set("voice", "alloy")
                // mp3, opus, aac, flac
//                .set("response_format", "mp3")
                // 0.25 - 4.0    Defaults to 1.0
//                .set("speed", 1.0)
                .set("input", "This is test speech create! ")
                ;
        String command = handlerName + DOT + "speechCreate";
        InputStream execute = ActionUtil.execute(command, args);
        File file = FileUtil.writeFromStream(execute, "F:\\test\\testSpeechCreate.mp3");
        log.info("result: {}", file);
    }

    @Test
    public void testCompletion() {
        Dict args = Dict.of("model", "gpt-3.5-turbo-instruct")
                .set("max_tokens", 7)
                .set("temperature", 0)
//                .set("stream", true)
                .set("prompt", "Say this is a test")
                ;
        String command = handlerName + DOT + "completion";
        Dict execute = ActionUtil.execute(command, args);
        log.info("result: {}", JSON.toJSONString(execute, Boolean.TRUE));
    }

    @Test
    public void testImageCreate() {
        Dict args = Dict.of("model", "dall-e-3")
                .set("n", 1)
                // Defaults to standard
//                .set("quality", "hd")
                // url or b64_json
//                .set("response_format", "url")
                // 256x256, 512x512, or 1024x1024 for dall-e-2.
                // 1024x1024, 1792x1024, or 1024x1792 for dall-e-3
                .set("size", "1024x1024")
                // vivid or natural, Defaults to vivid
//                .set("style", "vivid")
                .set("prompt", "A cute baby sea otter. ")
                ;
        String command = handlerName + DOT + "imageCreate";
        Dict execute = ActionUtil.execute(command, args);
        log.info("result: {}", JSON.toJSONString(execute, Boolean.TRUE));
    }

    @Test
    public void testModels() {
        String command = handlerName + DOT + "models";
        Dict execute = ActionUtil.execute(command, (Object) null);
        log.info("result: {}", JSON.toJSONString(execute, Boolean.TRUE));
    }

}
