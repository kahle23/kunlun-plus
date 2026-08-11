package baibao.data.paging.support;

import kunlun.common.Page;
import kunlun.data.bean.BeanUtil;
import kunlun.data.paging.support.PageResultProcessorImpl;

import java.util.List;

public class PageHelperResultProcessor extends PageResultProcessorImpl {

    @Override
    public <T> Page<T> process(List<T> data) {
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

    @Override
    public <F, T> Page<T> process(List<F> data, Class<T> clazz) {
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

}
