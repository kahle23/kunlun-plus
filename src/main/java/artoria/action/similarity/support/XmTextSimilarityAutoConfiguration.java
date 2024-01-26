package artoria.action.similarity.support;

import artoria.action.ActionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.xm.Similarity;

@Configuration
@ConditionalOnClass(Similarity.class)
public class XmTextSimilarityAutoConfiguration {

    public XmTextSimilarityAutoConfiguration() {
        XmTextSimilarityHandler handler = new XmTextSimilarityHandler();
        ActionUtils.registerHandler("text-similarity", handler);
    }

}
