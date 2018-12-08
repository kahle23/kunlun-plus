package artoria.generator.java;

import artoria.beans.BeanMap;
import artoria.beans.BeanUtils;
import artoria.exception.ExceptionUtils;
import artoria.generator.GenerateException;
import artoria.generator.Generator;
import artoria.io.IOUtils;
import artoria.io.StringBuilderWriter;
import artoria.template.Renderer;
import artoria.time.DateUtils;
import artoria.util.Assert;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static artoria.common.Constants.*;
import static artoria.io.IOUtils.EOF;

/**
 * Java code generator.
 * @author Kahle
 */
public class JavaCodeGenerator implements Generator {
    private static final String CLASSPATH = "classpath:";
    private static Logger log = LoggerFactory.getLogger(JavaCodeGenerator.class);
    private Map<String, Object> attributes = new HashMap<String, Object>();
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
    private List<GeneratorData> generatorDataList;

    public JavaCodeGenerator() {

        this.addAttribute("author", "artoria-extend");
    }

    public Map<String, Object> getAttributes() {

        return new HashMap<String, Object>(attributes);
    }

    public void addAttribute(String attributeName, Object attributeValue) {

        this.attributes.put(attributeName, attributeValue);
    }

    public void addAttributes(Map<String, Object> attributes) {
        Assert.notNull(attributes, "Parameter \"attributes\" must not null. ");
        this.attributes.putAll(attributes);
    }

    public void removeAttribute(String attributeName) {

        this.attributes.remove(attributeName);
    }

    public String getTemplateCharset() {

        return templateCharset;
    }

    public void setTemplateCharset(String templateCharset) {

        this.templateCharset = templateCharset;
    }

    public String getOutputCharset() {

        return outputCharset;
    }

    public void setOutputCharset(String outputCharset) {

        this.outputCharset = outputCharset;
    }

    public String getTemplateName() {

        return templateName;
    }

    public void setTemplateName(String templateName) {
        Assert.notBlank(templateName, "Parameter \"templateName\" must not blank. ");
        Assert.state(templateName.contains(UNDERLINE)
                , "Parameter \"templateName\" must contain underline. ");
        templateName = templateName.endsWith(DOT)
                ? templateName.substring(0, templateName.length() - 1)
                : templateName;
        this.templateName = templateName;
    }

    public String getBaseTemplatePath() {

        return baseTemplatePath;
    }

    public void setBaseTemplatePath(String baseTemplatePath) {

        this.baseTemplatePath = baseTemplatePath;
    }

    public String getTemplateExtensionName() {

        return templateExtensionName;
    }

    public void setTemplateExtensionName(String templateExtensionName) {
        Assert.notNull(templateExtensionName
                , "Parameter \"templateExtensionName\" must not null. ");
        templateExtensionName = templateExtensionName.startsWith(DOT)
                ? templateExtensionName
                : DOT + templateExtensionName;
        this.templateExtensionName = templateExtensionName.trim();
    }

    public String getBaseOutputPath() {

        return baseOutputPath;
    }

    public void setBaseOutputPath(String baseOutputPath) {

        this.baseOutputPath = baseOutputPath;
    }

    public Renderer getRenderer() {

        return renderer;
    }

    public void setRenderer(Renderer renderer) {

        this.renderer = renderer;
    }

    public String getBasePackageName() {

        return basePackageName;
    }

    public void setBasePackageName(String basePackageName) {
        Assert.notBlank(basePackageName
                , "Parameter \"basePackageName\" must not blank. ");
        basePackageName = basePackageName.endsWith(DOT)
                ? basePackageName.substring(0
                , basePackageName.length() - 1)
                : basePackageName;
        this.basePackageName = basePackageName.trim();
    }

    public String getBusinessPackageName() {

        return businessPackageName;
    }

    public void setBusinessPackageName(String businessPackageName) {
        Assert.notNull(businessPackageName
                , "Parameter \"businessPackageName\" must not null. ");
        businessPackageName = businessPackageName.startsWith(DOT)
                ? businessPackageName
                : DOT + businessPackageName;
        this.businessPackageName = businessPackageName.trim();
    }

    public Boolean getSkipExisted() {

        return skipExisted;
    }

    public void setSkipExisted(Boolean skipExisted) {
        Assert.notNull(skipExisted
                , "Parameter \"skipExisted\" must not null. ");
        this.skipExisted = skipExisted;
    }

    public String getBeginCoverMark() {

        return beginCoverMark;
    }

    public void setBeginCoverMark(String beginCoverMark) {

        this.beginCoverMark = beginCoverMark;
    }

    public String getEndCoverMark() {

        return endCoverMark;
    }

