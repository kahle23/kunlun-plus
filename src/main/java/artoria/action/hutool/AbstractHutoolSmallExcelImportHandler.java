package artoria.action.hutool;

import artoria.action.handler.AbstractImportExportActionHandler;
import artoria.action.handler.ImportHandler;
import artoria.data.bean.BeanUtils;
import artoria.exception.ExceptionUtils;
import artoria.util.Assert;
import artoria.util.CollectionUtils;
import artoria.util.MapUtils;
import artoria.util.StringUtils;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static artoria.common.Constants.*;
import static artoria.util.ObjectUtils.cast;

/**
 * The abstract hutool small excel import action handler.
 * This class only supports small amount of data in excel files.
 * @param <P> The import parameter type
 * @param <D> The type of data parsed
 * @author Kahle
 */
public abstract class AbstractHutoolSmallExcelImportHandler<P, D>
        extends AbstractImportExportActionHandler<P, D> implements ImportHandler<P, D> {
    private static Logger log = LoggerFactory.getLogger(AbstractHutoolSmallExcelImportHandler.class);

    public AbstractHutoolSmallExcelImportHandler(String actionName) {

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
        // The data conversion.
        Class<D> exchangeClass = nowContext.getExchangeClass();
        return BeanUtils.beanToBeanInList((List<?>) rawData, exchangeClass);
    }

    @Override
    public ExcelImportContext<P, D> buildContext(Object... arguments) {
        // Create the context.
        ExcelImportContext<P, D> context = createContext(ExcelImportContext.class, arguments);
        context.setBeginTime(new Date());
        context.setStatus(ONE);
        // Parse file data.
        Object fileData = context.getFileData();
        Assert.notNull(fileData, "Parameter \"fileData\" must not null. ");
        InputStream inputStream = parseFile(context);
        // Configuring context object.
        configContext(context);
        // Check of context.
        Assert.notBlank(context.getModule(), "Parameter \"module\" must not blank. ");
        Class<D> exchangeClass = context.getExchangeClass();
        Assert.notNull(exchangeClass, "Parameter \"exchangeClass\" must not null. ");
        // Default value processing.
        if (context.getAsync() == null) { context.setAsync(false); }
        if (StringUtils.isBlank(context.getResultMessage()) && context.getAsync()) {
            context.setResultMessage("Please wait for a moment while the data is being imported! ");
        }
        // Read the data.
        ExcelReader excelReader = ExcelUtil.getReader(inputStream);
        if (MapUtils.isNotEmpty(context.getHeaderAliases())) {
            excelReader.setHeaderAlias(context.getHeaderAliases());
        }
        List<Map<String, Object>> mapList = excelReader.readAll();
        context.setRawData(mapList);
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
            List<?> rawData = nowContext.getRawData();
            List<D> parsedData = parseData(nowContext, null, rawData);
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
        private Map<String, String> headerAliases;
        private Class<D> exchangeClass;
        private List<?> rawData;

        public Map<String, String> getHeaderAliases() {

            return headerAliases;
        }

        public void setHeaderAliases(Map<String, String> headerAliases) {

            this.headerAliases = headerAliases;
        }

        public Class<D> getExchangeClass() {

            return exchangeClass;
        }

        public void setExchangeClass(Class<D> exchangeClass) {

            this.exchangeClass = exchangeClass;
        }

        public List<?> getRawData() {

            return rawData;
        }

        public void setRawData(List<?> rawData) {

            this.rawData = rawData;
        }

    }

}
