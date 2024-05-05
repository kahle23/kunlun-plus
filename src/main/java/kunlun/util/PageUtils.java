/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.util;

import com.github.pagehelper.PageHelper;
import kunlun.common.Page;
import kunlun.data.bean.BeanUtils;

import java.util.List;

/**
 * Paging tools.
 * @author Kahle
 */
public class PageUtils {
    private static Integer defaultPageSize = 15;
    private static Integer defaultPageNum = 1;

    public static Integer getDefaultPageNum() {

        return defaultPageNum;
    }

    public static void setDefaultPageNum(Integer defaultPageNum) {
        Assert.notNull(defaultPageNum, "Parameter \"defaultPageNum\" must not null. ");
        PageUtils.defaultPageNum = defaultPageNum;
    }

    public static Integer getDefaultPageSize() {

        return defaultPageSize;
    }

    public static void setDefaultPageSize(Integer defaultPageSize) {
        Assert.notNull(defaultPageSize, "Parameter \"defaultPageSize\" must not null. ");
        PageUtils.defaultPageSize = defaultPageSize;
    }


    public static void startPage(Integer pageNum, Integer pageSize) {

        PageUtils.startPage(pageNum, pageSize, true, null);
    }

    public static void startPage(Integer pageNum, Integer pageSize, boolean doCount) {

        PageUtils.startPage(pageNum, pageSize, doCount, null);
    }

    public static void startPage(Integer pageNum, Integer pageSize, String orderBy) {

        PageUtils.startPage(pageNum, pageSize, true, orderBy);
    }

    public static void startPage(Integer pageNum, Integer pageSize, boolean doCount, String orderBy) {
        if (pageSize == null) { pageSize = defaultPageSize; }
        if (pageNum == null) { pageNum = defaultPageNum; }
        PageHelper.startPage(pageNum, pageSize, doCount);
        if (StringUtils.isNotBlank(orderBy)) {
            PageHelper.orderBy(orderBy);
        }
    }

    public static <T> Page<T> handleResult(List<T> data) {
        if (data == null) { return Page.of(); }
        if (!(data instanceof com.github.pagehelper.Page)) { return Page.of(); }
        @SuppressWarnings("rawtypes")
        com.github.pagehelper.Page page = (com.github.pagehelper.Page) data;
        int pageNum = page.getPageNum();
        int pageSize = page.getPageSize();
        int pageCount = page.getPages();
        return Page.of(pageNum, pageSize, pageCount, page.getTotal(), data);
    }

    public static <F, T> Page<T> handleResult(List<F> data, Class<T> clazz) {
        if (data == null) { return Page.of(); }
        List<T> list = BeanUtils.beanToBeanInList(data, clazz);
        if (!(data instanceof com.github.pagehelper.Page)) { return Page.of(); }
        @SuppressWarnings("rawtypes")
        com.github.pagehelper.Page page = (com.github.pagehelper.Page) data;
        int pageNum = page.getPageNum();
        int pageSize = page.getPageSize();
        int pageCount = page.getPages();
        return Page.of(pageNum, pageSize, pageCount, page.getTotal(), list);
    }

    public static <F, T> Page<T> handleResult(Page<F> data, Class<T> clazz) {
        if (data == null) { return Page.of(); }
        Integer pageNum = data.getPageNum();
        Integer pageSize = data.getPageSize();
        Integer pageCount = data.getPageCount();
        List<T> tList = BeanUtils.beanToBeanInList(data.getData(), clazz);
        return Page.of(pageNum, pageSize, pageCount, data.getTotal(), tList);
    }

}
