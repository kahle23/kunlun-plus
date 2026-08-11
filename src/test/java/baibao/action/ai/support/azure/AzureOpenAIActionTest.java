package baibao.action.ai.support.azure;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import kunlun.action.ActionUtil;
import kunlun.ai.model.*;
import kunlun.core.function.Consumer;
import kunlun.data.Dict;
import kunlun.data.json.JsonUtil;
import kunlun.data.json.support.FastJsonProcessor;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static kunlun.ai.model.Message.SYSTEM;
import static kunlun.ai.model.Message.USER;
import static kunlun.common.constant.Symbols.DOT;

@Ignore
public class AzureOpenAIActionTest {
    private static final Logger log = LoggerFactory.getLogger(AzureOpenAIActionTest.class);
    private static final String handlerName = "azure";
    private static final String embedModel1 = "text-embedding-3-large";
    private static final String embedModel = "text-embedding-ada-002";
    private static final String chatModel = "gpt-4";

    static {
        JsonUtil.registerProcessor(JsonUtil.getDefaultProcessorName(), new FastJsonProcessor());
        ActionUtil.registerAction(handlerName, new AbstractAzureOpenAIAction() {
            @Override
            protected Config getConfig(String strategy, Object input) {
                Config config = new Config();
                config.setEndpoint("aaaa.openai.azure.com");
                config.setApiVersion("2025-01-01");
                config.setApiKey("aaaa");
                config.setDebug(true);
                return config;
            }
        });
    }

    @Test
    public void testChat() {
        ChatRequest request = ChatRequest.Builder.of(chatModel)
//                .setTemperature(1.9)
                .addMessage(SYSTEM, "You are a helpful assistant.")
                .addMessage(USER, "what is AI?")
                .build();
        String command = handlerName + DOT + "chat";
        ChatResponse response = ActionUtil.execute(command, request);
        log.info("result: {}", JSON.toJSONString(response, Boolean.TRUE));
    }

    @Test
    public void testChat1() {
        ChatRequest request = ChatRequest.Builder.of(chatModel)
//                .setTemperature(1.9)
                .setStream(true)
                .addMessage(SYSTEM, "You are a helpful assistant.")
                .addMessage(USER, "what is AI?")
                .setStreamConsumer(new Consumer<Object>() {
                    @Override
                    public void accept(Object param) {
                        String line = String.valueOf(param);
                        System.out.println(line);
                    }})
                .build();
        String command = handlerName + DOT + "chat";
        ActionUtil.execute(command, request);
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
    public void testChatTool1() {
        String toolParams = "{\"type\":\"object\",\"properties\":{\"location\":{\"type\":\"string\",\"description\":\"The city and state, e.g. San Francisco, CA\",\"properties\":{\"required\":true}},\"unit\":{\"type\":\"string\",\"enum\":[\"celsius\",\"fahrenheit\"],\"description\":\"The unit of temperature\",\"properties\":{\"required\":true}}}}";
        ChatRequest request = ChatRequest.Builder.of(chatModel)
//                .setTemperature(1.9)
                .addMessage(SYSTEM, "You are a helpful assistant.")
                .addMessage(USER, "What's the weather like in Shanghai today? ")
                .addTool(Tool.Builder.of("function")
                        .setFunction("get_current_weather",
                                "Get the current weather in a given location.",
                                JSONUtil.toBean(toolParams, Dict.class))
                        .build())
                .build();
        String command = handlerName + DOT + "chat";
        ChatResponse response = ActionUtil.execute(command, request);
        log.info("result: {}", JSON.toJSONString(response, Boolean.TRUE));
    }

}
