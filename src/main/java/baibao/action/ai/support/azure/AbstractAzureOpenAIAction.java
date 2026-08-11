/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.action.ai.support.azure;

import baibao.action.ai.support.AbstractHttpApiAIAction;
import kunlun.ai.model.ChatRequest;
import kunlun.ai.model.ChatResponse;
import kunlun.ai.model.EmbedRequest;
import kunlun.ai.model.EmbedResponse;
import kunlun.data.Dict;
import kunlun.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static kunlun.common.constant.Numbers.FOUR;
import static kunlun.net.http.HttpMethod.POST;

/**
 * The Azure OpenAI AI action.
 * @see <a href="https://learn.microsoft.com/zh-cn/azure/ai-services/openai/reference">Azure OpenAI 服务 REST API 参考</a>
 * @author Kahle
 */
public abstract class AbstractAzureOpenAIAction extends AbstractHttpApiAIAction {
    private static final Logger log = LoggerFactory.getLogger(AbstractAzureOpenAIAction.class);

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
        else {
            throw new UnsupportedOperationException(
                "The Azure OpenAI AI Handler. \n" +
                "(The api documents \"https://learn.microsoft.com/zh-cn/azure/ai-services/openai/reference\")\n" +
                "Supported method:\n" +
                " - chat\n" +
                " - embeddings\n"
            );
        }
    }

    protected ChatResponse chat(ChatRequest request) {
        // Handle input.
        Dict inputDict = Tool.ME.fromChatRequest(request);
        // Get config.
        Config config = getConfig(AIMethods.CHAT, inputDict);
        // The model is equivalent to deployment-id.
        String url = String.format(
                "%s/openai/deployments/%s/chat/completions?api-version=%s"
                , Tool.ME.getEndpoint(config), inputDict.getString(MODEL_KEY), config.getApiVersion()
        );
        // Invoke http.
        Dict respDict = doHttp(HttpData.of(Tool.ME).setConfig(config).setHttpType(FOUR)
                .setValidateCertificate(false).setMethod(POST).setUrl(url)
                .setHeaders(Dict.of("api-key", config.getApiKey()))
                .setData(inputDict));
        // Handle output.
        return Tool.ME.toChatResponse(respDict);
    }

    protected EmbedResponse embeddings(EmbedRequest request) {
        // Handle input.
        Dict inputDict = Tool.ME.fromEmbedRequest(request);
        // Get config.
        Config config = getConfig(AIMethods.EMBEDDINGS, inputDict);
        // The model is equivalent to deployment-id.
        String url = String.format(
                "%s/openai/deployments/%s/embeddings?api-version=%s"
                , Tool.ME.getEndpoint(config), inputDict.getString(MODEL_KEY), config.getApiVersion()
        );
        // Invoke http.
        Dict respDict = doHttp(HttpData.of(Tool.ME).setConfig(config).setHttpType(FOUR)
                .setValidateCertificate(false).setMethod(POST).setUrl(url)
                .setHeaders(Dict.of("api-key", config.getApiKey()))
                .setData(inputDict));
        // Handle output.
        return Tool.ME.toEmbedResponse(respDict);
    }

    /**
     * The Azure Open AI handler internal tool.
     * @author Kahle
     */
    protected static class Tool extends InnerTool {
        /**
         * The internal tool instance.
         */
        public static final Tool ME = new Tool();

        public String getEndpoint(Config config) {
            String endpoint = config.getEndpoint();
            if (endpoint == null) { return null; }
            if (!endpoint.startsWith("https://")) {
                endpoint = "https://" + endpoint;
            }
            return endpoint;
        }
    }

    /**
     * The Azure Open AI handler configuration.
     * @author Kahle
     */
    public static class Config extends AbstractConfig {
        private String apiKey;
        private String endpoint;
        private String apiVersion;

        public Config(String apiKey) {

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

        public String getEndpoint() {

            return endpoint;
        }

        public void setEndpoint(String endpoint) {

            this.endpoint = endpoint;
        }

        public String getApiVersion() {

            return apiVersion;
        }

        public void setApiVersion(String apiVersion) {

            this.apiVersion = apiVersion;
        }
    }

}
