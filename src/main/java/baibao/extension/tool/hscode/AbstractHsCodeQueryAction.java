package baibao.extension.tool.hscode;

import kunlun.common.Page;
import kunlun.core.Action;

import static kunlun.util.Assert.notNull;

/**
 * AbstractHsCodeQueryAction
 * @author Kahle
 */
public abstract class AbstractHsCodeQueryAction implements Action {
    protected static final String USER_AGENT_VAL = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36";
    protected static final String USER_AGENT_KEY = "user-agent";

    /**
     * pagingQuery.
     * @param hsQuery hsQuery
     * @return Page HsData
     */
    protected abstract Page<HsData> pagingQuery(HsQuery hsQuery);

    /**
     * detailQuery
     * @param hsCode hsCode
     * @return HsData
     */
    protected abstract HsData detailQuery(String hsCode);

    @Override
    public Object execute(String strategy, Object input, Object[] arguments) {
        notNull(input);
        if ("pagingQuery".equals(strategy)) {
            return pagingQuery((HsQuery) input);
        } else if ("detailQuery".equals(strategy)) {
            return detailQuery(String.valueOf(input));
        } else {
            if (input instanceof HsQuery) {
                return pagingQuery((HsQuery) input);
            } else {
                return detailQuery(String.valueOf(input));
            }
        }
    }

}
