/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.extension.tool.exchangerate;

import baibao.extension.tool.exchangerate.support.SimpleExRateFiller;
import kunlun.action.ActionUtil;
import kunlun.common.constant.Nil;
import kunlun.common.constant.Words;
import kunlun.core.function.BiConsumer;
import kunlun.core.function.Consumer;
import kunlun.core.function.Function;
import kunlun.extension.convert.exchangerate.ExRateFiller;
import kunlun.util.Assert;
import kunlun.util.CollUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static kunlun.common.constant.Numbers.*;
import static kunlun.util.Assert.*;

/**
 * ExRateUtil.
 * @see AbstractExchangeRateAction
 * @author Kahle
 */
public class ExRateUtil {
    private static volatile Consumer<ExchangeRateQuery> preProcessor;
    private static volatile BiConsumer<ExchangeRateQuery, ExchangeRate> resultProcessor;
    private static volatile ExRateFiller exchangeRateFiller;
    private static Function<ExchangeRateQuery, String> commandProcessor;


    // region ======== processor manage ========

    public static Consumer<ExchangeRateQuery> getPreProcessor() {
        if (preProcessor != null) { return preProcessor; }
        synchronized (ExRateUtil.class) {
            if (preProcessor != null) { return preProcessor; }
            ExRateUtil.setPreProcessor(exRateQuery -> {
                notEmpty(exRateQuery.getBaseCurrencies());
                notBlank(CollUtil.getFirst(exRateQuery.getBaseCurrencies()));
                notEmpty(exRateQuery.getTargetCurrencies());
                notBlank(CollUtil.getFirst(exRateQuery.getTargetCurrencies()));
            });
            return preProcessor;
        }
    }

    public static void setPreProcessor(Consumer<ExchangeRateQuery> preProcessor) {

        ExRateUtil.preProcessor = notNull(preProcessor);
    }

    public static BiConsumer<ExchangeRateQuery, ExchangeRate> getResultProcessor() {
        if (resultProcessor != null) { return resultProcessor; }
        synchronized (ExRateUtil.class) {
            if (resultProcessor != null) { return resultProcessor; }
            ExRateUtil.setResultProcessor((exRateQry, exRate) -> {
            });
            return resultProcessor;
        }
    }

    public static void setResultProcessor(BiConsumer<ExchangeRateQuery, ExchangeRate> resultProcessor) {

        ExRateUtil.resultProcessor = notNull(resultProcessor);
    }

    public static Function<ExchangeRateQuery, String> getCommandProcessor() {
        Assert.state(commandProcessor != null);
        return commandProcessor;
    }

    public static void setCommandProcessor(Function<ExchangeRateQuery, String> commandProcessor) {

        ExRateUtil.commandProcessor = notNull(commandProcessor);
    }

    public static ExRateFiller getExchangeRateFiller() {
        if (exchangeRateFiller != null) { return exchangeRateFiller; }
        synchronized (ExRateUtil.class) {
            if (exchangeRateFiller != null) { return exchangeRateFiller; }
            ExRateUtil.setExchangeRateFiller(new SimpleExRateFiller());
            return exchangeRateFiller;
        }
    }

    public static void setExchangeRateFiller(ExRateFiller exchangeRateFiller) {

        ExRateUtil.exchangeRateFiller = notNull(exchangeRateFiller);
    }
    // endregion


    // region ======== query rate ========

    public static BigDecimal getRate(String source, String baseCcy, String targetCcy) {

        return getRate(source, baseCcy, targetCcy, Nil.DATE);
    }

    public static BigDecimal getRate(String source, String baseCcy, String targetCcy, Date curTime) {
        ExchangeRate rate = rate(source, baseCcy, targetCcy, curTime);
        return rate != null ? rate.getRate() : null;
    }

    public static ExchangeRate rate(String source, String baseCcy, String targetCcy) {

        return rate(source, baseCcy, targetCcy, Nil.DATE);
    }

    public static ExchangeRate rate(String source, String baseCcy, String targetCcy, Date curTime) {
        // Build query.
        ExchangeRateQuery query = new ExchangeRateQuery(baseCcy, targetCcy);
        query.setCurrentTime(curTime);
        query.setSource(source);
        // Pre process.
        getPreProcessor().accept(query);
        // Command.
        String cmd = getCommandProcessor().apply(query);
        // Execute.
        List<ExchangeRate> rates = ActionUtil.execute(cmd, query);
        ExchangeRate result = CollUtil.getFirst(rates);
        // Result process.
        getResultProcessor().accept(query, result);
        return result;
    }
    // endregion


