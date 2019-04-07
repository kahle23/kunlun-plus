package artoria.generator;

import artoria.common.AbstractAttributable;
import artoria.exception.ExceptionUtils;
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
import java.util.concurrent.ConcurrentHashMap;

import static artoria.common.Constants.*;
import static artoria.io.IOUtils.EOF;

/**
 * Java code generate helper.
 * @author Kahle
 */
public class JavaGenerateHelper extends AbstractAttributable implements Generator<Boolean> {
    private static final String DEFAULT_XML_BEGIN_COVER_MARK
            = "<!-- **** (Start) This will be covered, please do not modify. **** -->";
    private static final String DEFAULT_XML_END_COVER_MARK
            = "<!-- **** (End) This will be covered, please do not modify. **** -->";
    private static final String DEFAULT_JAVA_BEGIN_COVER_MARK
            = "/* (Start) This will be covered, please do not modify. */";
    private static final String DEFAULT_JAVA_END_COVER_MARK
            = "/* (End) This will be covered, please do not modify. */";
    private static final String DEFAULT_TEMPLATE_NAME_ARRAY
            = "mapper_xml,mapper_java,entity_java,serviceImpl_java,service_java,controller_java,dto_java,vo_java";
    private static final Map<String, String> TYPE_MAPPINGS = new ConcurrentHashMap<String, String>();
    private static final String OBJECT_CLASS = "java.lang.Object";
    private static final String JAVA_TYPE = "javaType";
    private static Logger log = LoggerFactory.getLogger(JavaGenerateHelper.class);
    private Set<String> removedTableNamePrefixes = new HashSet<String>();
    private Set<String> reservedTables = new HashSet<String>();
    private Set<String> excludedTables = new HashSet<String>();
    private DatabaseClient databaseClient;
    private String templateCharset;
    private String outputCharset;
    private String baseTemplatePath = "classpath:generator";
    private String templateExtensionName;
    private String baseOutputPath;
    private Renderer renderer;
    private String basePackageName;
    private List<TableMeta> tableList = new ArrayList<TableMeta>();
    private List<JavaCodeGenerator> generatorList = new ArrayList<JavaCodeGenerator>();

    static {
        // java.util.Data can not be returned
        // java.sql.Date, java.sql.Time, java.sql.Timestamp all extends java.util.Data so getDate can return the three types data
        TYPE_MAPPINGS.put("java.util.Date", "java.util.Date");
        // date, year
        TYPE_MAPPINGS.put("java.sql.Date", "java.util.Date");
        // time
        TYPE_MAPPINGS.put("java.sql.Time", "java.util.Date");
        // timestamp, datetime
        TYPE_MAPPINGS.put("java.sql.Timestamp", "java.util.Date");
        // binary, varbinary, tinyblob, blob, mediumblob, longblob
        // qjd project: print_info.content varbinary(61800);
        TYPE_MAPPINGS.put("[B", "byte[]");
        // varchar, char, enum, set, text, tinytext, mediumtext, longtext
        TYPE_MAPPINGS.put("java.lang.String", "java.lang.String");
        // int, integer, tinyint, smallint, mediumint
        TYPE_MAPPINGS.put("java.lang.Integer", "java.lang.Integer");
        // bigint
        TYPE_MAPPINGS.put("java.lang.Long", "java.lang.Long");
        // real, double
        TYPE_MAPPINGS.put("java.lang.Double", "java.lang.Double");
        // float
        TYPE_MAPPINGS.put("java.lang.Float", "java.lang.Float");
        // bit
        TYPE_MAPPINGS.put("java.lang.Boolean", "java.lang.Boolean");
        // decimal, numeric
        TYPE_MAPPINGS.put("java.math.BigDecimal", "java.math.BigDecimal");
        // unsigned bigint
        TYPE_MAPPINGS.put("java.math.BigInteger", "java.math.BigInteger");
        // short
        TYPE_MAPPINGS.put("java.lang.Short", "java.lang.Short");
        // object
        TYPE_MAPPINGS.put("java.lang.Object", "java.lang.Object");
    }

    public static String getTypeMapping(String columnType, String columnClassName) {
        Assert.notBlank(columnClassName
                , "Parameter \"columnClassName\" must not blank. ");
        if (StringUtils.isNotBlank(columnType)) {
            int index = columnType.indexOf(LEFT_PARENTHESIS);
            if (index != EOF) {
                columnType = columnType.substring(0, index);
            }
            String result = TYPE_MAPPINGS.get(columnType);
            if (StringUtils.isNotBlank(result)) { return result; }
        }
        String result = TYPE_MAPPINGS.get(columnClassName);
        return result == null ? columnClassName : result;
    }

