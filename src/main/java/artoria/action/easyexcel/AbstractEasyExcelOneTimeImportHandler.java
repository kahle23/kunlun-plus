package artoria.action.easyexcel;

import artoria.action.handler.AbstractImportExportActionHandler;
import artoria.action.handler.ImportHandler;
import artoria.exception.ExceptionUtils;
import artoria.util.Assert;
import artoria.util.CollectionUtils;
import artoria.util.StringUtils;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static artoria.common.constant.Numbers.*;
import static artoria.util.ObjectUtils.cast;

/**
 * The abstract easy excel one time import action handler.
 * @param <P> The import parameter type
 * @param <D> The type of data parsed
 * @see <a href="https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read">Easy Excel Read</a>
 * @author Kahle
 */
public abstract class AbstractEasyExcelOneTimeImportHandler<P, D>
        extends AbstractImportExportActionHandler<P, D> implements ImportHandler<P, D> {
    private static final Logger log = LoggerFactory.getLogger(AbstractEasyExcelOneTimeImportHandler.class);

    public AbstractEasyExcelOneTimeImportHandler(String actionName) {

        super(actionName);
    }

    protected InputStream parseFile(ExcelImportContext<P, D> context) {
        Object fileData = context.getFileData();
        Assert.notNull(fileData, "Parameter \"fileData\" must not null. ");
        try {
            if (fileData instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) fileData;
                context.setFilename(file.getOriginalFilename());
                context.setContentType(file.getContentType());
                context.setFileSize(file.getSize());
                return file.getInputStream();
            }
            else if (fileData instanceof File) {
                File file = (File) fileData;
                context.setFilename(file.getName());
                return new FileInputStream(file);
            }
            else {
                throw new IllegalArgumentException(
                        "The type of parameter \"fileData\" is not supported. "
                );
            }
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    /**
     * Configuring the import context object.
     * @param context The import context
     */
    protected abstract void configContext(ExcelImportContext<P, D> context);

    @Override
    public List<D> parseData(ImportContext<P> context, Object cursor, Object rawData) {
        // Transform context object.
        ExcelImportContext<P, D> nowContext = cast(context);
        return nowContext.getParsedData();
    }

    @Override
    public ExcelImportContext<P, D> buildContext(Object... arguments) {
        // Create the context.
        ExcelImportContext<P, D> context = createContext(ExcelImportContext.class, arguments);
        context.setBeginTime(new Date());
        context.setStatus(ONE);
        // Configuring context object.
        configContext(context);
        // Check of context.
        Assert.notBlank(context.getModule(), "Parameter \"module\" must not blank. ");
        Class<D> headerClass = context.getHeaderClass();
        Assert.notNull(headerClass, "Parameter \"headerClass\" must not null. ");
        // Default value processing.
        if (context.getAsync() == null) { context.setAsync(false); }
        if (StringUtils.isBlank(context.getResultMessage()) && context.getAsync()) {
            context.setResultMessage("Please wait for a moment while the data is being imported! ");
        }
        if (context.getReadSheets() == null) { context.setReadSheets(new ArrayList<ReadSheet>()); }
        if (context.getParsedData() == null) { context.setParsedData(new ArrayList<D>()); }
        // Parse file data.
        Object fileData = context.getFileData();
        Assert.notNull(fileData, "Parameter \"fileData\" must not null. ");
        InputStream inputStream = parseFile(context);
        ExcelReader excelReader = EasyExcel.read(inputStream).build();
        context.setExcelReader(excelReader);
        //
        if (CollectionUtils.isEmpty(context.getReadSheets())) {
            ExcelReaderSheetBuilder sheetBuilder = EasyExcel.readSheet()
                    .head(headerClass)
                    .registerReadListener(new SimpleDataReadListener<D>(context.getParsedData()));
            if (context.getHeadRowNumber() != null) {
                sheetBuilder.headRowNumber(context.getHeadRowNumber());
            }
            context.getReadSheets().add(sheetBuilder.build());
        }
        //
        excelReader.read(context.getReadSheets());
        // End.
        return context;
    }

    @Override
    public Object doExecute(AsyncSupportContext context) {
        // Transform context object.
        Assert.notNull(context, "Parameter \"context\" must not null. ");
        ExcelImportContext<P, D> nowContext = cast(context);
        try {
            // Push import task information if status is null or 1.
            // Import status: 0 unknown, 1 will import, 2 importing, 3 processing, 4 timeout, 5 failure, 6 success
            if (nowContext.getStatus() == null ||
                    nowContext.getStatus() == ONE) {
                nowContext.setStatus(TWO);
                pushTask(nowContext);
            }
            // Why not use something like paging?
            // Because "ExcelReader" already loads the entire file into memory.
            // So the current class only supports small amount of data in excel files.
            // So the data read logic is directly read all at once.
            preHandle(nowContext);
            //
            List<D> parsedData = parseData(nowContext, null, nowContext.getParsedData());
            if (nowContext.getTotalCount() == null) {
                nowContext.setTotalCount(0L);
                if (CollectionUtils.isNotEmpty(parsedData)) {
                    Integer size = parsedData.size();
                    nowContext.setTotalCount(size.longValue());
                }
            }
            // Perform data processing, such as storing to a database.
            handle(nowContext, null, parsedData);
            // Save the result of the import.
            save(nowContext);
            // Push import task information.
            // Task status is assigned by specific processing logic.
            if (nowContext.getEndTime() == null) {
                nowContext.setEndTime(new Date());
            }
            pushTask(nowContext);
            // Get the result.
            nowContext.setFinish(true);
            return getResult(context);
        }
        catch (Exception e) {
            // Push import task information.
            nowContext.setStatus(FIVE);
            nowContext.setError(e);
            pushTask(nowContext);
            throw ExceptionUtils.wrap(e);
        }
    }

    /**
     * The excel import context.
     * @param <P> The type of the import parameter
     * @param <D> The type of data parsed
     * @author Kahle
     */
    public static class ExcelImportContext<P, D> extends ImportContext<P> {
        private List<ReadSheet> readSheets;
        private ExcelReader excelReader;
        private Integer  headRowNumber;
        private Class<D> headerClass;
        private List<D> parsedData;

        public List<ReadSheet> getReadSheets() {

            return readSheets;
        }

        public void setReadSheets(List<ReadSheet> readSheets) {

            this.readSheets = readSheets;
        }

        public ExcelReader getExcelReader() {

            return excelReader;
        }

        public void setExcelReader(ExcelReader excelReader) {

            this.excelReader = excelReader;
        }

        public Integer getHeadRowNumber() {

            return headRowNumber;
        }

        public void setHeadRowNumber(Integer headRowNumber) {

            this.headRowNumber = headRowNumber;
        }

        public Class<D> getHeaderClass() {

            return headerClass;
        }

        public void setHeaderClass(Class<D> headerClass) {

            this.headerClass = headerClass;
        }

        public List<D> getParsedData() {

            return parsedData;
        }

        public void setParsedData(List<D> parsedData) {

            this.parsedData = parsedData;
        }
    }

    public static class SimpleDataReadListener<D> implements ReadListener<D> {
        private List<D> parsedData;

        public SimpleDataReadListener(List<D> parsedData) {
            Assert.notNull(parsedData, "Parameter \"parsedData\" must not null. ");
            this.parsedData = parsedData;
        }

        @Override
        public void invoke(D data, AnalysisContext context) {

            this.parsedData.add(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {

        }
    }

}
