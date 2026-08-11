/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.tool.exchangerate.support.exchangerateapi;

import baibao.extension.tool.exchangerate.AbstractExchangeRateAction;
import baibao.extension.tool.exchangerate.ExchangeRate;
import baibao.extension.tool.exchangerate.ExchangeRateQuery;
import kunlun.data.Dict;
import kunlun.data.json.JsonUtil;
import kunlun.net.http.HttpMethod;
import kunlun.net.http.HttpUtil;
import kunlun.net.http.support.SimpleRequest;
import kunlun.time.DateUtil;
import kunlun.util.CollUtil;
import kunlun.util.MapUtil;
import kunlun.util.ObjUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static kunlun.common.constant.Numbers.ONE;
import static kunlun.common.constant.TimePatterns.Y4MD2MI;
import static kunlun.util.StrUtil.isBlank;

/**
 * ExchangeRate-API
 * @see <a href="https://www.exchangerate-api.com/">ExchangeRate-API</a>
 * @see <a href="https://www.exchangerate-api.com/docs/free">Open Access Endpoint</a>
 * @author Kahle
 */
public class ExchangeRateApiAction extends AbstractExchangeRateAction {
    private static final Logger log = LoggerFactory.getLogger(ExchangeRateApiAction.class);

    @Override
    protected List<ExchangeRate> mainQuery(ExchangeRateQuery rateQuery) {
        // Parameter verification and preprocess.
        if (CollUtil.isEmpty(rateQuery.getBaseCurrencies())
                || rateQuery.getBaseCurrencies().size() != ONE) {
            return null;
        }
        String baseCurrency = CollUtil.getFirst(rateQuery.getBaseCurrencies());
        if (isBlank(baseCurrency)) { return null; }
        baseCurrency = baseCurrency.trim().toUpperCase();
        List<String> targetCurrencies = new ArrayList<String>();
        if (CollUtil.isNotEmpty(rateQuery.getTargetCurrencies())) {
            for (String currency : rateQuery.getTargetCurrencies()) {
                if (isBlank(currency)) { continue; }
                targetCurrencies.add(currency.trim().toUpperCase());
            }
        }
        // Invoke the API interface.
        String url = "https://api.exchangerate-api.com/v4/latest/" + baseCurrency;
        SimpleRequest request = SimpleRequest.of(HttpMethod.GET, url);
        request.setValidateCertificate(Boolean.FALSE);
        String jsonString = HttpUtil.execute(request).getBodyAsString();
        log.info("The raw exchange rate information: {}", jsonString);
        if (isBlank(jsonString)) { return null; }
        Dict dict = JsonUtil.parseObject(jsonString, Dict.class);
        if (MapUtil.isEmpty(dict)) { return null; }
        // Construct the result object.
        Date time = DateUtil.parse(dict.getString("date"), Y4MD2MI);
        String base = dict.getString("base");
        Dict rates = dict.getDict("rates");
        List<ExchangeRate> results = new ArrayList<ExchangeRate>();
        if (MapUtil.isEmpty(rates)) { return results; }
        for (Map.Entry<String, Object> entry : rates.entrySet()) {
            if (isBlank(entry.getKey()) || ObjUtil.isEmpty(entry.getValue())) { continue; }
            if (baseCurrency.equalsIgnoreCase(entry.getKey().trim())) { continue; }
            if (CollUtil.isNotEmpty(rateQuery.getTargetCurrencies())
                    && !targetCurrencies.contains(entry.getKey().trim().toUpperCase())) {
                continue;
            }
            ExchangeRate exchangeRate = new ExchangeRate(base, entry.getKey());
            exchangeRate.setRate(new BigDecimal(String.valueOf(entry.getValue())));
            exchangeRate.setTime(time);
            results.add(exchangeRate);
        }
        return results;
    }
}
