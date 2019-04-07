package artoria.generator;

import artoria.common.AbstractAttributable;
import artoria.exception.ExceptionUtils;
import artoria.io.IOUtils;
import artoria.io.StringBuilderWriter;
import artoria.jdbc.ColumnMeta;
import artoria.jdbc.TableMeta;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.template.Renderer;
import artoria.time.DateUtils;
import artoria.util.Assert;
import artoria.util.ClassLoaderUtils;
import artoria.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static artoria.common.Constants.*;
import static artoria.io.IOUtils.EOF;

/**
 * Java code generator.
 * @author Kahle
 */
public class JavaCodeGenerator extends AbstractAttributable implements Generator<Boolean> {
    private static final String CLASSPATH = "classpath:";
    private static Logger log = LoggerFactory.getLogger(JavaCodeGenerator.class);
    private String templateCharset = DEFAULT_CHARSET_NAME;
    private String outputCharset = DEFAULT_CHARSET_NAME;
    private String templateName;
    private String baseTemplatePath = CLASSPATH;
    private String templateExtensionName = ".txt";
    private String baseOutputPath;
    private Renderer renderer;
    private String basePackageName;
    private String businessPackageName = EMPTY_STRING;
    private Boolean skipExisted = false;
    private String beginCoverMark;
    private String endCoverMark;
    private String templateContent;
    private List<TableMeta> tableList;

    public JavaCodeGenerator() {

        this.addAttribute("author", "artoria-extend");
    }

    public String getTemplateCharset() {

        return this.templateCharset;
    }

    public void setTemplateCharset(String templateCharset) {
        Assert.notBlank(templateCharset, "Parameter \"templateCharset\" must not blank. ");
        this.templateCharset = templateCharset;
        this.addAttribute("templateCharset", templateCharset);
    }

    public String getOutputCharset() {

        return this.outputCharset;
    }

    public void setOutputCharset(String outputCharset) {
        Assert.notBlank(outputCharset, "Parameter \"outputCharset\" must not blank. ");
        this.outputCharset = outputCharset;
        this.addAttribute("outputCharset", outputCharset);
    }

    public String getTemplateName() {

        return this.templateName;
    }

    public void setTemplateName(String templateName) {
        Assert.notBlank(templateName, "Parameter \"templateName\" must not blank. ");
        Assert.state(templateName.contains(UNDERLINE)
                , "Parameter \"templateName\" must contain underline. ");
        templateName = templateName.endsWith(DOT)
                ? templateName.substring(0, templateName.length() - 1)
                : templateName;
        this.templateName = templateName;
        this.addAttribute("templateName", templateName);
    }

    public String getBaseTemplatePath() {

        return this.baseTemplatePath;
    }

    public void setBaseTemplatePath(String baseTemplatePath) {
        Assert.notBlank(baseTemplatePath, "Parameter \"baseTemplatePath\" must not blank. ");
        this.baseTemplatePath = baseTemplatePath;
        this.addAttribute("baseTemplatePath", baseTemplatePath);
    }

    public String getTemplateExtensionName() {

        return this.templateExtensionName;
    }

    public void setTemplateExtensionName(String templateExtensionName) {
        Assert.notNull(templateExtensionName
                , "Parameter \"templateExtensionName\" must not null. ");
        templateExtensionName = templateExtensionName.startsWith(DOT)
                ? templateExtensionName
                : DOT + templateExtensionName;
        this.templateExtensionName = templateExtensionName.trim();
        this.addAttribute("templateExtensionName", templateExtensionName);
    }

    public String getBaseOutputPath() {

        return this.baseOutputPath;
    }

    public void setBaseOutputPath(String baseOutputPath) {
        Assert.notBlank(baseOutputPath, "Parameter \"baseOutputPath\" must not blank. ");
        this.baseOutputPath = baseOutputPath;
        this.addAttribute("baseOutputPath", baseOutputPath);
    }

    public Renderer getRenderer() {

        return this.renderer;
    }

    public void setRenderer(Renderer renderer) {
        Assert.notNull(renderer, "Parameter \"renderer\" must not null. ");
        this.renderer = renderer;
    }

    public String getBasePackageName() {

        return this.basePackageName;
    }

    public void setBasePackageName(String basePackageName) {
        Assert.notBlank(basePackageName
                , "Parameter \"basePackageName\" must not blank. ");
        basePackageName = basePackageName.endsWith(DOT)
                ? basePackageName.substring(0
                , basePackageName.length() - 1)
                : basePackageName;
        this.basePackageName = basePackageName.trim();
        this.addAttribute("basePackageName", basePackageName);
    }

    public String getBusinessPackageName() {

        return this.businessPackageName;
    }

    public void setBusinessPackageName(String businessPackageName) {
        Assert.notNull(businessPackageName
                , "Parameter \"businessPackageName\" must not null. ");
        businessPackageName = businessPackageName.startsWith(DOT)
                ? businessPackageName
                : DOT + businessPackageName;
        this.businessPackageName = businessPackageName.trim();
        this.addAttribute("businessPackageName", businessPackageName);
    }

