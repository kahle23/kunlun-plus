package artoria.util;

import artoria.beans.BeanUtils;
import artoria.common.PageResult;
import artoria.common.Paging;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Paging tools.
 * @author Kahle
 */
public class PagingUtils {

    public static void startPage(int pageNum, int pageSize) {

        PagingUtils.startPage(new Paging(pageNum, pageSize));
    }

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
        Integer pageSize = paging.getPageSize();
        Integer pageNum = paging.getPageNum();
        Assert.notNull(pageSize, "Parameter \"pageSize\" must not null. ");
        Assert.notNull(pageNum, "Parameter \"pageNum\" must not null. ");
        PageHelper.startPage(pageNum, pageSize, doCount);
        if (StringUtils.isNotBlank(orderBy)) {
            PageHelper.orderBy(orderBy);
        }
    }

    public static <T> PageResult<List<T>> handleResult(List<T> data) {
        if (data == null) {
            return new PageResult<List<T>>();
        }
        if (data.isEmpty()) {
            return new PageResult<List<T>>(new ArrayList<T>());
        }
        if (!(data instanceof Page)) {
            return new PageResult<List<T>>(data);
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
        if (data.isEmpty()) {
            return new PageResult<List<T>>(new ArrayList<T>());
        }
        List<T> list = BeanUtils.beanToBeanInList(data, clazz);
        if (!(data instanceof Page)) {
            return new PageResult<List<T>>(list);
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
        List<F> list = data.getData();
        if (CollectionUtils.isEmpty(list)) {
            result.setData(new ArrayList<T>());
            return result;
        }
        result.setData(BeanUtils.beanToBeanInList(list, clazz));
        return result;
    }

}
