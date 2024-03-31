package kunlun.generator.render.support.java;

import kunlun.core.Loader;
import kunlun.core.Renderer;
import kunlun.data.Dict;
import kunlun.db.jdbc.meta.Table;
import kunlun.generator.render.RenderGenerator;
import kunlun.io.FileLoader;

import java.util.*;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static kunlun.common.constant.Charsets.STR_UTF_8;

/**
 * The java code generate configuration.
 * @author Kahle
 */
public class JavaCodeGenConfig implements RenderGenerator.Config {
    /**
     * The database column type to Java type mapping object.
     */
    private final Dict columnTypeToJavaTypeMap = new Dict();

    private Loader<Object, List<Table>> tableLoader;
    private Object tableLoaderConfig;
    private final Set<String> removedTableNamePrefixes = new LinkedHashSet<String>();
    private FileLoader fileLoader;
    private Renderer renderer;

    private String templateCharset = STR_UTF_8;
    private String outputCharset = STR_UTF_8;
    private String baseTemplatePath = "classpath:templates/generator/java/default";
    private String templateSuffix = ".txt";
    private String basePackageName;
    private String xmlBaseOutputPath = "src\\main\\resources\\mapper";
    private String baseOutputPath = "src\\main\\java";

    private String xmlStartOverrideTag = "<!-- **** (Start) This will be override, please do not modify. **** -->";
    private String xmlEndOverrideTag = "<!-- **** (End) This will be override, please do not modify. **** -->";
    private String javaStartOverrideTag = "/* (Start) This will be override, please do not modify. */";
    private String javaEndOverrideTag = "/* (End) This will be override, please do not modify. */";

    private final Map<String, Object> customAttributes = new LinkedHashMap<String, Object>();
    private final Map<String, TemplateConfig> templateConfigs = new LinkedHashMap<String, TemplateConfig>();


    public JavaCodeGenConfig() {
        // >> The template configs init.
        String javaSuffix = ".java", xmlSuffix = ".xml", name, tail, pkgName;
        // [mapperXml]
        name = "mapperXml"; tail = "Mapper"; pkgName = "mapper";
        templateConfigs.put(name, new TemplateConfig(name, tail, xmlSuffix, pkgName, FALSE));
        // [mapper]
        name = "mapper"; tail = "Mapper"; pkgName = "mapper";
        templateConfigs.put(name, new TemplateConfig(name, tail, javaSuffix, pkgName, FALSE));
        // [serviceImpl]
        name = "serviceImpl"; tail = "ServiceImpl"; pkgName = "service.impl";
        templateConfigs.put(name, new TemplateConfig(name, tail, javaSuffix, pkgName, TRUE));
        // [service]
        name = "service"; tail = "Service"; pkgName = "service";
        templateConfigs.put(name, new TemplateConfig(name, tail, javaSuffix, pkgName, FALSE));
        // [controller]
        name = "controller"; tail = "Controller"; pkgName = "controller";
        templateConfigs.put(name, new TemplateConfig(name, tail, javaSuffix, pkgName, TRUE));
        // [entity]
        name = "entity"; tail = ""; pkgName = "pojo.entity";
        templateConfigs.put(name, new TemplateConfig(name, tail, javaSuffix, pkgName, FALSE));
        // [dto]
        name = "dto"; tail = "DTO"; pkgName = "pojo.dto";
        templateConfigs.put(name, new TemplateConfig(name, tail, javaSuffix, pkgName, FALSE));
        // [vo]
        name = "vo"; tail = "VO"; pkgName = "pojo.vo";
        templateConfigs.put(name, new TemplateConfig(name, tail, javaSuffix, pkgName, FALSE));
        // >> The column type to java type map init.
        columnTypeToJavaTypeMap.put("char",     "java.lang.String");
        columnTypeToJavaTypeMap.put("varchar",  "java.lang.String");
        columnTypeToJavaTypeMap.put("text",     "java.lang.String");
        columnTypeToJavaTypeMap.put("longtext", "java.lang.String");
        columnTypeToJavaTypeMap.put("tinyint",  "java.lang.Integer");
        columnTypeToJavaTypeMap.put("smallint", "java.lang.Integer");
        columnTypeToJavaTypeMap.put("int",      "java.lang.Integer");
        columnTypeToJavaTypeMap.put("bigint",   "java.lang.Long");
        columnTypeToJavaTypeMap.put("float",    "java.lang.Float");
        columnTypeToJavaTypeMap.put("double",   "java.lang.Double");
        columnTypeToJavaTypeMap.put("bit",      "java.lang.Boolean");
        columnTypeToJavaTypeMap.put("date",     "java.util.Date");
        columnTypeToJavaTypeMap.put("time",     "java.util.Date");
        columnTypeToJavaTypeMap.put("timestamp","java.util.Date");
        columnTypeToJavaTypeMap.put("datetime", "java.util.Date");
    }

