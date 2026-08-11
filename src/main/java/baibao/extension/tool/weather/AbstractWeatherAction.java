package baibao.extension.tool.weather;

import kunlun.action.AbstractAction;
import kunlun.util.Assert;

import static kunlun.util.Assert.notNull;

public abstract class AbstractWeatherAction extends AbstractAction {

    protected abstract Weather doQuery(WeatherQuery weatherQuery);

    @Override
    public Object execute(String strategy, Object input, Object[] arguments) {
        // Parameter validation.
        Assert.isSupport(notNull(input).getClass(), Boolean.TRUE, WeatherQuery.class);
        // Query the exchange rate.
        return doQuery((WeatherQuery) input);
    }

}
