/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.util;

import com.github.pagehelper.PageHelper;
import kunlun.common.Page;
import kunlun.data.bean.BeanUtil;

import java.util.List;

/**
 * The paging tools.
 * @author Kahle
 */
@Deprecated
public class PageUtils {

    public static Integer getDefaultPageNum() {

        return PageUtil1.getDefaultPageNum();
    }

    public static void setDefaultPageNum(Integer defaultPageNum) {

        PageUtil1.setDefaultPageNum(defaultPageNum);
    }

    public static Integer getDefaultPageSize() {

        return PageUtil1.getDefaultPageSize();
    }

    public static void setDefaultPageSize(Integer defaultPageSize) {

        PageUtil1.setDefaultPageSize(defaultPageSize);
    }

    // ====

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
        if (pageSize == null) { pageSize = getDefaultPageSize(); }
        if (pageNum == null) { pageNum = getDefaultPageNum(); }
        PageHelper.startPage(pageNum, pageSize, doCount);
        if (StrUtil.isNotBlank(orderBy)) {
            PageHelper.orderBy(orderBy);
        }
    }

    public static <T> Page<T> handleResult(List<T> data) {
        if (data == null) { return Page.of(); }
        if (!(data instanceof com.github.pagehelper.Page)) {
            return Page.of(data);
        }
        @SuppressWarnings("rawtypes")
        com.github.pagehelper.Page page = (com.github.pagehelper.Page) data;
        int pageNum = page.getPageNum();
        int pageSize = page.getPageSize();
        int pageCount = page.getPages();
        return Page.of(pageNum, pageSize, pageCount, page.getTotal(), data);
    }

    public static <F, T> Page<T> handleResult(List<F> data, Class<T> clazz) {
        if (data == null) { return Page.of(); }
        List<T> list = BeanUtil.beanToBeanInList(data, clazz);
        if (!(data instanceof com.github.pagehelper.Page)) {
            return Page.of(list);
        }
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
        List<T> tList = BeanUtil.beanToBeanInList(data.getData(), clazz);
        return Page.of(pageNum, pageSize, pageCount, data.getTotal(), tList);
    }

}