    // region ======== 汇率填充 ========

    public static void fill(Object data) {

        getExchangeRateFiller().fill(data);
    }
    // endregion


    // region ======== query rate 1 ========

    public static BigDecimal getRate1(String baseCcy, String targetCcy) {

        return getRate1(baseCcy, targetCcy, Nil.DATE);
    }

    public static BigDecimal getRate1(String baseCcy, String targetCcy, Date curTime) {
        ExchangeRate rate = rate1(baseCcy, targetCcy, curTime);
        return rate != null ? rate.getRate() : null;
    }

    public static ExchangeRate rate1(String baseCcy, String targetCcy) {

        return rate1(baseCcy, targetCcy, Nil.DATE);
    }

    public static ExchangeRate rate1(String baseCcy, String targetCcy, Date curTime) {

        return rate(Words.DEFAULT + ONE, baseCcy, targetCcy, curTime);
    }
    // endregion


    // region ======== query rate 2 ========

    public static BigDecimal getRate2(String baseCcy, String targetCcy) {

        return getRate2(baseCcy, targetCcy, Nil.DATE);
    }

    public static BigDecimal getRate2(String baseCcy, String targetCcy, Date curTime) {
        ExchangeRate rate = rate2(baseCcy, targetCcy, curTime);
        return rate != null ? rate.getRate() : null;
    }

    public static ExchangeRate rate2(String baseCcy, String targetCcy) {

        return rate2(baseCcy, targetCcy, Nil.DATE);
    }

    public static ExchangeRate rate2(String baseCcy, String targetCcy, Date curTime) {

        return rate(Words.DEFAULT + TWO, baseCcy, targetCcy, curTime);
    }
    // endregion


    // region ======== query rate 3 ========

    public static BigDecimal getRate3(String baseCcy, String targetCcy) {

        return getRate3(baseCcy, targetCcy, Nil.DATE);
    }

    public static BigDecimal getRate3(String baseCcy, String targetCcy, Date curTime) {
        ExchangeRate rate = rate3(baseCcy, targetCcy, curTime);
        return rate != null ? rate.getRate() : null;
    }

    public static ExchangeRate rate3(String baseCcy, String targetCcy) {

        return rate3(baseCcy, targetCcy, Nil.DATE);
    }

    public static ExchangeRate rate3(String baseCcy, String targetCcy, Date curTime) {

        return rate(Words.DEFAULT + THREE, baseCcy, targetCcy, curTime);
    }
    // endregion


    // region ======== query rate 4 ========

    public static BigDecimal getRate4(String baseCcy, String targetCcy) {

        return getRate4(baseCcy, targetCcy, Nil.DATE);
    }

    public static BigDecimal getRate4(String baseCcy, String targetCcy, Date curTime) {
        ExchangeRate rate = rate4(baseCcy, targetCcy, curTime);
        return rate != null ? rate.getRate() : null;
    }

    public static ExchangeRate rate4(String baseCcy, String targetCcy) {

        return rate4(baseCcy, targetCcy, Nil.DATE);
    }

    public static ExchangeRate rate4(String baseCcy, String targetCcy, Date curTime) {

        return rate(Words.DEFAULT + FOUR, baseCcy, targetCcy, curTime);
    }
    // endregion


    // region ======== query rate 5 ========

    public static BigDecimal getRate5(String baseCcy, String targetCcy) {

        return getRate5(baseCcy, targetCcy, Nil.DATE);
    }

    public static BigDecimal getRate5(String baseCcy, String targetCcy, Date curTime) {
        ExchangeRate rate = rate5(baseCcy, targetCcy, curTime);
        return rate != null ? rate.getRate() : null;
    }

    public static ExchangeRate rate5(String baseCcy, String targetCcy) {

        return rate5(baseCcy, targetCcy, Nil.DATE);
    }

    public static ExchangeRate rate5(String baseCcy, String targetCcy, Date curTime) {

        return rate(Words.DEFAULT + FIVE, baseCcy, targetCcy, curTime);
    }
    // endregion

}
