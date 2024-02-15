package artoria.generator.code;

import artoria.renderer.TextRenderer;

import java.util.*;

import static artoria.common.constant.Charsets.STR_UTF_8;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.unmodifiableList;

@Deprecated
public class JavaCodeGenConfig {
    public static final List<JavaTemplateConfig> DEFAULT_TEMPLATE_CONFIGS =
            unmodifiableList(new ArrayList<JavaTemplateConfig>(){{
        add(new JavaTemplateConfig("entity_java", ".persistence.entity", FALSE));
        add(new JavaTemplateConfig("mapper_java", ".persistence.mapper", FALSE));
        add(new JavaTemplateConfig("mapper_xml", ".persistence.mapper", FALSE));
        add(new JavaTemplateConfig("serviceImpl_java", ".service.impl", TRUE));
        add(new JavaTemplateConfig("dto_java", ".service.dto", FALSE));
        add(new JavaTemplateConfig("service_java", ".service", FALSE));
        add(new JavaTemplateConfig("controller_java", ".controller", TRUE));
        add(new JavaTemplateConfig("vo_java", ".controller.vo", FALSE));
    }});
    private String xmlBeginCoverTag = "<!-- **** (Start) This will be covered, please do not modify. **** -->";
    private String xmlEndCoverTag = "<!-- **** (End) This will be covered, please do not modify. **** -->";
    private String javaBeginCoverTag = "/* (Start) This will be covered, please do not modify. */";
    private String javaEndCoverTag = "/* (End) This will be covered, please do not modify. */";

    private String jdbcDriverClass;
    private String jdbcUrl;
    private String jdbcUser;
    private String jdbcPassword;
    private String jdbcCatalog;

    private String templateCharset = STR_UTF_8;
    private String outputCharset = STR_UTF_8;
    private String baseTemplatePath = "classpath:templates/generator/java/default";
    private String templateExtensionName = ".txt";
    private String basePackageName;
    private String xmlBaseOutputPath = "src\\main\\resources\\mapper";
    private String baseOutputPath = "src\\main\\java";
    private List<JavaTemplateConfig> templateConfigs = DEFAULT_TEMPLATE_CONFIGS;
    private Map<String, Object> customAttributes;
    private TextRenderer textRenderer;

    private Set<String> removedTableNamePrefixes = new HashSet<String>();
    private Set<String> reservedTables = new HashSet<String>();
    private Set<String> excludedTables = new HashSet<String>();

    public String getJavaBeginCoverTag() {

        return javaBeginCoverTag;
    }

    public void setJavaBeginCoverTag(String javaBeginCoverTag) {

        this.javaBeginCoverTag = javaBeginCoverTag;
    }

    public String getJavaEndCoverTag() {

        return javaEndCoverTag;
    }

    public void setJavaEndCoverTag(String javaEndCoverTag) {

        this.javaEndCoverTag = javaEndCoverTag;
    }

    public String getXmlBeginCoverTag() {

        return xmlBeginCoverTag;
    }

    public void setXmlBeginCoverTag(String xmlBeginCoverTag) {

        this.xmlBeginCoverTag = xmlBeginCoverTag;
    }

    public String getXmlEndCoverTag() {

        return xmlEndCoverTag;
    }

    public void setXmlEndCoverTag(String xmlEndCoverTag) {

        this.xmlEndCoverTag = xmlEndCoverTag;
    }


    public String getJdbcDriverClass() {

        return jdbcDriverClass;
    }

    public void setJdbcDriverClass(String jdbcDriverClass) {

        this.jdbcDriverClass = jdbcDriverClass;
    }

    public String getJdbcUrl() {

        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {

        this.jdbcUrl = jdbcUrl;
    }

    public String getJdbcUser() {

        return jdbcUser;
    }

    public void setJdbcUser(String jdbcUser) {

        this.jdbcUser = jdbcUser;
    }

    public String getJdbcPassword() {

        return jdbcPassword;
    }

    public void setJdbcPassword(String jdbcPassword) {

        this.jdbcPassword = jdbcPassword;
    }

    public String getJdbcCatalog() {

        return jdbcCatalog;
    }

    public void setJdbcCatalog(String jdbcCatalog) {

        this.jdbcCatalog = jdbcCatalog;
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

    public String getTemplateExtensionName() {

        return templateExtensionName;
    }

    public void setTemplateExtensionName(String templateExtensionName) {

        this.templateExtensionName = templateExtensionName;
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

    public List<JavaTemplateConfig> getTemplateConfigs() {

        return templateConfigs;
    }

    public void setTemplateConfigs(List<JavaTemplateConfig> templateConfigs) {

        this.templateConfigs = templateConfigs;
    }

    public Map<String, Object> getCustomAttributes() {

        return customAttributes;
    }

    public void setCustomAttributes(Map<String, Object> customAttributes) {

        this.customAttributes = customAttributes;
    }

    public TextRenderer getTextRenderer() {

        return textRenderer;
    }

    public void setTextRenderer(TextRenderer textRenderer) {

        this.textRenderer = textRenderer;
    }




    public Set<String> getRemovedTableNamePrefixes() {

        return removedTableNamePrefixes;
    }

    public void setRemovedTableNamePrefixes(Set<String> removedTableNamePrefixes) {

        this.removedTableNamePrefixes = removedTableNamePrefixes;
    }

    public Set<String> getReservedTables() {

        return reservedTables;
    }

    public void setReservedTables(Set<String> reservedTables) {

        this.reservedTables = reservedTables;
    }

    public Set<String> getExcludedTables() {

        return excludedTables;
    }

    public void setExcludedTables(Set<String> excludedTables) {

        this.excludedTables = excludedTables;
    }

}
