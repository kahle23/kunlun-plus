package baibao.extension.tool.hscode.support.likecha;

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
 * 立刻查 海关编码的 Action 测试类.
 * @see LikeChaQueryAction
 * @author Kahle
 */
@Slf4j
@Ignore
public class LikeChaQueryActionTest {
    private static final String HS_QUERY_NAME = Actions.HS_CODE_02;

    static {
        JsonUtil.registerProcessor(JsonUtil.getDefaultProcessorName(), new FastJsonProcessor());
        ActionUtil.registerAction(HS_QUERY_NAME, new LikeChaQueryAction());
    }

    @Test
    public void pagingQueryTest() {
        HsQuery hsQuery = new HsQuery();
        hsQuery.setName("女士夹克");
//        hsQuery.setCode("60");
        Page<HsData> page = ActionUtil.execute(HS_QUERY_NAME, hsQuery);
        log.info("{}", JSONUtil.toJsonPrettyStr(page));
    }

    @Test
    public void detailQueryTest() {
        HsData hsData = ActionUtil.execute(HS_QUERY_NAME, "6110300090");
        log.info("{}", JSONUtil.toJsonPrettyStr(hsData));
    }

}
