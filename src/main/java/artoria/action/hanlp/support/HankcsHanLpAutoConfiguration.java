package artoria.action.hanlp.support;

import artoria.action.ActionUtils;
import artoria.action.hanlp.TextSegment;
import com.hankcs.hanlp.HanLP;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(HanLP.class)
public class HankcsHanLpAutoConfiguration {

    public HankcsHanLpAutoConfiguration() {
        HankcsSegmentHandler handler = new HankcsSegmentHandler();
        ActionUtils.registerHandler("text-segment", handler);
        ActionUtils.registerHandler(TextSegment.class, handler);
    }

}
