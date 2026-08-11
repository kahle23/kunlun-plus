package baibao.extension.tool.exchangerate.support.jdbc;

import baibao.extension.tool.exchangerate.AbstractExchangeRateAction;
import baibao.extension.tool.exchangerate.ExchangeRate;
import baibao.extension.tool.exchangerate.ExchangeRateQuery;
import cn.hutool.core.util.StrUtil;
import kunlun.data.Dict;
import kunlun.util.MapUtil;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.convert.Convert.*;
import static cn.hutool.core.date.DateUtil.formatDate;
import static cn.hutool.core.date.DateUtil.parseDate;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static kunlun.common.constant.Numbers.ONE;
import static kunlun.common.constant.Symbols.EMPTY_STRING;
import static kunlun.common.constant.Words.RAW1;
import static kunlun.util.Assert.*;
import static kunlun.util.CollUtil.getFirst;

/**
 * BaseJdbcExchangeRateAction
 *
 * <pre><code>
 * CREATE TABLE `t_exchange_rate` (
 *   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
 *   `base_currency` varchar(10) NOT NULL COMMENT '基础币种',
 *   `target_currency` varchar(10) NOT NULL COMMENT '目标币种',
 *   `time` date NOT NULL COMMENT '汇率时间',
 *   `rate` decimal(10,5) NOT NULL COMMENT '汇率值',
 *   PRIMARY KEY (`id`) USING BTREE,
 *   UNIQUE KEY `idx_base_tar_time` (`base_currency`,`target_currency`,`time`)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='汇率表';
 * </code></pre>
 *
 * @author Kahle
 */
public abstract class BaseJdbcExRateAction extends AbstractExchangeRateAction {
    /**
     * The table name.
     */
    protected String tableName = "t_exchange_rate";

    /**
     * getJdbcTemplate
     * @return getJdbcTemplate
     */
    protected abstract NamedParameterJdbcTemplate getJdbcTemplate();

    /**
     * dbQuery
     * @param rateQuery rateQuery
     * @return ExchangeRate
     */
    protected List<ExchangeRate> dbQuery(ExchangeRateQuery rateQuery) {
        // 参数校验
        notNull(rateQuery); notEmpty(rateQuery.getTargetCurrencies());
        notEmpty(rateQuery.getBaseCurrencies());
        isTrue(rateQuery.getTargetCurrencies().size() == ONE);
        isTrue(rateQuery.getBaseCurrencies().size() == ONE);
        // 参数提取
        String targetCurrency = notBlank(getFirst(rateQuery.getTargetCurrencies()));
        String baseCurrency = notBlank(getFirst(rateQuery.getBaseCurrencies()));
        // 来源币种和目标币种相等，直接返回1
        if (StrUtil.equalsIgnoreCase(baseCurrency, targetCurrency)) {
            return singletonList(new ExchangeRate(baseCurrency, targetCurrency, BigDecimal.ONE));
        }
        // 构建SQL
        String sql = "SELECT * FROM `" + tableName + "` \n" +
                "WHERE `base_currency` = :baseCurrency \n" +
                "AND `target_currency` = :targetCurrency \n" +
                (nonNull(rateQuery.getCurrentTime()) ? "AND `time` <= :time \n" : EMPTY_STRING) +
                "ORDER BY `time` DESC LIMIT 1";
        // 构建SQL的参数
        Dict param = Dict.of("baseCurrency", baseCurrency).set("targetCurrency", targetCurrency);
        if (nonNull(rateQuery.getCurrentTime())) {
            param.set("time", parseDate(formatDate(rateQuery.getCurrentTime())));
        }
        // 执行SQL
        Map<String, Object> data = getFirst(getJdbcTemplate().queryForList(sql, param));
        if (MapUtil.isEmpty(data)) { return null; }
        // 构建结果对象
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setId(toStr(data.get("id")));
        exchangeRate.setBaseCurrency(toStr(data.get("base_currency")));
        exchangeRate.setTargetCurrency(toStr(data.get("target_currency")));
        exchangeRate.setTime(toDate(data.get("time")));
        exchangeRate.setRate(toBigDecimal(data.get("rate")));
        exchangeRate.setOthers(Dict.of(RAW1, data));
        return singletonList(exchangeRate);
    }

    /**
     * dbSaveOrUpdate
     * @param exchangeRate exchangeRate
     * @return Object
     */
    protected Object dbSaveOrUpdate(ExchangeRate exchangeRate) {
        // 参数校验
        notNull(exchangeRate);
        // 构建SQL
        String sql = "INSERT INTO `" + tableName + "` (`id`, `base_currency`, `target_currency`, `time`, `rate`)\n" +
                "VALUES (null, :baseCurrency, :targetCurrency, :time, :rate)\n" +
                "ON DUPLICATE KEY UPDATE\n" +
                "`rate` = VALUES(`rate`);\n";
        // 执行SQL
        return getJdbcTemplate().update(sql, Dict.of()
                .set("baseCurrency", notBlank(exchangeRate.getBaseCurrency()))
                .set("targetCurrency", notBlank(exchangeRate.getTargetCurrency()))
                .set("time", notNull(exchangeRate.getTime()))
                .set("rate", notNull(exchangeRate.getRate())));
    }

    @Override
    public Object execute(String strategy, Object input, Object[] arguments) {
        if ("mainQuery".equals(strategy)) {
            isSupport(notNull(input).getClass(), Boolean.TRUE, ExchangeRateQuery.class);
            return mainQuery((ExchangeRateQuery) input);
        } else if ("dbQuery".equals(strategy)) {
            isSupport(notNull(input).getClass(), Boolean.TRUE, ExchangeRateQuery.class);
            return dbQuery((ExchangeRateQuery) input);
        } else if ("dbSaveOrUpdate".equals(strategy)) {
            isSupport(notNull(input).getClass(), Boolean.TRUE, ExchangeRate.class);
            return dbSaveOrUpdate((ExchangeRate) input);
        } else {
            isSupport(notNull(input).getClass(), Boolean.TRUE, ExchangeRateQuery.class);
            return mainQuery((ExchangeRateQuery) input);
        }
    }

}
