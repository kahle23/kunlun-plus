package artoria.generator.code;

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
import java.util.Map;

import static artoria.common.Constants.DEFAULT_CHARSET_NAME;
import static artoria.io.IOUtils.EOF;

/**
 * Base code generator.
 * @author Kahle
 */
public abstract class BaseCodeGenerator implements Generator<TableInfo> {
    private static Logger log = LoggerFactory.getLogger(BaseCodeGenerator.class);
    public static final String CLASSPATH_PREFIX = "classpath:";
    private String templateCharset = DEFAULT_CHARSET_NAME;
    private String outputCharset = DEFAULT_CHARSET_NAME;
    private final Object LAZY_LOAD_LOCK = new Object();
    private Map<String, Object> attributes;
    private Renderer renderer;
    private String templateContent;
    private String templatePath;
    private String outputPath;

    public Map<String, Object> getAttributes() {
        if (this.attributes == null) {
            this.attributes = new HashMap<String, Object>(16);
        }
        return this.attributes;
    }

    protected void setAttributes(Map<String, Object> attributes) {
        Assert.notNull(attributes, "Parameter \"attributes\" must not null. ");
        this.attributes = attributes;
    }

    public void addAttribute(String attributeName, Object attributeValue) {

        this.getAttributes().put(attributeName, attributeValue);
    }

    public Object removeAttribute(String attributeName) {

        return this.getAttributes().remove(attributeName);
    }

    public String getTemplateCharset() {

        return this.templateCharset;
    }

    public void setTemplateCharset(String templateCharset) {

        this.templateCharset = templateCharset;
    }

    public String getOutputCharset() {

        return this.outputCharset;
    }

    public void setOutputCharset(String outputCharset) {

        this.outputCharset = outputCharset;
    }

    public String getTemplatePath() {

        return this.templatePath;
    }

    public void setTemplatePath(String templatePath) {

        this.templatePath = templatePath;
    }

    public String getOutputPath() {

        return this.outputPath;
    }

    public void setOutputPath(String outputPath) {

        this.outputPath = outputPath;
    }

    public Renderer getRenderer() {

        return this.renderer;
    }

    @Override
    public void setRenderer(Renderer stringRenderer) {

        this.renderer = stringRenderer;
    }

    protected String getTemplateContent() throws IOException {
        if (StringUtils.isNotBlank(this.templateContent)) {
            return this.templateContent;
        }
        synchronized (LAZY_LOAD_LOCK) {
            if (StringUtils.isNotBlank(this.templateContent)) {
                return this.templateContent;
            }
            InputStream in = null;
            try {
                in = this.templatePath.startsWith(CLASSPATH_PREFIX) ?
                        IOUtils.findClasspath(this.templatePath.substring(CLASSPATH_PREFIX.length())) :
                        new FileInputStream(this.templatePath);
                this.templateContent = IOUtils.toString(in, this.getTemplateCharset());
            }
            finally {
                IOUtils.closeQuietly(in);
            }
            return this.templateContent;
        }
    }

    protected String replaceExistedFile(File existedFile
            , String generatedStr
            , String startCoveredMark
            , String endCoveredMark) {
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
        if (StringUtils.isBlank(fileContent)) { return generatedStr; }
        StringBuilder result = new StringBuilder();
        int fileIndex = 0, generatedIndex = 0, count = 0;
        do {
            int fileBegin = fileContent.indexOf(startCoveredMark, fileIndex);
            if (fileBegin == EOF && count == 0) {
                log.info("The file \"{}\" already exists and can not find " +
                        "start covered mark, it will be skip. ", existedFile.getName());
                return null;
            }
            if (fileBegin == EOF) {
                int fileEnd = fileContent.indexOf(endCoveredMark, fileIndex);
                if (fileEnd != EOF) {
                    log.info("The file \"{}\" already exists and find error end " +
                            "covered mark after index {}, so it will be skip. "
                            , existedFile.getName(), fileIndex);
                    return null;
                }
                int generatedBegin = generatedStr.indexOf(startCoveredMark, generatedIndex);
                if (generatedBegin != EOF) {
                    log.info("Find start covered mark in template when generate " +
                            "\"{}\" and it is not supposed to happen, so it will be skip. "
                            , existedFile.getName());
                    return null;
                }
                int generatedEnd = generatedStr.indexOf(endCoveredMark, generatedIndex);
                if (generatedEnd != EOF) {
                    log.info("Find end covered mark in template when generate " +
                            "\"{}\" and it is not supposed to happen, so it will be skip. "
                            , existedFile.getName());
                    return null;
                }
                result.append(fileContent.substring(fileIndex, fileContent.length()));
                return result.toString();
            }
            int fileEnd = fileContent.indexOf(endCoveredMark
                    , fileBegin + startCoveredMark.length());
            if (fileEnd == EOF) {
                log.info("The file \"{}\" already exists and can not find " +
                        "end covered mark, it will be skip. ", existedFile.getName());
                return null;
            }
            int generatedBegin = generatedStr.indexOf(startCoveredMark, generatedIndex);
            if (generatedBegin == EOF) {
                log.info("Can not find start covered mark in template when " +
                        "generate \"{}\", it will be skip. ", existedFile.getName());
                return null;
            }
            int generatedEnd = generatedStr.indexOf(endCoveredMark
                    , generatedBegin + startCoveredMark.length());
            if (generatedEnd == EOF) {
                log.info("Can not find end covered mark in template when generate " +
                        "\"{}\", it will be skip. ", existedFile.getName());
                return null;
            }
            result.append(fileContent.substring(fileIndex, fileBegin));
            result.append(generatedStr.substring(generatedBegin, generatedEnd));
            result.append(endCoveredMark);
            fileIndex = fileEnd + endCoveredMark.length();
            generatedIndex = generatedEnd + endCoveredMark.length();
            count++;
        } while (true);
    }

    /**
     * Take file name by table info.
     * @param tableInfo Table info
     * @return File name
     */
    protected abstract String takeFileName(TableInfo tableInfo);

    /**
     * Handle if file already existed.
     * @param generatedStr Generated string
     * @param existedFile Existed file object
     * @return The content that will be written
     */
    protected abstract String handleExistedFile(String generatedStr, File existedFile);

    @Override
    public void generate(TableInfo tableInfo, Writer writer) throws GenerateException {
        try {
            Map<String, Object> data = new HashMap<String, Object>(16);
            data.put("tableInfo", tableInfo);
            data.put("generatedTime", DateUtils.format());
            data.putAll(this.getAttributes());
            String name = takeFileName(tableInfo);
            this.getRenderer().render(data, writer, name, this.getTemplateContent(), null);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e, GenerateException.class);
        }
    }

    @Override
    public void generate(TableInfo tableInfo) throws GenerateException {
        try {
            File outputDir = new File(this.getOutputPath());
            if (!outputDir.exists() && !outputDir.mkdirs()) {
                throw new IOException("Directory \"" + outputDir + "\" create failure. ");
            }
            File outputFile = new File(outputDir, this.takeFileName(tableInfo));
            if (outputFile.exists()) {
                Writer builderWriter = new StringBuilderWriter();
                this.generate(tableInfo, builderWriter);
                String generatedStr = builderWriter.toString();
                String outputStr = this.handleExistedFile(generatedStr, outputFile);
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
                    this.generate(tableInfo, writer);
                }
                finally {
                    IOUtils.closeQuietly(writer);
                }
            }
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e, GenerateException.class);
        }
    }

}
