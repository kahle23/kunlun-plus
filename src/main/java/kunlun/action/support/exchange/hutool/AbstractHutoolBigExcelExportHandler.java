/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support.exchange.hutool;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import kunlun.action.support.exchange.AbstractImportExportActionHandler;
import kunlun.action.support.exchange.ExportHandler;
import kunlun.data.collect.PageArrayList;
import kunlun.exception.ExceptionUtils;
import kunlun.util.Assert;
import kunlun.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static kunlun.common.constant.Numbers.*;
import static kunlun.util.ObjectUtils.cast;

/**
 * The abstract hutool big excel export action handler.
 * @param <P> The export parameter type
 * @param <D> The type of data obtained
 * @author Kahle
 */
public abstract class AbstractHutoolBigExcelExportHandler<P, D>
        extends AbstractImportExportActionHandler<P, D> implements ExportHandler<P, D> {
    private static Logger log = LoggerFactory.getLogger(AbstractHutoolBigExcelExportHandler.class);

    public AbstractHutoolBigExcelExportHandler(String actionName) {

        super(actionName);
    }

    /**
     * Configuring the export context object.
     * @param context The export context
     */
    protected abstract void configContext(ExcelExportContext<P> context);

    /**
     * Write the header of excel.
     * @param context The export context
     */
    @Override
    public void preHandle(ImportExportContext<P> context) {
        ExcelExportContext<P> nowContext = cast(context);
        Map<String, String> headerAliases = nowContext.getHeaderAliases();
        if (MapUtil.isEmpty(headerAliases)) { return; }
        List<String> headers = new ArrayList<String>();
        for (Map.Entry<String, String> entry : headerAliases.entrySet()) {
            String key = String.valueOf(entry.getKey());
            String value = entry.getValue();
            value = StrUtil.isNotBlank(value) ? value : key;
            nowContext.getWorker().addHeaderAlias(key, value);
            headers.add(value);
        }
        nowContext.getWorker().setOnlyAlias(true);
        if (CollUtil.isNotEmpty(headers)) {
            nowContext.getWorker().writeHeadRow(headers);
        }
    }

    @Override
    public Object handle(ImportExportContext<P> context, Object cursor, List<D> data) {
        // Transform context object.
        ExcelExportContext<P> nowContext = cast(context);
        nowContext.getWorker().write(data);
        return null;
    }

    @Override
    public ExcelExportContext<P> buildContext(Object... arguments) {
        // Create the context.
        ExcelExportContext<P> context = createContext(ExcelExportContext.class, arguments);
        context.setBeginTime(new Date());
        context.setStatus(ONE);
        // Configuring context object.
        configContext(context);
        // Check of context.
        Assert.notBlank(context.getModule(), "Parameter \"module\" must not blank. ");
        // Default value processing.
        if (context.getAsync() == null) { context.setAsync(false); }
        if (StringUtils.isBlank(context.getResultMessage()) && context.getAsync()) {
            context.setResultMessage("Please wait for a moment while data is being exported! ");
        }
        // End.
        return context;
    }

    @Override
    public Object doExecute(AsyncSupportContext context) {
        // Transform context object.
        Assert.notNull(context, "Parameter \"context\" must not null. ");
        ExcelExportContext<P> nowContext = cast(context);
        try {
            // Set export worker.
            nowContext.setWorker(ExcelUtil.getBigWriter());
            // Push export task information if status is null or 1.
            // Export status: 0 unknown, 1 will export, 2 exporting, 3 processing, 4 timeout, 5 failure, 6 success
            if (nowContext.getStatus() == null ||
                    nowContext.getStatus() == ONE) {
                nowContext.setStatus(TWO);
                pushTask(nowContext);
            }
            // Write excel headers.
            preHandle(nowContext);
            // Query the data and write it to excel.
            long successCount = ZERO;
            for (int pageNum = ONE, pageCount = ONE; pageNum <= pageCount; pageNum++) {
                // Query the data.
                List<D> list = getData(nowContext, pageNum);
                if (CollUtil.isEmpty(list)) { break; }
                // Handle page count (only on the first page).
                // If the result is not instance of "PageArrayList", the data query is not paged.
                if (pageNum == ONE && list instanceof PageArrayList) {
                    PageArrayList pageList = (PageArrayList) list;
                    pageCount = pageList.getPageCount();
                    nowContext.setTotalCount(pageList.getTotal());
                }
                // Write data to excel.
                handle(nowContext, pageNum, list);
                successCount += list.size();
            }
            nowContext.setSuccessCount(successCount);
            // Calculate the count.
            Long totalCount = nowContext.getTotalCount();
            if (totalCount == null) {
                nowContext.setTotalCount(totalCount = successCount);
            }
            nowContext.setFailureCount(totalCount - successCount);
            // Save result.
            save(nowContext);
            // Push export task information.
            nowContext.setEndTime(new Date());
            nowContext.setStatus(SIX);
            pushTask(nowContext);
            // Get the result.
            nowContext.setFinish(true);
            return getResult(context);
        }
        catch (Exception e) {
            // Push export task information.
            nowContext.setStatus(FIVE);
            nowContext.setError(e);
            pushTask(nowContext);
            throw ExceptionUtils.wrap(e);
        }
    }

    /**
     * The excel export context.
     * @param <P> The export parameter type
     * @author Kahle
     */
    public static class ExcelExportContext<P> extends ExportContext<P> {
        /**
         * The worker of the export.
         */
        private ExcelWriter worker;
        /**
         * The header aliases.
         */
        private Map<String, String> headerAliases;

        public ExcelWriter getWorker() {

            return worker;
        }

        public void setWorker(ExcelWriter worker) {

            this.worker = worker;
        }

        public Map<String, String> getHeaderAliases() {

            return headerAliases;
        }

        public void setHeaderAliases(Map<String, String> headerAliases) {

            this.headerAliases = headerAliases;
        }

    }

}
