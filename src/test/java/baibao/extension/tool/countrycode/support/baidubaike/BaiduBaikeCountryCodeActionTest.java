package baibao.extension.tool.countrycode.support.baidubaike;

import baibao.extension.tool.countrycode.CountryCode;
import baibao.extension.tool.countrycode.CountryCodeQuery;
import cn.hutool.json.JSONUtil;
import kunlun.action.ActionUtil;
import kunlun.data.json.JsonUtil;
import kunlun.data.json.support.FastJsonProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

@Slf4j
@Ignore
public class BaiduBaikeCountryCodeActionTest {
    private static final String COUNTRY_CODE_NAME = "country-code";

    static {
        JsonUtil.registerProcessor(JsonUtil.getDefaultProcessorName(), new FastJsonProcessor());
        ActionUtil.registerAction(COUNTRY_CODE_NAME, new BaiduBaikeCountryCodeAction());
        ActionUtil.registerShortcut(CountryCodeQuery.class, COUNTRY_CODE_NAME);
    }

    @Test
    public void test1() {
        List<CountryCode> countryCodes = ActionUtil.execute(new CountryCodeQuery());
        log.info("{}", JSONUtil.toJsonPrettyStr(countryCodes));
    }
}
