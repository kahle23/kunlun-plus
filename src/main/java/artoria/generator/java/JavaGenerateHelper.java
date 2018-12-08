package artoria.generator.java;

import artoria.beans.BeanMap;
import artoria.beans.BeanUtils;
import artoria.exception.ExceptionUtils;
import artoria.generator.GenerateException;
import artoria.generator.Generator;
import artoria.jdbc.ColumnMeta;
import artoria.jdbc.DatabaseClient;
import artoria.jdbc.TableMeta;
import artoria.template.Renderer;
import artoria.util.Assert;
import artoria.util.CollectionUtils;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static artoria.common.Constants.*;

/**
 * Java code generate helper.
 * @author Kahle
 */
public class JavaGenerateHelper implements Generator {
    private static final String DEFAULT_XML_BEGIN_COVER_MARK = "<!-- **** (Start) This will be covered, please do not modify. **** -->";
    private static final String DEFAULT_XML_END_COVER_MARK = "<!-- **** (End) This will be covered, please do not modify. **** -->";
    private static final String DEFAULT_JAVA_BEGIN_COVER_MARK = "/* (Start) This will be covered, please do not modify. */";
    private static final String DEFAULT_JAVA_END_COVER_MARK = "/* (End) This will be covered, please do not modify. */";
    private static final String DEFAULT_TEMPLATE_NAME_ARRAY =
            "mapper_xml,mapper_java,entity_java,serviceImpl_java,service_java,controller_java,dto_java,vo_java";
    private static Logger log = LoggerFactory.getLogger(JavaGenerateHelper.class);
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private Set<String> removedTableNamePrefixes = new HashSet<String>();
    private Set<String> reservedTables = new HashSet<String>();
    private Set<String> excludedTables = new HashSet<String>();
    private DatabaseClient databaseClient;
    private String templateCharset;
    private String outputCharset;
    private String baseTemplatePath;
    private String templateExtensionName;
    private String baseOutputPath;
    private Renderer renderer;
    private String basePackageName;
    private List<GeneratorData> generatorDataList = new ArrayList<GeneratorData>();
    private List<JavaCodeGenerator> generatorList = new ArrayList<JavaCodeGenerator>();

    public Map<String, Object> getAttributes() {

        return new HashMap<String, Object>(attributes);
    }

    public JavaGenerateHelper addAttribute(String attributeName, Object attributeValue) {
        this.attributes.put(attributeName, attributeValue);
        return this;
    }

    public JavaGenerateHelper addAttributes(Map<String, Object> attributes) {
        Assert.notNull(attributes, "Parameter \"attributes\" must not null. ");
        this.attributes.putAll(attributes);
        return this;
    }

    public JavaGenerateHelper removeAttribute(String attributeName) {
        this.attributes.remove(attributeName);
        return this;
    }

    public JavaGenerateHelper addRemovedTableNamePrefixes(String... removedTableNamePrefixes) {
        Collections.addAll(this.removedTableNamePrefixes, removedTableNamePrefixes);
        return this;
    }

    public JavaGenerateHelper addReservedTables(String... reservedTables) {
        Collections.addAll(this.reservedTables, reservedTables);
        return this;
    }

    public JavaGenerateHelper addExcludedTables(String... excludedTables) {
        Collections.addAll(this.excludedTables, excludedTables);
        return this;
    }

    public DatabaseClient getDatabaseClient() {

        return databaseClient;
    }

    public JavaGenerateHelper setDatabaseClient(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
        return this;
    }

    public String getTemplateCharset() {

        return templateCharset;
    }

    public JavaGenerateHelper setTemplateCharset(String templateCharset) {
        this.templateCharset = templateCharset;
        return this;
    }

    public String getOutputCharset() {

        return outputCharset;
    }

    public JavaGenerateHelper setOutputCharset(String outputCharset) {
        this.outputCharset = outputCharset;
        return this;
    }

    public String getBaseTemplatePath() {

        return baseTemplatePath;
    }

    public JavaGenerateHelper setBaseTemplatePath(String baseTemplatePath) {
        this.baseTemplatePath = baseTemplatePath;
        return this;
    }

    public String getTemplateExtensionName() {

        return templateExtensionName;
    }

    public JavaGenerateHelper setTemplateExtensionName(String templateExtensionName) {
        this.templateExtensionName = templateExtensionName;
        return this;
    }

    public String getBaseOutputPath() {

        return baseOutputPath;
    }

    public JavaGenerateHelper setBaseOutputPath(String baseOutputPath) {
        this.baseOutputPath = baseOutputPath;
        return this;
    }

    public Renderer getRenderer() {

        return renderer;
    }

    public JavaGenerateHelper setRenderer(Renderer renderer) {
        this.renderer = renderer;
        return this;
    }

