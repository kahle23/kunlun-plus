package baibao.extension.tool.countrycode;

import kunlun.core.Action;
import kunlun.util.Assert;

import java.util.List;

import static kunlun.util.Assert.notNull;

/**
 * AbstractCountryCodeAction
 * @see <a href="https://www.iso.org/iso-3166-country-codes.html">ISO 3166 Country Codes</a>
 * @author Kahle
 */
public abstract class AbstractCountryCodeAction implements Action {

    /**
     * mainQuery.
     * @param countryCodeQuery CountryCodeQuery
     * @return CountryCode List
     */
    protected abstract List<CountryCode> mainQuery(CountryCodeQuery countryCodeQuery);

    @Override
    public Object execute(String strategy, Object input, Object[] arguments) {
        // Parameter validation.
        Assert.isSupport(notNull(input).getClass(), Boolean.TRUE, CountryCodeQuery.class);
        // Query the exchange rate.
        return mainQuery((CountryCodeQuery) input);
    }

}
