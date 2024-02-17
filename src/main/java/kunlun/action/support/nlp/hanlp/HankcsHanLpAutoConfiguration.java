/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support.nlp.hanlp;

import com.hankcs.hanlp.HanLP;
import kunlun.action.ActionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(HanLP.class)
public class HankcsHanLpAutoConfiguration {

    public HankcsHanLpAutoConfiguration() {
        HankcsSegmentHandler handler = new HankcsSegmentHandler();
        ActionUtils.registerHandler("text-segment", handler);
    }

}
