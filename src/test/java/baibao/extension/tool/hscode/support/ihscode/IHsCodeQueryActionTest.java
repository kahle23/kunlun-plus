package baibao.extension.tool.hscode.support.ihscode;

import baibao.common.constant.Actions;
import baibao.extension.tool.hscode.HsData;
import baibao.extension.tool.hscode.HsQuery;
import cn.hutool.json.JSONUtil;
import kunlun.action.ActionUtil;
import kunlun.common.Page;
import kunlun.data.json.JsonUtil;
import kunlun.data.json.support.FastJsonProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

/**
 * HS海关编码查询的 Action 测试类.
 * @see IHsCodeQueryAction
 * @author Kahle
 */
@Slf4j
@Ignore
public class IHsCodeQueryActionTest {
    private static final String HS_QUERY_NAME = Actions.HS_CODE_01;

    static {
        JsonUtil.registerProcessor(JsonUtil.getDefaultProcessorName(), new FastJsonProcessor());
        ActionUtil.registerAction(HS_QUERY_NAME, new IHsCodeQueryAction());
    }

    @Test
    public void pagingQueryTest() {
        HsQuery hsQuery = new HsQuery();
        hsQuery.setKeywords("女士夹克");
//        hsQuery.setKeywords("60");
        Page<HsData> page = ActionUtil.execute(HS_QUERY_NAME, hsQuery);
        log.info("{}", JSONUtil.toJsonPrettyStr(page));
    }

    @Test
    public void detailQueryTest() {
        HsData hsData = ActionUtil.execute(HS_QUERY_NAME, "4203100090");
        log.info("{}", JSONUtil.toJsonPrettyStr(hsData));
    }

}
