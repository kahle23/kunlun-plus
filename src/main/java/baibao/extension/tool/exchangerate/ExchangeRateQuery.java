/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.tool.exchangerate;

import kunlun.common.constant.Nil;
import kunlun.util.Assert;
import kunlun.util.StrUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * The exchange rate query object.
 * @author Kahle
 */
public class ExchangeRateQuery implements Serializable {
    private List<String> baseCurrencies;
    private List<String> targetCurrencies;
    private Date   currentTime;
    private String source;

    public ExchangeRateQuery(List<String> baseCurrencies, List<String> targetCurrencies, Date currentTime) {
        this.baseCurrencies = Assert.notEmpty(baseCurrencies);
        this.targetCurrencies = targetCurrencies;
        this.currentTime = currentTime;
    }

    public ExchangeRateQuery(String baseCurrency, String targetCurrency) {
        if (StrUtil.isNotBlank(baseCurrency)) {
            this.baseCurrencies = singletonList(baseCurrency);
        }
        if (StrUtil.isNotBlank(targetCurrency)) {
            this.targetCurrencies = singletonList(targetCurrency);
        }
    }

    public ExchangeRateQuery(String baseCurrency) {

        this(baseCurrency, Nil.STR);
    }

    public ExchangeRateQuery() {

    }

    public List<String> getBaseCurrencies() {

        return baseCurrencies;
    }

    public void setBaseCurrencies(List<String> baseCurrencies) {

        this.baseCurrencies = baseCurrencies;
    }

    public List<String> getTargetCurrencies() {

        return targetCurrencies;
    }

    public void setTargetCurrencies(List<String> targetCurrencies) {

        this.targetCurrencies = targetCurrencies;
    }

    public Date getCurrentTime() {

        return currentTime;
    }

    public void setCurrentTime(Date currentTime) {

        this.currentTime = currentTime;
    }

    public String getSource() {

        return source;
    }

    public void setSource(String source) {

        this.source = source;
    }
}
