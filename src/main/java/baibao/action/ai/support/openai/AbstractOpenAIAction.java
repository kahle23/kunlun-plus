/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.action.ai.support.openai;

import baibao.action.ai.support.AbstractHttpApiAIAction;
import kunlun.ai.model.ChatRequest;
import kunlun.ai.model.ChatResponse;
import kunlun.ai.model.EmbedRequest;
import kunlun.ai.model.EmbedResponse;
import kunlun.data.Dict;
import kunlun.util.Assert;
import kunlun.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static kunlun.common.constant.Numbers.*;
import static kunlun.net.http.HttpMethod.GET;
import static kunlun.net.http.HttpMethod.POST;

/**
 * The OpenAI AI action.
 * @see <a href="https://platform.openai.com/docs/api-reference">API REFERENCE</a>
 * @author Kahle
 */
public abstract class AbstractOpenAIAction extends AbstractHttpApiAIAction {
    private static final Logger log = LoggerFactory.getLogger(AbstractOpenAIAction.class);

    /**
     * Get the AI handler configuration according to the arguments.
     * @param strategy The strategy or null for AI action execution
     * @param input The input parameters for inference calculations
     * @return The AI action configuration
     */
    protected abstract Config getConfig(String strategy, Object input);

    @Override
    public Object execute(String strategy, Object input, Object[] arguments) {
        if (StrUtil.isBlank(strategy) || AIMethods.CHAT.equals(strategy)) {
            return chat((ChatRequest) input);
        }
        else if (AIMethods.EMBEDDINGS.equals(strategy)) {
            return embeddings((EmbedRequest) input);
        }
        else if ("speechCreate".equals(strategy)) {
            return speechCreate(input, strategy);
        }
        else if ("transcriptionCreate".equals(strategy)) {
            return transcriptionCreate(input, strategy);
        }
        else if ("translationCreate".equals(strategy)) {
            return translationCreate(input, strategy);
        }
        else if ("completion".equals(strategy)) {
            return completion(input, strategy);
        }
        else if ("imageCreate".equals(strategy)) {
            return imageCreate(input, strategy);
        }
        else if ("imageEdit".equals(strategy)) {
            return imageEdit(input, strategy);
        }
        else if ("imageVariation".equals(strategy)) {
            return imageVariation(input, strategy);
        }
        else if ("modelList".equals(strategy)) {
            return modelList(input, strategy);
        }
        else {
            throw new UnsupportedOperationException(
                "The OpenAI AI Handler. \n" +
                "(The api documents \"https://platform.openai.com/docs/api-reference\")\n" +
                "Supported method:\n" +
                " - chat\n" +
                " - embedding\n" +
                " - speechCreate\n" +
                " - transcriptionCreate\n" +
                " - translationCreate\n" +
                " - completion\n" +
                " - imageCreate\n" +
                " - imageEdit\n" +
                " - imageVariation\n" +
                " - modelList\n"
            );
        }
    }

    /**
     * Given a list of messages comprising a conversation, the model will return a response.
     * @param request The required arguments
     * @return The result of operation
     * @see <a href="https://platform.openai.com/docs/api-reference/chat/create">
     *     Create chat completion</a>
     */
    protected ChatResponse chat(ChatRequest request) {
        // Handle input.
        Dict inputDict = Tool.ME.fromChatRequest(request);
        // Get config.
        Config config = getConfig(AIMethods.CHAT, inputDict);
        // Invoke http.
        Dict respDict = doHttp(HttpData.of(Tool.ME).setConfig(config)
                .setHttpType(FOUR).setMethod(POST)
                .setUrl("https://api.openai.com/v1/chat/completions")
                .setHeaders(Dict.of(AUTHORIZATION_KEY, BEARER_KEY + config.getApiKey()))
                .setData(inputDict));
        // Handle output.
        return Tool.ME.toChatResponse(respDict);
    }