    public Dict getColumnTypeToJavaTypeMap() {

        return columnTypeToJavaTypeMap;
    }

    public Loader<Object, List<Table>> getTableLoader() {

        return tableLoader;
    }

    @SuppressWarnings("unchecked")
    public void setTableLoader(Loader<?, List<Table>> tableLoader) {

        this.tableLoader = (Loader<Object, List<Table>>) tableLoader;
    }

    public Object getTableLoaderConfig() {

        return tableLoaderConfig;
    }

    public void setTableLoaderConfig(Object tableLoaderConfig) {

        this.tableLoaderConfig = tableLoaderConfig;
    }

    public Set<String> getRemovedTableNamePrefixes() {

        return removedTableNamePrefixes;
    }

    public FileLoader getFileLoader() {

        return fileLoader;
    }

    public void setFileLoader(FileLoader fileLoader) {

        this.fileLoader = fileLoader;
    }

    public Renderer getRenderer() {

        return renderer;
    }

    public void setRenderer(Renderer renderer) {

        this.renderer = renderer;
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

    public String getBaseTemplatePath() {

        return baseTemplatePath;
    }

    public void setBaseTemplatePath(String baseTemplatePath) {

        this.baseTemplatePath = baseTemplatePath;
    }

    public String getTemplateSuffix() {

        return templateSuffix;
    }

    public void setTemplateSuffix(String templateSuffix) {

        this.templateSuffix = templateSuffix;
    }

    public String getBasePackageName() {

        return basePackageName;
    }

    public void setBasePackageName(String basePackageName) {

        this.basePackageName = basePackageName;
    }

    public String getXmlBaseOutputPath() {

        return xmlBaseOutputPath;
    }

    public void setXmlBaseOutputPath(String xmlBaseOutputPath) {

        this.xmlBaseOutputPath = xmlBaseOutputPath;
    }

    public String getBaseOutputPath() {

        return baseOutputPath;
    }

    public void setBaseOutputPath(String baseOutputPath) {

        this.baseOutputPath = baseOutputPath;
    }

    public String getXmlStartOverrideTag() {

        return xmlStartOverrideTag;
    }

    public void setXmlStartOverrideTag(String xmlStartOverrideTag) {

        this.xmlStartOverrideTag = xmlStartOverrideTag;
    }

    public String getXmlEndOverrideTag() {

        return xmlEndOverrideTag;
    }

    public void setXmlEndOverrideTag(String xmlEndOverrideTag) {

        this.xmlEndOverrideTag = xmlEndOverrideTag;
    }

    public String getJavaStartOverrideTag() {

        return javaStartOverrideTag;
    }

    public void setJavaStartOverrideTag(String javaStartOverrideTag) {

        this.javaStartOverrideTag = javaStartOverrideTag;
    }

    public String getJavaEndOverrideTag() {

        return javaEndOverrideTag;
    }

    public void setJavaEndOverrideTag(String javaEndOverrideTag) {

        this.javaEndOverrideTag = javaEndOverrideTag;
    }

    public Map<String, Object> getCustomAttributes() {

        return customAttributes;
    }

    public Map<String, TemplateConfig> getTemplateConfigs() {

        return templateConfigs;
    }

    public static class TemplateConfig {
        private final String  templateName;
        private final String  nameTail;
        private final String  fileSuffix;
        private final String  bizPackageName;
        private final Boolean skipExisted;

        public TemplateConfig(String templateName, String nameTail, String fileSuffix, String bizPackageName, Boolean skipExisted) {
            this.bizPackageName = bizPackageName;
            this.templateName = templateName;
            this.skipExisted = skipExisted;
            this.fileSuffix = fileSuffix;
            this.nameTail = nameTail;
        }

        public String getTemplateName() {

            return templateName;
        }

        public String getNameTail() {

            return nameTail;
        }

        public String getFileSuffix() {

            return fileSuffix;
        }

        public String getBizPackageName() {

            return bizPackageName;
        }

        public Boolean getSkipExisted() {

            return skipExisted;
        }
    }

}