    public Boolean getSkipExisted() {

        return this.skipExisted;
    }

    public void setSkipExisted(Boolean skipExisted) {
        Assert.notNull(skipExisted
                , "Parameter \"skipExisted\" must not null. ");
        this.skipExisted = skipExisted;
        this.addAttribute("skipExisted", skipExisted);
    }

    public String getBeginCoverMark() {

        return this.beginCoverMark;
    }

    public void setBeginCoverMark(String beginCoverMark) {
        Assert.notBlank(beginCoverMark
                , "Parameter \"beginCoverMark\" must not blank. ");
        this.beginCoverMark = beginCoverMark;
        this.addAttribute("beginCoverMark", beginCoverMark);
    }

    public String getEndCoverMark() {

        return this.endCoverMark;
    }

    public void setEndCoverMark(String endCoverMark) {
        Assert.notBlank(endCoverMark
                , "Parameter \"endCoverMark\" must not blank. ");
        this.endCoverMark = endCoverMark;
        this.addAttribute("endCoverMark", endCoverMark);
    }

    public String getTemplateContent() {
        if (StringUtils.isNotBlank(this.templateContent)) {
            return this.templateContent;
        }
        String templatePath = this.getTemplatePath();
        InputStream in = null;
        try {
            in = templatePath.startsWith(CLASSPATH) ?
                    ClassLoaderUtils.getResourceAsStream(
                            templatePath.substring(CLASSPATH.length()), this.getClass()
                    ) :
                    new FileInputStream(templatePath);
            this.templateContent = IOUtils.toString(in, this.getTemplateCharset());
        }
        catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
        finally {
            IOUtils.closeQuietly(in);
        }
        return this.templateContent;
    }

    public void setTemplateContent(String templateContent) {
        Assert.notBlank(templateContent
                , "Parameter \"templateContent\" must not blank. ");
        this.templateContent = templateContent;
    }

    public List<TableMeta> getTableList() {

        return this.tableList;
    }

    public void setTableList(List<TableMeta> tableList) {
        Assert.notNull(tableList
                , "Parameter \"tableList\" must not null. ");
        this.tableList = tableList;
    }

    protected String getTemplatePath() {
        String baseTemplatePath = this.getBaseTemplatePath();
        String templateName = this.getTemplateName();
        String extensionName = this.getTemplateExtensionName();
        String fileName = templateName + extensionName;
        return new File(baseTemplatePath, fileName).toString();
    }

    protected String getOutputPath() {
        String baseOutputPath = this.getBaseOutputPath();
        String basePackageName = this.getBasePackageName();
        String businessPackageName = this.getBusinessPackageName();
        String packageName = basePackageName + businessPackageName;
        packageName = StringUtils.replace(packageName, DOT, SLASH);
        return new File(baseOutputPath, packageName).toString();
    }

    protected String getFileName(TableMeta table) {
        Assert.notNull(table
                , "Parameter \"table\" must not null. ");
        String templateName = this.getTemplateName();
        int index = templateName.lastIndexOf(UNDERLINE), length;
        String begin = templateName, end = EMPTY_STRING;
        if (index != -1 && index + 1 !=
                (length = templateName.length())) {
            begin = templateName.substring(0, index);
            end = templateName.substring(index + 1, length);
        }
        if (begin.endsWith(UNDERLINE)) {
            begin = begin.substring(0
                    , begin.length() - 1);
        }
        begin = StringUtils.uncapitalize(begin);
        String className = (String) table.getAttribute(begin + "ClassName");
        return className + DOT + end;
    }

