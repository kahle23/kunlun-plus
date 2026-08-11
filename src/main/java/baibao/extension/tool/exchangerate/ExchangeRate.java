/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.tool.exchangerate;

import kunlun.data.Dict;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 汇率的数据对象.
 * @see <a href="https://en.wikipedia.org/wiki/Exchange_rate">Exchange rate</a>
 * @since 2025-04-23
 * @author Kahle
 */
public class ExchangeRate implements Serializable {
    /**
     * 汇率记录的ID
     */
    private String id;
    /**
     * 基础币种
     */
    private String baseCurrency;
    /**
     * 基础币种的名称
     */
    private String baseCurrencyName;
    /**
     * 目标币种
     */
    private String targetCurrency;
    /**
     * 目标币种名称
     */
    private String targetCurrencyName;
    /**
     * 汇率时间
     */
    private Date time;
    /**
     * 汇率的值
     */
    private BigDecimal rate;
    /**
     * 其他
     */
    private Dict others;

    public ExchangeRate(String baseCurrency, String targetCurrency, BigDecimal rate) {
        this.targetCurrency = targetCurrency;
        this.baseCurrency = baseCurrency;
        this.rate = rate;
    }

    public ExchangeRate(String baseCurrency, String targetCurrency) {
        this.targetCurrency = targetCurrency;
        this.baseCurrency = baseCurrency;
    }

    public ExchangeRate() {

        others = Dict.of();
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getBaseCurrency() {

        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {

        this.baseCurrency = baseCurrency;
    }

    public String getBaseCurrencyName() {

        return baseCurrencyName;
    }

    public void setBaseCurrencyName(String baseCurrencyName) {

        this.baseCurrencyName = baseCurrencyName;
    }

    public String getTargetCurrency() {

        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {

        this.targetCurrency = targetCurrency;
    }

    public String getTargetCurrencyName() {

        return targetCurrencyName;
    }

    public void setTargetCurrencyName(String targetCurrencyName) {

        this.targetCurrencyName = targetCurrencyName;
    }

    public Date getTime() {

        return time;
    }

    public void setTime(Date time) {

        this.time = time;
    }

    public BigDecimal getRate() {

        return rate;
    }

    public void setRate(BigDecimal rate) {

        this.rate = rate;
    }

    public Dict getOthers() {

        return others;
    }

    public void setOthers(Dict others) {

        this.others = others;
    }
}
