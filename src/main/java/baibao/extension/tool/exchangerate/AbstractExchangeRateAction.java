package baibao.extension.tool.exchangerate;

import kunlun.core.Action;
import kunlun.util.Assert;

import java.util.List;

import static kunlun.util.Assert.notNull;

/**
 * AbstractExchangeRateAction
 * @author Kahle
 */
public abstract class AbstractExchangeRateAction implements Action {

    /**
     * mainQuery.
     * @param rateQuery rateQuery
     * @return ExchangeRate List
     */
    protected abstract List<ExchangeRate> mainQuery(ExchangeRateQuery rateQuery);

    @Override
    public Object execute(String strategy, Object input, Object[] arguments) {
        // Parameter validation.
        Assert.isSupport(notNull(input).getClass(), Boolean.TRUE, ExchangeRateQuery.class);
        // Query the exchange rate.
        return mainQuery((ExchangeRateQuery) input);
    }

}