    public String getBasePackageName() {

        return basePackageName;
    }

    public JavaGenerateHelper setBasePackageName(String basePackageName) {
        this.basePackageName = basePackageName;
        return this;
    }

    public List<GeneratorData> getGeneratorDataList() {

        return generatorDataList;
    }

    public List<JavaCodeGenerator> getGeneratorList() {

        return generatorList;
    }

    public JavaGenerateHelper addGenerator(JavaCodeGenerator generator) {
        this.generatorList.add(generator);
        return this;
    }

    public JavaGenerateHelper addGenerators(List<JavaCodeGenerator> generatorList) {
        this.generatorList.addAll(generatorList);
        return this;
    }

    public JavaGenerateHelper createGenerator() {
        this.createGenerator(DEFAULT_TEMPLATE_NAME_ARRAY);
        return this;
    }

    public JavaGenerateHelper createGenerator(String templateNameArray) {
        Assert.notBlank(templateNameArray
                , "Parameter \"templateNameArray\" must not blank. ");
        String[] split = templateNameArray.split(COMMA);
        for (String templateName : split) {
            if (StringUtils.isBlank(templateName)) { continue; }
            this.createGenerator(templateName, null);
        }
        return this;
    }

    public JavaGenerateHelper createGenerator(String templateName, String businessPackageName) {
        Assert.notBlank(templateName, "Parameter \"templateName\" must not blank. ");
        JavaCodeGenerator generator = new JavaCodeGenerator();
        boolean havePackageName = StringUtils.isNotBlank(businessPackageName);
        if (templateName.toLowerCase().contains("mapper")) {
            businessPackageName = havePackageName ? businessPackageName : ".persistence.mapper";
            generator.setSkipExisted(false);
        }
        else if (templateName.toLowerCase().contains("serviceImpl")) {
            businessPackageName = havePackageName ? businessPackageName : ".service.impl";
            generator.setSkipExisted(true);
        }
        else if (templateName.toLowerCase().contains("service")) {
            businessPackageName = havePackageName ? businessPackageName : ".service";
            generator.setSkipExisted(false);
        }
        else if (templateName.toLowerCase().contains("controller")) {
            businessPackageName = havePackageName ? businessPackageName : ".controller";
            generator.setSkipExisted(true);
        }
        else if (templateName.toLowerCase().contains("entity")) {
            businessPackageName = havePackageName ? businessPackageName : ".persistence.entity";
            generator.setSkipExisted(false);
        }
        else if (templateName.toLowerCase().contains("vo")) {
            businessPackageName = havePackageName ? businessPackageName : ".pojo.vo";
            generator.setSkipExisted(true);
        }
        else if (templateName.toLowerCase().contains("dto")) {
            businessPackageName = havePackageName ? businessPackageName : ".pojo.dto";
            generator.setSkipExisted(true);
        }
        else {
            businessPackageName = EMPTY_STRING;
            generator.setSkipExisted(true);
        }
        if (templateName.toLowerCase().contains("xml")) {
            generator.setBeginCoverMark(DEFAULT_XML_BEGIN_COVER_MARK);
            generator.setEndCoverMark(DEFAULT_XML_END_COVER_MARK);
        }
        else if (templateName.toLowerCase().contains("java")) {
            generator.setBeginCoverMark(DEFAULT_JAVA_BEGIN_COVER_MARK);
            generator.setEndCoverMark(DEFAULT_JAVA_END_COVER_MARK);
        }
        generator.setTemplateName(templateName);
        generator.setBusinessPackageName(businessPackageName);
        this.addGenerator(generator);
        return this;
    }

