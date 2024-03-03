/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support.jdbc.spring;

import kunlun.action.support.jdbc.AbstractScriptBasedJdbcInvokeHandler;
import kunlun.action.support.jdbc.JdbcInvokeConfig;
import kunlun.common.Page;
import kunlun.data.Array;
import kunlun.data.Dict;
import kunlun.data.bean.BeanUtils;
import kunlun.util.CollectionUtils;
import kunlun.util.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static kunlun.common.constant.Numbers.*;
import static kunlun.util.ArrayUtils.toList;
import static kunlun.util.CollectionUtils.toArray;

public abstract class AbstractScriptBasedJdbcTemplateInvokeHandler extends AbstractScriptBasedJdbcInvokeHandler {

    protected abstract NamedParameterJdbcTemplate getJdbcTemplate();

    protected String renderSql(String rendererName, String sql, Object data) {

        return sql;
    }

    protected Page<Map<String, Object>> byPageNumber(String sql, Map<String, Object> params) {
        // Extract params and calc.
        Dict dict = Dict.of(params);
        int pageSize = dict.getInteger("pageSize", TEN);
        int pageNum = dict.getInteger("pageNum", ONE);
        int offset = (pageNum - ONE) * pageSize;
        // Query data.
        String querySql = sql + " limit " + offset + ", " + pageSize;
        String countSql = "select count(0) from (" + sql + ") tmp";
        List<Map<String, Object>> mapList = getJdbcTemplate().queryForList(querySql, params);
        Number countNum = getJdbcTemplate().queryForObject(countSql, params, Number.class);
        Long count = countNum != null ? countNum.longValue() : null;
        // Build result.
        Integer pageCount = count != null ? (int)((count/pageSize)+(count%pageSize>ZERO?ONE:ZERO)) : null;
        return Page.of(pageNum, pageSize, pageCount, count, mapList);
    }

    protected Page<Map<String, Object>> byScrollId(String sql, Map<String, Object> params) {
        // Extract params and fill.
        Dict dict = Dict.of(params);
        int pageSize = dict.getInteger("pageSize", TEN);
        if (StringUtils.isBlank(dict.getString("scrollId"))) {
            params.put("scrollId", STR_ZERO);
        }
        // Query data.
        String querySql = sql + " limit " + pageSize;
        List<Map<String, Object>> mapList = getJdbcTemplate().queryForList(querySql, params);
        // Build result.
        Object idObj = null;
        if (CollectionUtils.isNotEmpty(mapList)) {
            idObj = CollectionUtils.getLast(mapList).get("id");
        }
        return Page.of(idObj != null ? String.valueOf(idObj) : null, pageSize, mapList);
    }

    @Override
    protected void doInvoke(InvokeContext context) {
        JdbcInvokeConfig config = (JdbcInvokeConfig) context.getConfig();
        String renderer = config.getRendererName();
        String sql = config.getSql();
        switch (config.getExecuteType()) {
            // 0 unknown, 1 nothing
            // 2 update (insert or update)
            case TWO:  {
                Map<String, Object> inputMap = BeanUtils.beanToMap(context.getConvertedInput());
                String renderSql = renderSql(renderer, sql, inputMap);
                context.setRawOutput(getJdbcTemplate().update(renderSql, inputMap));
            } break;
            // 3 batch update (insert or update)
            case THREE: {
                // Convert input.
                Object convertedInput = context.getConvertedInput();
                List<Map<String, Object>> inputMapList;
                if (convertedInput instanceof Collection) {
                    //noinspection rawtypes
                    inputMapList = BeanUtils.beanToMapInList(Array.of((Collection) convertedInput));
                }
                else if (convertedInput != null &&
                        convertedInput.getClass().isArray()) {
                    inputMapList = BeanUtils.beanToMapInList(Array.of((Object[]) convertedInput));
                }
                else { throw new UnsupportedOperationException("Unsupported input type! "); }
                // Query data.
                String renderSql = renderSql(renderer, sql, inputMapList);
                int[] ints = getJdbcTemplate().batchUpdate(renderSql, toArray(inputMapList, Map.class));
                // Result.
                context.setRawOutput(toList(ints));
            } break;
            // 4 single value query
            case FOUR: {
                Map<String, Object> inputMap = BeanUtils.beanToMap(context.getConvertedInput());
                String renderSql = renderSql(renderer, sql, inputMap);
                context.setRawOutput(getJdbcTemplate().queryForObject(renderSql, inputMap, Object.class));
            } break;
            // 5 single object query
            case FIVE: {
                Map<String, Object> inputMap = BeanUtils.beanToMap(context.getConvertedInput());
                String renderSql = renderSql(renderer, sql, inputMap);
                context.setRawOutput(getJdbcTemplate().queryForMap(renderSql, inputMap));
            } break;
            // 6 object array query
            case SIX:  {
                Map<String, Object> inputMap = BeanUtils.beanToMap(context.getConvertedInput());
                String renderSql = renderSql(renderer, sql, inputMap);
                context.setRawOutput(getJdbcTemplate().queryForList(renderSql, inputMap));
            } break;
            // 7 object array paging query (page number)
            case SEVEN: {
                Map<String, Object> inputMap = BeanUtils.beanToMap(context.getConvertedInput());
                String renderSql = renderSql(renderer, sql, inputMap);
                context.setRawOutput(byPageNumber(renderSql, inputMap));
            } break;
            // 8 object array paging query (scroll id)
            case EIGHT: {
                Map<String, Object> inputMap = BeanUtils.beanToMap(context.getConvertedInput());
                String renderSql = renderSql(renderer, sql, inputMap);
                context.setRawOutput(byScrollId(renderSql, inputMap));
            } break;
            default: { throw new UnsupportedOperationException("Unsupported execute type! "); }
        }
    }

}
