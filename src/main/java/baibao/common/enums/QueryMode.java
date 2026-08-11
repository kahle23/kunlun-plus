/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.common.enums;

import baibao.common.dto.base.BaseQuery;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * 查询对象中的查询模式.
 * @author Kahle
 */
public enum QueryMode {

    /**
     * 全部的，完整的查询.
     */
    FULL,

    /**
     * 简易的查询.
     * （一般应对与比如下拉列表、数据填充等）
     */
    SIMPLE,

    /**
     * 仅填充.
     */
    ONLY_FILL,

    /**
     * 仅数据处理.
     */
    ONLY_PROCESS,

//    /**
//     * 自定义的（使用者自行约定）.
//     */
//    CUSTOM,
//
//    /**
//     * 自定义1的（使用者自行约定）.
//     */
//    CUSTOM_1,
//
//    /**
//     * 自定义2的（使用者自行约定）.
//     */
//    CUSTOM_2,
;

    /**
     * 允许填充
     */
    public static final List<QueryMode> ALLOW_FILL_LIST = unmodifiableList(asList(QueryMode.FULL, QueryMode.ONLY_FILL));
    /**
     * 允许处理
     */
    public static final List<QueryMode> ALLOW_PROCESS_LIST = unmodifiableList(asList(QueryMode.FULL, QueryMode.ONLY_PROCESS));

    public static boolean allowFill(QueryMode queryMode) {

        return queryMode != null && ALLOW_FILL_LIST.contains(queryMode);
    }

    public static boolean allowProcess(QueryMode queryMode) {

        return queryMode != null && ALLOW_PROCESS_LIST.contains(queryMode);
    }

    public static boolean allowFill(BaseQuery baseQuery) {

        return baseQuery != null && allowFill(baseQuery.getQueryMode());
    }

    public static boolean allowProcess(BaseQuery baseQuery) {

        return baseQuery != null && allowProcess(baseQuery.getQueryMode());
    }
}