    public static String addTypeMapping(String columnClassNameOrType, String javaClassName) {
        Assert.notBlank(columnClassNameOrType
                , "Parameter \"columnClassNameOrType\" must not blank. ");
        Assert.notBlank(javaClassName
                , "Parameter \"javaClassName\" must not blank. ");
        return TYPE_MAPPINGS.put(columnClassNameOrType, javaClassName);
    }

    public static String removeTypeMapping(String columnClassNameOrType) {
        Assert.notBlank(columnClassNameOrType
                , "Parameter \"columnClassNameOrType\" must not blank. ");
        return TYPE_MAPPINGS.remove(columnClassNameOrType);
    }

    public Set<String> getRemovedTableNamePrefixes() {

        return Collections.unmodifiableSet(this.removedTableNamePrefixes);
    }

    public JavaGenerateHelper addRemovedTableNamePrefixes(String... removedTableNamePrefixes) {
        Assert.notEmpty(removedTableNamePrefixes
                , "Parameter \"removedTableNamePrefixes\" must not empty. ");
        Collections.addAll(this.removedTableNamePrefixes, removedTableNamePrefixes);
        return this;
    }

    public Set<String> getReservedTables() {

        return Collections.unmodifiableSet(this.reservedTables);
    }

    public JavaGenerateHelper addReservedTables(String... reservedTables) {
        Assert.notEmpty(reservedTables
                , "Parameter \"reservedTables\" must not empty. ");
        Collections.addAll(this.reservedTables, reservedTables);
        return this;
    }

    public Set<String> getExcludedTables() {

        return Collections.unmodifiableSet(this.excludedTables);
    }

    public JavaGenerateHelper addExcludedTables(String... excludedTables) {
        Assert.notEmpty(excludedTables
                , "Parameter \"excludedTables\" must not empty. ");
        Collections.addAll(this.excludedTables, excludedTables);
        return this;
    }

    public DatabaseClient getDatabaseClient() {

        return this.databaseClient;
    }

    public JavaGenerateHelper setDatabaseClient(DatabaseClient databaseClient) {
        Assert.notNull(databaseClient
                , "Parameter \"databaseClient\" must not null. ");
        this.databaseClient = databaseClient;
        return this;
    }

    public String getTemplateCharset() {

        return this.templateCharset;
    }

    public JavaGenerateHelper setTemplateCharset(String templateCharset) {
        Assert.notBlank(templateCharset
                , "Parameter \"templateCharset\" must not blank. ");
        this.templateCharset = templateCharset;
        return this;
    }

    public String getOutputCharset() {

        return this.outputCharset;
    }

    public JavaGenerateHelper setOutputCharset(String outputCharset) {
        Assert.notBlank(outputCharset
                , "Parameter \"outputCharset\" must not blank. ");
        this.outputCharset = outputCharset;
        return this;
    }

    public String getBaseTemplatePath() {

        return this.baseTemplatePath;
    }

    public JavaGenerateHelper setBaseTemplatePath(String baseTemplatePath) {
        Assert.notBlank(baseTemplatePath
                , "Parameter \"baseTemplatePath\" must not blank. ");
        this.baseTemplatePath = baseTemplatePath;
        return this;
    }

    public String getTemplateExtensionName() {

        return this.templateExtensionName;
    }

    public JavaGenerateHelper setTemplateExtensionName(String templateExtensionName) {
        Assert.notBlank(templateExtensionName
                , "Parameter \"templateExtensionName\" must not blank. ");
        this.templateExtensionName = templateExtensionName;
        return this;
    }

    public String getBaseOutputPath() {

        return this.baseOutputPath;
    }

    public JavaGenerateHelper setBaseOutputPath(String baseOutputPath) {
        Assert.notBlank(baseOutputPath
                , "Parameter \"baseOutputPath\" must not blank. ");
        this.baseOutputPath = baseOutputPath;
        return this;
    }

    public Renderer getRenderer() {

        return this.renderer;
    }

    public JavaGenerateHelper setRenderer(Renderer renderer) {
        Assert.notNull(renderer
                , "Parameter \"renderer\" must not null. ");
        this.renderer = renderer;
        return this;
    }

    public String getBasePackageName() {

        return this.basePackageName;
    }

    public JavaGenerateHelper setBasePackageName(String basePackageName) {
        Assert.notBlank(basePackageName
                , "Parameter \"basePackageName\" must not blank. ");
        this.basePackageName = basePackageName;
        return this;
    }

    public List<TableMeta> getTableList() {

        return this.tableList;
    }