    private void initializeGeneratorDataList() throws Exception {
        if (CollectionUtils.isNotEmpty(generatorDataList)) { return; }
        List<TableMeta> tableMetaList = this.getDatabaseClient().getTableMetaList();
        if (CollectionUtils.isEmpty(tableMetaList)) { return; }
        //
        Map<String, JavaCodeGenerator> generatorMap = new HashMap<String, JavaCodeGenerator>();
        for (JavaCodeGenerator generator : generatorList) {
            String templateName = generator.getTemplateName();
            int index = templateName.lastIndexOf(UNDERLINE);
            if (index != -1) {
                templateName = templateName.substring(0, index);
            }
            templateName = StringUtils.uncapitalize(templateName);
            generatorMap.put(templateName, generator);
        }
        //
        for (TableMeta tableMeta : tableMetaList) {
            String tableName = tableMeta.getName();
            if (!this.reservedTables.isEmpty()
                    && !this.reservedTables.contains(tableName)) {
                continue;
            }
            if (!this.excludedTables.isEmpty()
                    && this.excludedTables.contains(tableName)) {
                continue;
            }
            // Initialize TableInfo
            GeneratorData generatorData = BeanUtils.beanToBean(tableMeta, GeneratorData.class);
            this.generatorDataList.add(generatorData);
            // Handle entity name
            String entityName = tableName;
            if (!this.removedTableNamePrefixes.isEmpty()) {
                for (String prefix : this.removedTableNamePrefixes) {
                    if (!entityName.startsWith(prefix)) { continue; }
                    entityName = entityName.substring(prefix.length());
                    break;
                }
            }
            if (StringUtils.isBlank(entityName)) {
                log.error("Table name \"{}\" handled is blank, so continue. ", tableName);
                continue;
            }
            entityName = StringUtils.underlineToCamel(entityName);
            entityName = StringUtils.capitalize(entityName);
            // Handle column information
            generatorData.setColumnDataList(new ArrayList<ColumnData>());
            generatorData.setTypeImportList(new HashSet<String>());
            List<ColumnMeta> columnMetaList = tableMeta.getColumnMetaList();
            Map<String, String> importClassMap = new HashMap<String, String>(16);
            for (ColumnMeta columnMeta : columnMetaList) {
                ColumnData columnData = BeanUtils.beanToBean(columnMeta, ColumnData.class);
                generatorData.getColumnDataList().add(columnData);
                String columnName = columnData.getName();
                String fieldName = StringUtils.underlineToCamel(columnName);
                columnData.setFieldName(StringUtils.uncapitalize(fieldName));
                String capFieldName = StringUtils.capitalize(fieldName);
                columnData.setGetterName(GET + capFieldName);
                columnData.setSetterName(SET + capFieldName);
                // Handle java type
                String columnClassName = columnData.getClassName();
                if (StringUtils.isBlank(columnClassName)) { continue; }
                String columnType = columnData.getType();
                columnClassName = TypeMapper.getType(columnClassName, columnType);
                int index = columnClassName.lastIndexOf(DOT);
                if (index == -1) {
                    columnData.setJavaType(columnClassName);
                    continue;
                }
                String shortName = columnClassName.substring(index + 1);
                String className = importClassMap.get(shortName);
                if (className != null && !className.equals(columnClassName)) {
                    columnData.setJavaType(columnClassName);
                }
                else {
                    importClassMap.put(shortName, columnClassName);
                    generatorData.getTypeImportList().add(columnClassName);
                    columnData.setJavaType(shortName);
                }
            }
            //
            for (Map.Entry<String, JavaCodeGenerator> entry : generatorMap.entrySet()) {
                JavaCodeGenerator generator = entry.getValue();
                String basePackageName = generator.getBasePackageName();
                basePackageName = StringUtils.isBlank(basePackageName)
                        ? this.getBasePackageName() : basePackageName;
                String businessPackageName = generator.getBusinessPackageName();
                String beginName = entry.getKey();
                String backName = StringUtils.capitalize(beginName);
                BeanMap beanMap = BeanUtils.createBeanMap(generatorData);
                if ("vo".equalsIgnoreCase(backName)
                        || "dto".equalsIgnoreCase(backName)) {
                    backName = backName.toUpperCase();
                }
                else if ("entity".equalsIgnoreCase(backName)) {
                    backName = EMPTY_STRING;
                }
                String className = entityName + backName;
                beanMap.put(beginName + "ClassName", className);
                beanMap.put(beginName + "ObjectName", StringUtils.uncapitalize(className));
                beanMap.put(beginName + "PackageName", basePackageName + businessPackageName);
            }
        }
    }

    private void synchronizeOtherData() {
        for (JavaCodeGenerator generator : generatorList) {
            generator.addAttributes(this.getAttributes());
            generator.setGeneratorDataList(generatorDataList);
            if (renderer != null) { generator.setRenderer(this.getRenderer()); }
            if (StringUtils.isNotBlank(outputCharset)) { generator.setOutputCharset(outputCharset); }
            if (StringUtils.isNotBlank(baseOutputPath)) { generator.setBaseOutputPath(baseOutputPath); }
            if (StringUtils.isNotBlank(templateCharset)) { generator.setTemplateCharset(templateCharset); }
            if (StringUtils.isNotBlank(basePackageName)) { generator.setBasePackageName(basePackageName); }
            if (StringUtils.isNotBlank(baseTemplatePath)) { generator.setBaseTemplatePath(baseTemplatePath); }
            if (StringUtils.isNotBlank(templateExtensionName)) { generator.setTemplateExtensionName(templateExtensionName); }
        }
    }

    @Override
    public void generate() {
        try {
            this.initializeGeneratorDataList();
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e, GenerateException.class);
        }
        this.synchronizeOtherData();
        for (JavaCodeGenerator generator : generatorList) {
            generator.generate();
        }
    }

}
