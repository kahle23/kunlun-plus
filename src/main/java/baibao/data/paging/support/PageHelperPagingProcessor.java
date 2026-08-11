package baibao.data.paging.support;

import com.github.pagehelper.PageHelper;
import kunlun.common.Paging;
import kunlun.data.Dict;
import kunlun.data.paging.support.PagingProcessorImpl;
import kunlun.util.StrUtil;

import static kunlun.util.Assert.notNull;
import static kunlun.util.ObjUtil.ifNull;

public class PageHelperPagingProcessor extends PagingProcessorImpl {

    @Override
    public Object process(Paging paging) {
        notNull(paging);
        Integer pageNum = ifNull(paging.getPageNum(), getDefaultPageNum());
        Integer pageSize = ifNull(paging.getPageSize(), getDefaultPageSize());
        Dict pageParams = ifNull(paging.getPageParams(), Dict.of());
        Boolean doCount = pageParams.getBoolean("doCount");
        doCount = doCount != null && doCount;
        String orderBy = pageParams.getString("orderBy");
        PageHelper.startPage(pageNum, pageSize, doCount);
        if (StrUtil.isNotBlank(orderBy)) {
            PageHelper.orderBy(orderBy);
        }
        return null;
    }

}