    public List<JavaCodeGenerator> getGeneratorList() {

        return this.generatorList;
    }

    public JavaGenerateHelper addGenerator(JavaCodeGenerator generator) {
        Assert.notNull(generator
                , "Parameter \"generator\" must not null. ");
        this.generatorList.add(generator);
        return this;
    }

    public JavaGenerateHelper addGenerators(List<JavaCodeGenerator> generatorList) {
        Assert.notEmpty(generatorList
                , "Parameter \"generatorList\" must not empty. ");
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
        else if (templateName.toLowerCase().contains("serviceimpl")) {
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

    private void initializeTableList() throws Exception {
        if (CollectionUtils.isNotEmpty(tableList)) { return; }
        Assert.notNull(this.databaseClient
                , "Parameter \"databaseClient\" must not null. ");
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
        for (TableMeta table : tableMetaList) {
            String tableName = table.getName();
            if (!this.reservedTables.isEmpty()
                    && !this.reservedTables.contains(tableName)) {
                continue;
            }
            if (!this.excludedTables.isEmpty()
                    && this.excludedTables.contains(tableName)) {
                continue;
            }
            this.tableList.add(table);
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
            List<ColumnMeta> columnMetaList = table.getColumnMetaList();
            Set<String> typeImportList = new HashSet<String>();
            table.addAttribute("typeImportList", typeImportList);
            Map<String, String> importClassMap = new HashMap<String, String>();
            for (ColumnMeta columnMeta : columnMetaList) {
                String columnName = columnMeta.getName();
                String fieldName = StringUtils.underlineToCamel(columnName);
                columnMeta.addAttribute("fieldName", StringUtils.uncapitalize(fieldName));
                String capFieldName = StringUtils.capitalize(fieldName);
                columnMeta.addAttribute("getterName", GET + capFieldName);
                columnMeta.addAttribute("setterName", SET + capFieldName);
                // Handle java type
                String columnClassName = columnMeta.getClassName();
                if (StringUtils.isBlank(columnClassName)) { continue; }
                String columnType = columnMeta.getType();
                columnClassName = JavaGenerateHelper.getTypeMapping(columnType, columnClassName);
                int index = columnClassName.lastIndexOf(DOT);
                if (index == EOF) {
                    columnMeta.addAttribute(JAVA_TYPE, columnClassName);
                    continue;
                }
                String shortName = columnClassName.substring(index + 1);
                String className = importClassMap.get(shortName);
                if (className != null && !className.equals(columnClassName)) {
                    columnMeta.addAttribute(JAVA_TYPE, columnClassName);
                }
                else {
                    importClassMap.put(shortName, columnClassName);
                    typeImportList.add(columnClassName);
                    columnMeta.addAttribute(JAVA_TYPE, shortName);
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
                if ("vo".equalsIgnoreCase(backName)
                        || "dto".equalsIgnoreCase(backName)) {
                    backName = backName.toUpperCase();
                }
                else if ("entity".equalsIgnoreCase(backName)) {
                    backName = EMPTY_STRING;
                }
                String className = entityName + backName;
                table.addAttribute(beginName + "ClassName", className);
                String objectName = StringUtils.uncapitalize(className);
                table.addAttribute(beginName + "ObjectName", objectName);
                String packageName = basePackageName + businessPackageName;
                table.addAttribute(beginName + "PackageName", packageName);
            }
        }
    }

    @Override
    public Boolean generate() throws GenerateException {
        try {
            if (CollectionUtils.isEmpty(generatorList)) { return false; }
            this.initializeTableList();
            for (JavaCodeGenerator generator : generatorList) {
                generator.addAttributes(this.getAttributes());
                generator.setTableList(this.getTableList());
                if (renderer != null) {
                    generator.setRenderer(this.getRenderer());
                }
                if (StringUtils.isNotBlank(outputCharset)) {
                    generator.setOutputCharset(outputCharset);
                }
                if (StringUtils.isNotBlank(baseOutputPath)) {
                    generator.setBaseOutputPath(baseOutputPath);
                }
                if (StringUtils.isNotBlank(templateCharset)) {
                    generator.setTemplateCharset(templateCharset);
                }
                if (StringUtils.isNotBlank(basePackageName)) {
                    generator.setBasePackageName(basePackageName);
                }
                if (StringUtils.isNotBlank(baseTemplatePath)) {
                    generator.setBaseTemplatePath(baseTemplatePath);
                }
                if (StringUtils.isNotBlank(templateExtensionName)) {
                    generator.setTemplateExtensionName(templateExtensionName);
                }
                generator.generate();
            }
            return true;
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e, GenerateException.class);
        }
    }

}
