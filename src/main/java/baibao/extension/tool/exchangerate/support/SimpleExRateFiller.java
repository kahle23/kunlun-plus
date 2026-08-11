package baibao.extension.tool.exchangerate.support;

import baibao.extension.tool.exchangerate.ExRateUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import kunlun.data.bean.support.FieldBasedBeanMap;
import kunlun.extension.convert.exchangerate.ExRateFiller;
import kunlun.extension.convert.exchangerate.annotation.ExRate;
import kunlun.util.CastUtil;
import kunlun.util.CollUtil;
import kunlun.util.MapUtil;
import kunlun.util.StrUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * 注解 ExRate 的填充的实现类.
 * @author Zerox
 */
public class SimpleExRateFiller implements ExRateFiller {

    @Override
    public void fill(Object data) {
        Collection<Object> coll = data instanceof Collection ? CastUtil.cast(data) : Collections.singletonList(data);
        Object first = CollUtil.getFirst(coll);
        Class<?> aClass = first.getClass();
        //
        Map<String, ExRate> exRateMap = new LinkedHashMap<>();
        Map<String, Field> fieldMap = ReflectUtil.getFieldMap(aClass);
        for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
            ExRate exRate = entry.getValue().getAnnotation(ExRate.class);
            if (exRate == null) { continue; }
            exRateMap.put(entry.getKey(), exRate);
        }
        if (MapUtil.isEmpty(exRateMap)) { return; }
        //
        for (Object obj : coll) {
            if (obj == null) { continue; }
            Map<Object, Object> beanMap = new FieldBasedBeanMap(obj);
            //
            for (Map.Entry<String, ExRate> entry : exRateMap.entrySet()) {
                String fieldName = entry.getKey();
                ExRate exRate = entry.getValue();
                // 原始对象中有值，跳过
                Object fieldVal = beanMap.get(fieldName);
                if (fieldVal != null) { continue; }
                //
                doFill(beanMap, fieldName, exRate);
            }
        }
    }

    protected void doFill(Map<Object, Object> beanMap, String fieldName, ExRate exRate) {
        String amountField = exRate.amountField();
        String source = exRate.source();
        String base = exRate.base();
        String baseField = exRate.baseField();
        String target = exRate.target();
        String targetField = exRate.targetField();
        String timeField = exRate.timeField();

        if (StrUtil.isBlank(base) && StrUtil.isBlank(baseField)) { return; }
        if (StrUtil.isBlank(target) && StrUtil.isBlank(targetField)) { return; }
        // 基础币种获取
        // 如果 base 有值，则走 base，反之走 baseField
        if (StrUtil.isBlank(base)) {
            base = Convert.toStr(beanMap.get(baseField));
        }
        if (StrUtil.isBlank(base)) { return; }
        // 目标币种获取
        // 如果 target 有值，则走 target，反之走 targetField
        if (StrUtil.isBlank(target)) {
            target = Convert.toStr(beanMap.get(targetField));
        }
        if (StrUtil.isBlank(target)) { return; }
        // 汇率时间获取
        Date time = null;
        if (StrUtil.isNotBlank(timeField)) {
            time = Convert.toDate(beanMap.get(timeField));
        }
        if (time == null) { time = new Date(); }
        // 金额获取
        BigDecimal amount = null;
        if (StrUtil.isNotBlank(amountField)) {
            amount = Convert.toBigDecimal(beanMap.get(amountField));
        }

        //
        BigDecimal rate = ExRateUtil.getRate(source, base, target, time);
        if (rate == null) { return; }
        if (StrUtil.isBlank(amountField)) {
            beanMap.put(fieldName, rate);
            return;
        }

        //
        if (amount == null) { return; }
        BigDecimal multiply = amount.multiply(rate);
        if (multiply != null) {
            beanMap.put(fieldName, multiply);
        }
    }
}