    /**
     * Get a vector representation of a given input that can be easily consumed by
     *      machine learning models and algorithms.
     * @param request The required arguments
     * @return The result of operation
     * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/create">
     *     Create embeddings</a>
     */
    protected EmbedResponse embeddings(EmbedRequest request) {
        // Handle input.
        Dict inputDict = Tool.ME.fromEmbedRequest(request);
        // Get config.
        Config config = getConfig(AIMethods.EMBEDDINGS, inputDict);
        // Invoke http.
        Dict respDict = doHttp(HttpData.of(Tool.ME).setConfig(config)
                .setHttpType(FOUR).setMethod(POST)
                .setUrl("https://api.openai.com/v1/embeddings")
                .setHeaders(Dict.of(AUTHORIZATION_KEY, BEARER_KEY + config.getApiKey()))
                .setData(inputDict));
        // Handle output.
        return Tool.ME.toEmbedResponse(respDict);
    }

    /**
     * Generates audio from the input text.
     * @param input The required arguments
     * @return The result of operation
     * @see <a href="https://platform.openai.com/docs/api-reference/audio/createSpeech">
     *     Create speech</a>
     */
    protected Dict speechCreate(Object input, String operation) {
        Assert.notNull(input, "Parameter \"input\" must not null. ");
        Config config = getConfig(operation, input = Tool.ME.toDict(input));
        return doHttp(HttpData.of(Tool.ME).setConfig(config)
                .setHttpType(FOUR).setMethod(POST)
                .setUrl("https://api.openai.com/v1/audio/speech")
                .setHeaders(Dict.of(AUTHORIZATION_KEY, BEARER_KEY + config.getApiKey()))
                .setData((Dict) input));
    }

    /**
     * Transcribes audio into the input language.
     * @param input The required arguments
     * @return The result of operation
     * @see <a href="https://platform.openai.com/docs/api-reference/audio/createTranscription">
     *     Create transcription</a>
     */
    protected Dict transcriptionCreate(Object input, String operation) {
        Assert.notNull(input, "Parameter \"input\" must not null. ");
        Config config = getConfig(operation, input = Tool.ME.toDict(input));
        return doHttp(HttpData.of(Tool.ME).setConfig(config)
                .setHttpType(FOUR).setMethod(POST)
                .setUrl("https://api.openai.com/v1/audio/transcriptions")
                .setHeaders(Dict.of(AUTHORIZATION_KEY, BEARER_KEY + config.getApiKey()))
                .setData((Dict) input));
    }

    /**
     * Translates audio into English.
     * @param input The required arguments
     * @return The result of operation
     * @see <a href="https://platform.openai.com/docs/api-reference/audio/createTranslation">
     *     Create translation</a>
     */
    protected Dict translationCreate(Object input, String operation) {
        Assert.notNull(input, "Parameter \"input\" must not null. ");
        Config config = getConfig(operation, input = Tool.ME.toDict(input));
        return doHttp(HttpData.of(Tool.ME).setConfig(config)
                .setHttpType(FOUR).setMethod(POST)
                .setUrl("https://api.openai.com/v1/audio/translations")
                .setHeaders(Dict.of(AUTHORIZATION_KEY, BEARER_KEY + config.getApiKey()))
                .setData((Dict) input));
    }

    /**
     * Given a prompt, the model will return one or more predicted completions,
     *      and can also return the probabilities of alternative tokens at each position.
     *      We recommend most users use our Chat Completions API.
     * @param input The required arguments
     * @return The result of operation
     * @see <a href="https://platform.openai.com/docs/api-reference/completions/create">
     *     Create completion</a>
     */
    protected Dict completion(Object input, String operation) {
        Assert.notNull(input, "Parameter \"input\" must not null. ");
        Config config = getConfig(operation, input = Tool.ME.toDict(input));
        return doHttp(HttpData.of(Tool.ME).setConfig(config)
                .setHttpType(FOUR).setMethod(POST)
                .setUrl("https://api.openai.com/v1/completions")
                .setHeaders(Dict.of(AUTHORIZATION_KEY, BEARER_KEY + config.getApiKey()))
                .setData((Dict) input));
    }

