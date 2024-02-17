/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support.exchange.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import kunlun.action.support.exchange.AbstractImportExportActionHandler;
import kunlun.action.support.exchange.ExportHandler;
import kunlun.data.collect.PageArrayList;
import kunlun.exception.ExceptionUtils;
import kunlun.util.Assert;
import kunlun.util.CollectionUtils;
import kunlun.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static kunlun.common.constant.Numbers.*;
import static kunlun.util.ObjectUtils.cast;

/**
 * The abstract easy excel byte array based export action handler.
 * @param <P> The export parameter type
 * @param <D> The type of data obtained
 * @see <a href="https://easyexcel.opensource.alibaba.com/docs/current/quickstart/write">Easy Excel Write</a>
 * @author Kahle
 */
public abstract class AbstractEasyExcelByteArrayBasedExportHandler<P, D>
        extends AbstractImportExportActionHandler<P, D> implements ExportHandler<P, D> {
    private static final Logger log = LoggerFactory.getLogger(AbstractEasyExcelByteArrayBasedExportHandler.class);

    public AbstractEasyExcelByteArrayBasedExportHandler(String actionName) {

        super(actionName);
    }

    /**
     * Configuring the export context object.
     * @param context The export context
     */
    protected abstract void configContext(ExcelExportContext<P> context);

    @Override
    public void preHandle(ImportExportContext<P> context) {
        try {
            ExcelExportContext<P> nowContext = cast(context);
            nowContext.setOutputStream(new ByteArrayOutputStream());
            // Create excel writer builder.
            ExcelWriterBuilder builder = EasyExcel.write(nowContext.getOutputStream());
            // Set headers (not necessary).
            if (nowContext.getHeaderClass() != null) {
                builder.head(nowContext.getHeaderClass());
            }
            else if (CollectionUtils.isNotEmpty(nowContext.getHeaders())) {
                builder.head(nowContext.getHeaders());
            }
            // Set excelWriter and writeSheet.
            nowContext.setExcelWriter(builder.build());
            nowContext.setWriteSheet(EasyExcel.writerSheet().build());
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Override
    public Object handle(ImportExportContext<P> context, Object cursor, List<D> data) {
        // Transform context object.
        ExcelExportContext<P> nowContext = cast(context);
        // Write excel data.
        WriteSheet writeSheet = nowContext.getWriteSheet();
        nowContext.getExcelWriter().write(data, writeSheet);
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
        final ExcelExportContext<P> nowContext = cast(context);
        try {
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
                // EasyExcel has to write out the data even if it doesn't have it.
                // Opening this excel (generated without calling the "write" method) will result in an error.
                if (list == null) { list = Collections.emptyList(); }
                // Handle page count (only on the first page).
                // If the result is not instance of "PageArrayList", the data query is not paged.
                if (pageNum == ONE && list instanceof PageArrayList) {
                    @SuppressWarnings("rawtypes")
                    PageArrayList pageList = (PageArrayList) list;
                    pageCount = pageList.getPageCount();
                    nowContext.setTotalCount(pageList.getTotal());
                }
                // Write data to excel.
                handle(nowContext, pageNum, list);
                successCount += list.size();
            }
            nowContext.getExcelWriter().close();
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
        private ByteArrayOutputStream outputStream;
        private ExcelWriter excelWriter;
        private WriteSheet writeSheet;
        private List<List<String>> headers;
        private Class<?> headerClass;

        public ByteArrayOutputStream getOutputStream() {

            return outputStream;
        }

        public void setOutputStream(ByteArrayOutputStream outputStream) {

            this.outputStream = outputStream;
        }

        public List<List<String>> getHeaders() {

            return headers;
        }

        public void setHeaders(List<List<String>> headers) {

            this.headers = headers;
        }

        public Class<?> getHeaderClass() {

            return headerClass;
        }

        public void setHeaderClass(Class<?> headerClass) {

            this.headerClass = headerClass;
        }

        public ExcelWriter getExcelWriter() {

            return excelWriter;
        }

        public void setExcelWriter(ExcelWriter excelWriter) {

            this.excelWriter = excelWriter;
        }

        public WriteSheet getWriteSheet() {

            return writeSheet;
        }

        public void setWriteSheet(WriteSheet writeSheet) {

            this.writeSheet = writeSheet;
        }
    }

}
