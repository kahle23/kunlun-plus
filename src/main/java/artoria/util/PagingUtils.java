package artoria.util;

import artoria.beans.BeanUtils;
import artoria.common.Paging;
import artoria.common.Result;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Paging tools.
 * @author Kahle
 */
public class PagingUtils {

    public static void startPage(Paging paging) {

        PagingUtils.startPage(paging, true, null);
    }

    public static void startPage(Paging paging, boolean doCount) {

        PagingUtils.startPage(paging, doCount, null);
    }

    public static void startPage(Paging paging, String orderBy) {

        PagingUtils.startPage(paging, true, orderBy);
    }

    public static void startPage(Paging paging, boolean doCount, String orderBy) {
        if (paging == null) { return; }
        Integer pageNum = paging.getPageNum();
        Integer pageSize = paging.getPageSize();
        PageHelper.startPage(pageNum, pageSize, doCount);
        if (StringUtils.isNotBlank(orderBy)) {
            PageHelper.orderBy(orderBy);
        }
    }

    public static <T> Result<List<T>> handleResult(List<T> data) {
        if (data == null) {
            return new Result<List<T>>();
        }
        if (data.isEmpty()) {
            return new Result<List<T>>(new ArrayList<T>());
        }
        if (!(data instanceof Page)) {
            return new Result<List<T>>(data);
        }
        Page page = (Page) data;
        Result<List<T>> result = new Result<List<T>>();
        result.setPageNum(page.getPageNum());
        result.setPageSize(page.getPageSize());
        result.setPageCount(page.getPages());
        result.setTotal(page.getTotal());
        result.setData(data);
        return result;
    }

    public static <F, T> Result<List<T>> handleResult(List<F> data, Class<T> clazz) {
        if (data == null) {
            return new Result<List<T>>();
        }
        if (data.isEmpty()) {
            return new Result<List<T>>(new ArrayList<T>());
        }
        List<T> list = BeanUtils.beanToBeanInList(data, clazz);
        if (!(data instanceof Page)) {
            return new Result<List<T>>(list);
        }
        Page page = (Page) data;
        Result<List<T>> result = new Result<List<T>>();
        result.setPageNum(page.getPageNum());
        result.setPageSize(page.getPageSize());
        result.setPageCount(page.getPages());
        result.setTotal(page.getTotal());
        result.setData(list);
        return result;
    }

    public static <F, T> Result<List<T>> handleResult(Result<List<F>> data, Class<T> clazz) {
        if (data == null) {
            return new Result<List<T>>();
        }
        Result<List<T>> result = new Result<List<T>>();
        result.setPageNum(data.getPageNum());
        result.setPageSize(data.getPageSize());
        result.setPageCount(data.getPageCount());
        result.setTotal(data.getTotal());
        List<F> list = data.getData();
        if (CollectionUtils.isEmpty(list)) {
            result.setData(new ArrayList<T>());
            return result;
        }
        result.setData(BeanUtils.beanToBeanInList(list, clazz));
        return result;
    }

}