    /**
     * Creates an image given a prompt.
     * @param input The required arguments
     * @return The result of operation
     * @see <a href="https://platform.openai.com/docs/api-reference/images/create">
     *     Create image</a>
     */
    protected Dict imageCreate(Object input, String operation) {
        Assert.notNull(input, "Parameter \"input\" must not null. ");
        Config config = getConfig(operation, input = Tool.ME.toDict(input));
        return doHttp(HttpData.of(Tool.ME).setConfig(config)
                .setHttpType(FOUR).setMethod(POST)
                .setUrl("https://api.openai.com/v1/images/generations")
                .setHeaders(Dict.of(AUTHORIZATION_KEY, BEARER_KEY + config.getApiKey()))
                .setData((Dict) input));
    }

    /**
     * Creates an edited or extended image given an original image and a prompt.
     * @param input The required arguments
     * @return The result of operation
     * @see <a href="https://platform.openai.com/docs/api-reference/images/createEdit">
     *     Create image edit</a>
     */
    protected Dict imageEdit(Object input, String operation) {
        Assert.notNull(input, "Parameter \"input\" must not null. ");
        Config config = getConfig(operation, input = Tool.ME.toDict(input));
        return doHttp(HttpData.of(Tool.ME).setConfig(config)
                .setHttpType(THREE).setMethod(POST)
                .setUrl("https://api.openai.com/v1/images/edits")
                .setHeaders(Dict.of(AUTHORIZATION_KEY, BEARER_KEY + config.getApiKey()))
                .setData((Dict) input));
    }

    /**
     * Creates a variation of a given image.
     * @param input The required arguments
     * @return The result of operation
     * @see <a href="https://platform.openai.com/docs/api-reference/images/createVariation">
     *     Create image variation</a>
     */
    protected Dict imageVariation(Object input, String operation) {
        Assert.notNull(input, "Parameter \"input\" must not null. ");
        Config config = getConfig(operation, input = Tool.ME.toDict(input));
        return doHttp(HttpData.of(Tool.ME).setConfig(config)
                .setHttpType(THREE).setMethod(POST)
                .setUrl("https://api.openai.com/v1/images/variations")
                .setHeaders(Dict.of(AUTHORIZATION_KEY, BEARER_KEY + config.getApiKey()))
                .setData((Dict) input));
    }

    /**
     * Lists the currently available models, and provides basic information about
     *      each one such as the owner and availability.
     * @param input The required arguments
     * @return The result of operation
     * @see <a href="https://platform.openai.com/docs/api-reference/models/list">
     *     List models</a>
     */
    protected Dict modelList(Object input, String operation) {
        Assert.notNull(input, "Parameter \"input\" must not null. ");
        Config config = getConfig(operation, input = Tool.ME.toDict(input));
        return doHttp(HttpData.of(Tool.ME).setConfig(config)
                .setHttpType(ONE).setMethod(GET)
                .setUrl("https://api.openai.com/v1/models")
                .setHeaders(Dict.of(AUTHORIZATION_KEY, BEARER_KEY + config.getApiKey()))
                .setData((Dict) input));
    }

    /**
     * The OpenAI AI handler internal tool.
     * @author Kahle
     */
    protected static class Tool extends InnerTool {
        /**
         * The internal tool instance.
         */
        public static final Tool ME = new Tool();
    }

    /**
     * The OpenAI AI handler configuration.
     * @author Kahle
     */
    public static class Config extends AbstractConfig {
        private String apiKey;

        public Config(String apiKey) {
            Assert.notBlank(apiKey, "Parameter \"apiKey\" must not blank. ");
            this.apiKey = apiKey;
        }

        public Config() {

        }

        public String getApiKey() {

            return apiKey;
        }

        public void setApiKey(String apiKey) {

            this.apiKey = apiKey;
        }
    }

}
