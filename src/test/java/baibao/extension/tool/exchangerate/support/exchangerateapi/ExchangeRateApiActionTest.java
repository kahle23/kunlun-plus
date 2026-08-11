package baibao.extension.tool.exchangerate.support.exchangerateapi;

import baibao.extension.tool.exchangerate.ExchangeRate;
import baibao.extension.tool.exchangerate.ExchangeRateQuery;
import cn.hutool.json.JSONUtil;
import kunlun.action.ActionUtil;
import kunlun.data.json.JsonUtil;
import kunlun.data.json.support.FastJsonProcessor;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Ignore
public class ExchangeRateApiActionTest {
    private static final Logger log = LoggerFactory.getLogger(ExchangeRateApiActionTest.class);
    private static final String EXCHANGE_RATE_NAME = "exchangerate-api";

    static {
        JsonUtil.registerProcessor(JsonUtil.getDefaultProcessorName(), new FastJsonProcessor());
        ActionUtil.registerAction(EXCHANGE_RATE_NAME, new ExchangeRateApiAction());
        ActionUtil.registerShortcut(ExchangeRateQuery.class, EXCHANGE_RATE_NAME);
    }

    @Test
    public void test1() {
        List<ExchangeRate> rates = ActionUtil.execute(new ExchangeRateQuery("USD"/*, "CNY"*/));
        log.info("{}", JSONUtil.toJsonPrettyStr(rates));
    }

}
