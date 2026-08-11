package baibao.action.ai.support.zhipu;

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

public abstract class AbstractZhipuAIAction extends AbstractHttpApiAIAction {
    private static final Logger log = LoggerFactory.getLogger(AbstractZhipuAIAction.class);

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
                "The Zhipu AI Handler. \n" +
                "(The api documents \"https://maas.aminer.cn/dev/api\")\n" +
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
        // Invoke http.
        Dict respDict = doHttp(HttpData.of(Tool.ME).setConfig(config)
                .setHttpType(FOUR).setMethod(POST)
                .setUrl("https://open.bigmodel.cn/api/paas/v4/chat/completions")
                .setHeaders(Dict.of(AUTHORIZATION_KEY, BEARER_KEY + config.getApiKey()))
                .setData(inputDict));
        // Handle output.
        return Tool.ME.toChatResponse(respDict);
    }

    protected EmbedResponse embeddings(EmbedRequest request) {
        // Handle input.
        Dict inputDict = Tool.ME.fromEmbedRequest(request);
        // Get config.
        Config config = getConfig(AIMethods.EMBEDDINGS, inputDict);
        // Invoke http.
        Dict respDict = doHttp(HttpData.of(Tool.ME).setConfig(config)
                .setHttpType(FOUR).setMethod(POST)
                .setUrl("https://open.bigmodel.cn/api/paas/v4/embeddings")
                .setHeaders(Dict.of(AUTHORIZATION_KEY, BEARER_KEY + config.getApiKey()))
                .setData(inputDict));
        // Handle output.
        return Tool.ME.toEmbedResponse(respDict);
    }

    /**
     * The Zhipu AI Handler internal tool.
     * @author Kahle
     */
    protected static class Tool extends InnerTool {
        /**
         * The internal tool instance.
         */
        public static final Tool ME = new Tool();
    }

    /**
     * The Zhipu AI Handler configuration.
     * @author Kahle
     */
    public static class Config extends AbstractConfig {
        private String apiKey;

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
    }

}
