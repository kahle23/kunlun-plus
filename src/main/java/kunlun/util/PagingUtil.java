/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.util;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import kunlun.common.PageResult;
import kunlun.common.Paging;
import kunlun.data.bean.BeanUtil;

import java.util.List;

/**
 * Paging tools.
 * @author Kahle
 */
@Deprecated // TODO: Can delete
public class PagingUtil {

    public static void startPage(int pageNum, int pageSize) {

//        PagingUtil.startPage(new Paging(pageNum, pageSize));
    }

    public static void startPage(Paging paging) {

        PagingUtil.startPage(paging, true, null);
    }

    public static void startPage(Paging paging, boolean doCount) {

        PagingUtil.startPage(paging, doCount, null);
    }

    public static void startPage(Paging paging, String orderBy) {

        PagingUtil.startPage(paging, true, orderBy);
    }

    public static void startPage(Paging paging, boolean doCount, String orderBy) {
        if (paging == null) { return; }
        Integer pageSize = paging.getPageSize();
        Integer pageNum = paging.getPageNum();
        Assert.notNull(pageSize, "Parameter \"pageSize\" must not null. ");
        Assert.notNull(pageNum, "Parameter \"pageNum\" must not null. ");
        PageHelper.startPage(pageNum, pageSize, doCount);
        if (StrUtil.isNotBlank(orderBy)) {
            PageHelper.orderBy(orderBy);
        }
    }

    public static <T> PageResult<List<T>> handleResult(List<T> data) {
        if (data == null) {
            return new PageResult<List<T>>();
        }
        if (!(data instanceof Page)) {
            return new PageResult<List<T>>(/*data*/);
        }
        Page page = (Page) data;
        PageResult<List<T>> result = new PageResult<List<T>>();
        result.setPageNum(page.getPageNum());
        result.setPageSize(page.getPageSize());
        result.setPageCount(page.getPages());
        result.setTotal(page.getTotal());
        result.setData(data);
        return result;
    }

    public static <F, T> PageResult<List<T>> handleResult(List<F> data, Class<T> clazz) {
        if (data == null) {
            return new PageResult<List<T>>();
        }
        List<T> list = BeanUtil.beanToBeanInList(data, clazz);
        if (!(data instanceof Page)) {
            return new PageResult<List<T>>(/*list*/);
        }
        Page page = (Page) data;
        PageResult<List<T>> result = new PageResult<List<T>>();
        result.setPageNum(page.getPageNum());
        result.setPageSize(page.getPageSize());
        result.setPageCount(page.getPages());
        result.setTotal(page.getTotal());
        result.setData(list);
        return result;
    }

    public static <F, T> PageResult<List<T>> handleResult(PageResult<List<F>> data, Class<T> clazz) {
        if (data == null) {
            return new PageResult<List<T>>();
        }
        PageResult<List<T>> result = new PageResult<List<T>>();
        result.setPageNum(data.getPageNum());
        result.setPageSize(data.getPageSize());
        result.setPageCount(data.getPageCount());
        result.setTotal(data.getTotal());
        List<F> fList = data.getData();
        List<T> tList = BeanUtil.beanToBeanInList(fList, clazz);
        result.setData(tList);
        return result;
    }

}