    protected String readAndReplace(File existedFile, String generated) {
        Assert.notNull(existedFile, "Parameter \"existedFile\" must not null. ");
        Assert.notBlank(generated, "Parameter \"generated\" must not blank. ");
        log.info("The file \"" + existedFile.getName() + "\" already exists, it will be try replace. ");
        InputStream in = null;
        String fileContent;
        try {
            in = new FileInputStream(existedFile);
            fileContent = IOUtils.toString(in, this.getOutputCharset());
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e, GenerateException.class);
        }
        finally {
            IOUtils.closeQuietly(in);
        }
        if (StringUtils.isBlank(fileContent)) {
            return generated;
        }
        if (StringUtils.isBlank(beginCoverMark)
                || StringUtils.isBlank(endCoverMark)) {
            return generated;
        }
        StringBuilder result = new StringBuilder();
        int fileIndex = 0, generatedIndex = 0, count = 0;
        do {
            int fileBegin = fileContent.indexOf(beginCoverMark, fileIndex);
            if (fileBegin == EOF && count == 0) {
                log.info("The file \"" + existedFile.getName() + "\" already exists " +
                        "and can not find begin cover mark, it will be skip. ");
                return null;
            }
            if (fileBegin == EOF) {
                int fileEnd = fileContent.indexOf(endCoverMark, fileIndex);
                if (fileEnd != EOF) {
                    log.info("The file \"" + existedFile.getName() +
                            "\" already exists and find error end cover mark after index "
                            + fileIndex + ", so it will be skip. ");
                    return null;
                }
                int generatedBegin = generated.indexOf(beginCoverMark, generatedIndex);
                if (generatedBegin != EOF) {
                    log.info("Find begin cover mark in template when generate \"" + existedFile.getName()
                                    + "\" and it is not supposed to happen, so it will be skip. ");
                    return null;
                }
                int generatedEnd = generated.indexOf(endCoverMark, generatedIndex);
                if (generatedEnd != EOF) {
                    log.info("Find end cover mark in template when generate \"" + existedFile.getName()
                                    + "\" and it is not supposed to happen, so it will be skip. ");
                    return null;
                }
                result.append(fileContent.substring(fileIndex, fileContent.length()));
                return result.toString();
            }
            int fileEnd = fileContent.indexOf(endCoverMark
                    , fileBegin + beginCoverMark.length());
            if (fileEnd == EOF) {
                log.info("The file \"" + existedFile.getName() + "\" already exists and " +
                        "can not find end cover mark, it will be skip. ");
                return null;
            }
            int generatedBegin = generated.indexOf(beginCoverMark, generatedIndex);
            if (generatedBegin == EOF) {
                log.info("Can not find begin cover mark in template when generate \""
                        + existedFile.getName() + "\", it will be skip. ");
                return null;
            }
            int generatedEnd = generated.indexOf(endCoverMark
                    , generatedBegin + beginCoverMark.length());
            if (generatedEnd == EOF) {
                log.info("Can not find end cover mark in template " +
                        "when generate \"" + existedFile.getName() + "\", it will be skip. ");
                return null;
            }
            result.append(fileContent.substring(fileIndex, fileBegin));
            result.append(generated.substring(generatedBegin, generatedEnd));
            result.append(endCoverMark);
            fileIndex = fileEnd + endCoverMark.length();
            generatedIndex = generatedEnd + endCoverMark.length();
            count++;
        } while (true);
    }

    protected void render(TableMeta table, Writer writer) throws Exception {
        Assert.notNull(writer, "Parameter \"writer\" must not null. ");
        Assert.notNull(table, "Parameter \"table\" must not null. ");
        log.info("Generator \"" + this.getTemplateName() +
                "\": rendering the java code corresponding to table \"" + table.getName() + "\". ");
        Map<String, Object> data = new HashMap<String, Object>(this.getAttributes());
        List<Map<String, Object>> columnList = new ArrayList<Map<String, Object>>();
        List<ColumnMeta> columnMetaList = table.getColumnMetaList();
        for (ColumnMeta columnMeta : columnMetaList) {
            columnList.add(columnMeta.getAttributes());
        }
        Map<String, Object> tableAttributes = table.getAttributes();
        tableAttributes = new HashMap<String, Object>(tableAttributes);
        tableAttributes.put("columnList", columnList);
        tableAttributes.remove("columnMetaList");
        data.put("generatedTime", DateUtils.format());
        data.put("table", tableAttributes);
        String fileName = this.getFileName(table);
        this.getRenderer().render(data, writer, fileName, this.getTemplateContent(), null);
    }

    @Override
    public Boolean generate() throws GenerateException {
        try {
            Assert.notEmpty(tableList
                    , "Parameter \"tableList\" must not empty. ");
            File outputDir = new File(this.getOutputPath());
            if (!outputDir.exists() && !outputDir.mkdirs()) {
                throw new IOException("Directory \"" + outputDir + "\" create failure. ");
            }
            for (TableMeta table : tableList) {
                if (table == null) { continue; }
                String fileName = this.getFileName(table);
                File outputFile = new File(outputDir, fileName);
                if (outputFile.exists()) {
                    if (skipExisted) { return true; }
                    Writer builderWriter = new StringBuilderWriter();
                    this.render(table, builderWriter);
                    String generated = builderWriter.toString();
                    String outputStr = this.readAndReplace(outputFile, generated);
                    if (outputStr == null) { return true; }
                    Writer writer = null;
                    try {
                        OutputStream out = new FileOutputStream(outputFile);
                        writer = new OutputStreamWriter(out, this.getOutputCharset());
                        writer.write(outputStr);
                    }
                    finally {
                        IOUtils.closeQuietly(writer);
                    }
                }
                else {
                    if (!outputFile.createNewFile()) {
                        throw new IOException("File \"" + outputDir + "\" create failure. ");
                    }
                    Writer writer = null;
                    try {
                        OutputStream out = new FileOutputStream(outputFile);
                        writer = new OutputStreamWriter(out, this.getOutputCharset());
                        this.render(table, writer);
                    }
                    finally {
                        IOUtils.closeQuietly(writer);
                    }
                }
            }
            return true;
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e, GenerateException.class);
        }
    }

}