    public void setEndCoverMark(String endCoverMark) {

        this.endCoverMark = endCoverMark;
    }

    public String getTemplateContent() {
        if (StringUtils.isNotBlank(this.templateContent)) {
            return this.templateContent;
        }
        String templatePath = this.getTemplatePath();
        InputStream in = null;
        try {
            in = templatePath.startsWith(CLASSPATH) ?
                    IOUtils.findClasspath(templatePath.substring(CLASSPATH.length())) :
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

        this.templateContent = templateContent;
    }

    public List<GeneratorData> getGeneratorDataList() {

        return generatorDataList;
    }

    public void setGeneratorDataList(List<GeneratorData> generatorDataList) {
        this.generatorDataList = generatorDataList;
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

    protected String getFileName(GeneratorData generatorData) {
        Assert.notNull(generatorData
                , "Parameter \"generatorData\" must not null. ");
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
        BeanMap beanMap = BeanUtils.createBeanMap(generatorData);
        String className = (String) beanMap.get(begin + "ClassName");
        return className + DOT + end;
    }

    protected String readAndReplace(File existedFile, String generated) {
        log.info("The file \"{}\" already exists, it will be try replace. ", existedFile.getName());
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
                log.info("The file \"{}\" already exists and can not find " +
                        "begin cover mark, it will be skip. ", existedFile.getName());
                return null;
            }
            if (fileBegin == EOF) {
                int fileEnd = fileContent.indexOf(endCoverMark, fileIndex);
                if (fileEnd != EOF) {
                    log.info("The file \"{}\" already exists and find error end " +
                                    "cover mark after index {}, so it will be skip. "
                            , existedFile.getName(), fileIndex);
                    return null;
                }
                int generatedBegin = generated.indexOf(beginCoverMark, generatedIndex);
                if (generatedBegin != EOF) {
                    log.info("Find begin cover mark in template when generate " +
                                    "\"{}\" and it is not supposed to happen, so it will be skip. "
                            , existedFile.getName());
                    return null;
                }
                int generatedEnd = generated.indexOf(endCoverMark, generatedIndex);
                if (generatedEnd != EOF) {
                    log.info("Find end cover mark in template when generate " +
                                    "\"{}\" and it is not supposed to happen, so it will be skip. "
                            , existedFile.getName());
                    return null;
                }
                result.append(fileContent.substring(fileIndex, fileContent.length()));
                return result.toString();
            }
            int fileEnd = fileContent.indexOf(endCoverMark
                    , fileBegin + beginCoverMark.length());
            if (fileEnd == EOF) {
                log.info("The file \"{}\" already exists and can not find " +
                        "end cover mark, it will be skip. ", existedFile.getName());
                return null;
            }
            int generatedBegin = generated.indexOf(beginCoverMark, generatedIndex);
            if (generatedBegin == EOF) {
                log.info("Can not find begin cover mark in template when " +
                        "generate \"{}\", it will be skip. ", existedFile.getName());
                return null;
            }
            int generatedEnd = generated.indexOf(endCoverMark
                    , generatedBegin + beginCoverMark.length());
            if (generatedEnd == EOF) {
                log.info("Can not find end cover mark in template when generate " +
                        "\"{}\", it will be skip. ", existedFile.getName());
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

    protected void render(GeneratorData generatorData, Writer writer) throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("tableInfo", generatorData);
        data.put("generatedTime", DateUtils.format());
        data.putAll(this.getAttributes());
        String fileName = this.getFileName(generatorData);
        this.getRenderer().render(data, writer, fileName, this.getTemplateContent(), null);
    }

    @Override
    public void generate() {
        try {
            Assert.notEmpty(generatorDataList
                    , "Parameter \"generatorDataList\" must not empty. ");
            File outputDir = new File(this.getOutputPath());
            if (!outputDir.exists() && !outputDir.mkdirs()) {
                throw new IOException("Directory \"" + outputDir + "\" create failure. ");
            }
            for (GeneratorData generatorData : generatorDataList) {
                if (generatorData == null) { continue; }
                String fileName = this.getFileName(generatorData);
                File outputFile = new File(outputDir, fileName);
                if (outputFile.exists()) {
                    if (skipExisted) { return; }
                    Writer builderWriter = new StringBuilderWriter();
                    this.render(generatorData, builderWriter);
                    String generated = builderWriter.toString();
                    String outputStr = this.readAndReplace(outputFile, generated);
                    if (outputStr == null) { return; }
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
                        this.render(generatorData, writer);
                    }
                    finally {
                        IOUtils.closeQuietly(writer);
                    }
                }
            }
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e, GenerateException.class);
        }
    }

}
