package artoria.generator.code;

import artoria.beans.BeanUtils;
import artoria.exception.ExceptionUtils;
import artoria.generator.GenerateException;
import artoria.jdbc.ColumnMeta;
import artoria.jdbc.DatabaseClient;
import artoria.jdbc.TableMeta;
import artoria.template.Renderer;
import artoria.util.Assert;
import artoria.util.CollectionUtils;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

import static artoria.common.Constants.*;

/**
 * Code generate manager.
 * @author Kahle
 */
public class CodeGenerateManager {
    private static Logger log = LoggerFactory.getLogger(CodeGenerateManager.class);
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private Set<String> removedTableNamePrefixes = new HashSet<String>();
    private Set<String> reservedTables = new HashSet<String>();
    private Set<String> excludedTables = new HashSet<String>();
    private String templateCharset = DEFAULT_CHARSET_NAME;
    private String outputCharset = DEFAULT_CHARSET_NAME;
    private String baseTemplatePath = BaseCodeGenerator.CLASSPATH_PREFIX;
    private String templateSuffix = ".txt";
    private String basePackageName;
    private String baseOutputPath;
    private DatabaseClient databaseClient;
    private Renderer renderer;
    private boolean initialized = false;
    private List<TableInfo> tableInfoList = new ArrayList<TableInfo>();

    {
        this.addAttribute("author", "artoria-extend");
    }

    private void assertInitialized() {
        if (this.initialized) {
            throw new UnsupportedOperationException("It's already initialized. ");
        }
    }

    private void assertUninitialized() {
        if (!this.initialized) {
            throw new UnsupportedOperationException("It's not initialized. ");
        }
    }

    private void assertAttributes() {
        Assert.notNull(this.databaseClient, "Attribute \"databaseClient\" must not null. ");
        Assert.notNull(this.renderer, "Attribute \"renderer\" must not null. ");
        Assert.notBlank(this.templateCharset, "Attribute \"templateCharset\" must not blank. ");
        Assert.notBlank(this.outputCharset, "Attribute \"outputCharset\" must not blank. ");
        Assert.notBlank(this.baseTemplatePath, "Attribute \"baseTemplatePath\" must not blank. ");
        Assert.notBlank(this.templateSuffix, "Attribute \"templateSuffix\" must not blank. ");
        Assert.notBlank(this.basePackageName, "Attribute \"basePackageName\" must not blank. ");
        Assert.notBlank(this.baseOutputPath, "Attribute \"baseOutputPath\" must not blank. ");
    }

    private void initializePackageName() {
        this.controllerPackageName = StringUtils.isBlank(this.controllerPackageName)
                ? this.basePackageName + ".controller" : this.controllerPackageName;
        this.voPackageName = StringUtils.isBlank(this.voPackageName)
                ? this.basePackageName + ".controller.vo" : this.voPackageName;
        this.servicePackageName = StringUtils.isBlank(this.servicePackageName)
                ? this.basePackageName + ".service" : this.servicePackageName;
        this.serviceImplPackageName = StringUtils.isBlank(this.serviceImplPackageName)
                ? this.basePackageName + ".service.impl" : this.serviceImplPackageName;
        this.dtoPackageName = StringUtils.isBlank(this.dtoPackageName)
                ? this.basePackageName + ".service.dto" : this.dtoPackageName;
        this.mapperPackageName = StringUtils.isBlank(this.mapperPackageName)
                ? this.basePackageName + ".persistence.mapper" : this.mapperPackageName;
        this.entityPackageName = StringUtils.isBlank(this.entityPackageName)
                ? this.basePackageName + ".persistence.entity" : this.entityPackageName;
    }

    private void initializeTemplatePath() {
        this.controllerTemplatePath = StringUtils.isBlank(this.controllerTemplatePath)
                ? "classpath:code/controller.txt" : this.controllerTemplatePath;
        this.voTemplatePath = StringUtils.isBlank(this.voTemplatePath)
                ? "classpath:code/vo.txt" : this.voTemplatePath;
        this.serviceTemplatePath = StringUtils.isBlank(this.serviceTemplatePath)
                ? "classpath:code/service.txt" : this.serviceTemplatePath;
        this.serviceImplTemplatePath = StringUtils.isBlank(this.serviceImplTemplatePath)
                ? "classpath:code/serviceImpl.txt" : this.serviceImplTemplatePath;
        this.dtoTemplatePath = StringUtils.isBlank(this.dtoTemplatePath)
                ? "classpath:code/dto.txt" : this.dtoTemplatePath;
        this.mapperTemplatePath = StringUtils.isBlank(this.mapperTemplatePath)
                ? "classpath:code/mapper.txt" : this.mapperTemplatePath;
        this.mapperXmlTemplatePath = StringUtils.isBlank(this.mapperXmlTemplatePath)
                ? "classpath:code/mapperXml.txt" : this.mapperXmlTemplatePath;
        this.entityTemplatePath = StringUtils.isBlank(this.entityTemplatePath)
                ? "classpath:code/entity.txt" : this.entityTemplatePath;
    }

    private void initializeOutputPath() {
        this.controllerOutputPath = StringUtils.isBlank(this.controllerOutputPath)
                ? new File(this.baseOutputPath
                , StringUtils.replace(this.controllerPackageName, DOT, SLASH)).toString() : this.controllerOutputPath;
        this.voOutputPath = StringUtils.isBlank(this.voOutputPath)
                ? new File(this.baseOutputPath
                , StringUtils.replace(this.voPackageName, DOT, SLASH)).toString() : this.voOutputPath;
        this.serviceOutputPath = StringUtils.isBlank(this.serviceOutputPath)
                ? new File(this.baseOutputPath
                , StringUtils.replace(this.servicePackageName, DOT, SLASH)).toString() : this.serviceOutputPath;
        this.serviceImplOutputPath = StringUtils.isBlank(this.serviceImplOutputPath)
                ? new File(this.baseOutputPath
                , StringUtils.replace(this.serviceImplPackageName, DOT, SLASH)).toString() : this.serviceImplOutputPath;
        this.dtoOutputPath = StringUtils.isBlank(this.dtoOutputPath)
                ? new File(this.baseOutputPath
                , StringUtils.replace(this.dtoPackageName, DOT, SLASH)).toString() : this.dtoOutputPath;
        this.mapperOutputPath = StringUtils.isBlank(this.mapperOutputPath)
                ? new File(this.baseOutputPath
                , StringUtils.replace(this.mapperPackageName, DOT, SLASH)).toString() : this.mapperOutputPath;
        this.mapperXmlOutputPath = StringUtils.isBlank(this.mapperXmlOutputPath)
                ? new File(this.baseOutputPath
                , StringUtils.replace(this.mapperPackageName, DOT, SLASH)).toString() : this.mapperXmlOutputPath;
        this.entityOutputPath = StringUtils.isBlank(this.entityOutputPath)
                ? new File(this.baseOutputPath
                , StringUtils.replace(this.entityPackageName, DOT, SLASH)).toString() : this.entityOutputPath;
    }

    private void initializeVoGenerator() {
        if (this.voGenerator == null) {
            this.voGenerator = new VoGenerator();
            this.voGenerator.setTemplateCharset(this.getTemplateCharset());
            this.voGenerator.setOutputCharset(this.getOutputCharset());
            this.voGenerator.setTemplatePath(this.getVoTemplatePath());
            this.voGenerator.setOutputPath(this.getVoOutputPath());
            this.voGenerator.setRenderer(this.getRenderer());
            this.voGenerator.setAttributes(this.getAttributes());
        }
    }

    private void initializeControllerGenerator() {
        if (this.controllerGenerator == null) {
            this.controllerGenerator = new ControllerGenerator();
            this.controllerGenerator.setTemplateCharset(this.getTemplateCharset());
            this.controllerGenerator.setOutputCharset(this.getOutputCharset());
            this.controllerGenerator.setTemplatePath(this.getControllerTemplatePath());
            this.controllerGenerator.setOutputPath(this.getControllerOutputPath());
            this.controllerGenerator.setRenderer(this.getRenderer());
            this.controllerGenerator.setAttributes(this.getAttributes());
        }
    }

    private void initializeDtoGenerator() {
        if (this.dtoGenerator == null) {
            this.dtoGenerator = new DtoGenerator();
            this.dtoGenerator.setTemplateCharset(this.getTemplateCharset());
            this.dtoGenerator.setOutputCharset(this.getOutputCharset());
            this.dtoGenerator.setTemplatePath(this.getDtoTemplatePath());
            this.dtoGenerator.setOutputPath(this.getDtoOutputPath());
            this.dtoGenerator.setRenderer(this.getRenderer());
            this.dtoGenerator.setAttributes(this.getAttributes());
        }
    }

    private void initializeServiceGenerator() {
        if (this.serviceGenerator == null) {
            this.serviceGenerator = new ServiceGenerator();
            this.serviceGenerator.setTemplateCharset(this.getTemplateCharset());
            this.serviceGenerator.setOutputCharset(this.getOutputCharset());
            this.serviceGenerator.setTemplatePath(this.getServiceTemplatePath());
            this.serviceGenerator.setOutputPath(this.getServiceOutputPath());
            this.serviceGenerator.setRenderer(this.getRenderer());
            this.serviceGenerator.setAttributes(this.getAttributes());
        }
    }

    private void initializeServiceImplGenerator() {
        if (this.serviceImplGenerator == null) {
            this.serviceImplGenerator = new ServiceImplGenerator();
            this.serviceImplGenerator.setTemplateCharset(this.getTemplateCharset());
            this.serviceImplGenerator.setOutputCharset(this.getOutputCharset());
            this.serviceImplGenerator.setTemplatePath(this.getServiceImplTemplatePath());
            this.serviceImplGenerator.setOutputPath(this.getServiceImplOutputPath());
            this.serviceImplGenerator.setRenderer(this.getRenderer());
            this.serviceImplGenerator.setAttributes(this.getAttributes());
        }
    }

    private void initializeEntityGenerator() {
        if (this.entityGenerator == null) {
            this.entityGenerator = new EntityGenerator();
            this.entityGenerator.setTemplateCharset(this.getTemplateCharset());
            this.entityGenerator.setOutputCharset(this.getOutputCharset());
            this.entityGenerator.setTemplatePath(this.getEntityTemplatePath());
            this.entityGenerator.setOutputPath(this.getEntityOutputPath());
            this.entityGenerator.setRenderer(this.getRenderer());
            this.entityGenerator.setAttributes(this.getAttributes());
        }
    }

    private void initializeMapperGenerator() {
        if (this.mapperGenerator == null) {
            this.mapperGenerator = new MapperGenerator();
            this.mapperGenerator.setTemplateCharset(this.getTemplateCharset());
            this.mapperGenerator.setOutputCharset(this.getOutputCharset());
            this.mapperGenerator.setTemplatePath(this.getMapperTemplatePath());
            this.mapperGenerator.setOutputPath(this.getMapperOutputPath());
            this.mapperGenerator.setRenderer(this.getRenderer());
            this.mapperGenerator.setAttributes(this.getAttributes());
        }
    }

    private void initializeMapperXmlGenerator() {
        if (this.mapperXmlGenerator == null) {
            this.mapperXmlGenerator = new MapperXmlGenerator();
            this.mapperXmlGenerator.setTemplateCharset(this.getTemplateCharset());
            this.mapperXmlGenerator.setOutputCharset(this.getOutputCharset());
            this.mapperXmlGenerator.setTemplatePath(this.getMapperXmlTemplatePath());
            this.mapperXmlGenerator.setOutputPath(this.getMapperXmlOutputPath());
            this.mapperXmlGenerator.setRenderer(this.getRenderer());
            this.mapperXmlGenerator.setAttributes(this.getAttributes());
        }
    }

    private void initializeTableInfoList() {
        try {
            List<TableMeta> tableMetaList = this.getDatabaseClient().getTableMetaList();
            if (CollectionUtils.isEmpty(tableMetaList)) {
                return;
            }
            for (TableMeta tableMeta : tableMetaList) {
                String tableName = tableMeta.getName();
                if (!this.reservedTables.isEmpty() && !this.reservedTables.contains(tableName)) {
                    continue;
                }
                if (!this.excludedTables.isEmpty() && this.excludedTables.contains(tableName)) {
                    continue;
                }
                // Initialize TableInfo
                TableInfo tableInfo = BeanUtils.beanToBean(tableMeta, TableInfo.class);
                this.tableInfoList.add(tableInfo);
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
                tableInfo.setEntityClassName(entityName);
                tableInfo.setControllerClassName(entityName + "Controller");
                tableInfo.setVoClassName(entityName + "VO");
                tableInfo.setServiceClassName(entityName + "Service");
                tableInfo.setServiceImplClassName(entityName + "ServiceImpl");
                tableInfo.setDtoClassName(entityName + "DTO");
                tableInfo.setMapperClassName(entityName + "Mapper");
                // Handle column information
                tableInfo.setColumnInfoList(new ArrayList<ColumnInfo>());
                tableInfo.setEntityImports(new HashSet<String>());
                List<ColumnMeta> columnMetaList = tableMeta.getColumnMetaList();
                Map<String, String> importClassMap = new HashMap<String, String>(16);
                for (ColumnMeta columnMeta : columnMetaList) {
                    ColumnInfo columnInfo = BeanUtils.beanToBean(columnMeta, ColumnInfo.class);
                    tableInfo.getColumnInfoList().add(columnInfo);
                    String columnName = columnInfo.getName();
                    String fieldName = StringUtils.underlineToCamel(columnName);
                    columnInfo.setFieldName(StringUtils.uncapitalize(fieldName));
                    String capFieldName = StringUtils.capitalize(fieldName);
                    columnInfo.setGetterName(GET + capFieldName);
                    columnInfo.setSetterName(SET + capFieldName);
                    // Handle java type
                    String columnClassName = columnInfo.getClassName();
                    if (StringUtils.isBlank(columnClassName)) { continue; }
                    String columnType = columnInfo.getType();
                    columnClassName = TypeMapper.getType(columnClassName, columnType);
                    int index = columnClassName.lastIndexOf(DOT);
                    if (index == -1) {
                        columnInfo.setJavaType(columnClassName);
                        continue;
                    }
                    String shortName = columnClassName.substring(index + 1);
                    String className = importClassMap.get(shortName);
                    if (className != null && !className.equals(columnClassName)) {
                        columnInfo.setJavaType(columnClassName);
                    }
                    else {
                        importClassMap.put(shortName, columnClassName);
                        tableInfo.getEntityImports().add(columnClassName);
                        columnInfo.setJavaType(shortName);
                    }
                }
                //
                String voClassName = tableInfo.getVoClassName();
                tableInfo.setVoObjectName(StringUtils.uncapitalize(voClassName));
                String serviceClassName = tableInfo.getServiceClassName();
                tableInfo.setServiceObjectName(StringUtils.uncapitalize(serviceClassName));
                String dtoClassName = tableInfo.getDtoClassName();
                tableInfo.setDtoObjectName(StringUtils.uncapitalize(dtoClassName));
                String mapperClassName = tableInfo.getMapperClassName();
                tableInfo.setMapperObjectName(StringUtils.uncapitalize(mapperClassName));
                String entityClassName = tableInfo.getEntityClassName();
                tableInfo.setEntityObjectName(StringUtils.uncapitalize(entityClassName));
                //
                tableInfo.setControllerPackageName(this.getControllerPackageName());
                tableInfo.setVoPackageName(this.getVoPackageName());
                tableInfo.setServicePackageName(this.getServicePackageName());
                tableInfo.setServiceImplPackageName(this.getServiceImplPackageName());
                tableInfo.setDtoPackageName(this.getDtoPackageName());
                tableInfo.setMapperPackageName(this.getMapperPackageName());
                tableInfo.setEntityPackageName(this.getEntityPackageName());
            }
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e, GenerateException.class);
        }
    }

    private void printInformation() {
        int maxShowTableNum = 5;
        StringBuilder information = new StringBuilder(NEWLINE);
        information.append(">> Initialize success! ").append(NEWLINE);
        information.append(">> Base package name: ").append(this.getBasePackageName()).append(NEWLINE);
        information.append(">> Base output path: ").append(this.getBaseOutputPath()).append(NEWLINE);
        int tableCount = this.tableInfoList.size();
        information.append(">> Table count: ").append(tableCount).append(NEWLINE);
        if (tableCount < maxShowTableNum && tableCount > 0) {
            information.append(">> Table name: ");
            for (TableInfo tableInfo : this.tableInfoList) {
                information.append(tableInfo.getName())
                        .append(COMMA)
                        .append(BLANK_SPACE);
            }
            information.deleteCharAt(information.length() - 2).append(NEWLINE);
        }
        log.info(information.toString());
    }

    public Map<String, Object> getAttributes() {

        return this.attributes;
    }

    public void addAttribute(String attributeName, Object attributeValue) {
        this.assertInitialized();
        this.getAttributes().put(attributeName, attributeValue);
    }

    public Object removeAttribute(String attributeName) {
        this.assertInitialized();
        return this.getAttributes().remove(attributeName);
    }

    public void addRemovedTableNamePrefixes(String... removedTableNamePrefixes) {
        this.assertInitialized();
        Collections.addAll(this.removedTableNamePrefixes, removedTableNamePrefixes);
    }

    public void addReservedTables(String... reservedTables) {
        this.assertInitialized();
        Collections.addAll(this.reservedTables, reservedTables);
    }

    public void addExcludedTables(String... excludedTables) {
        this.assertInitialized();
        Collections.addAll(this.excludedTables, excludedTables);
    }

    public String getTemplateCharset() {

        return this.templateCharset;
    }

    public void setTemplateCharset(String templateCharset) {
        this.assertInitialized();
        this.templateCharset = templateCharset;
    }

    public String getOutputCharset() {

        return this.outputCharset;
    }

    public void setOutputCharset(String outputCharset) {
        this.assertInitialized();
        this.outputCharset = outputCharset;
    }

    public String getBaseTemplatePath() {

        return this.baseTemplatePath;
    }

    public void setBaseTemplatePath(String baseTemplatePath) {
        this.assertInitialized();
        this.baseTemplatePath = baseTemplatePath;
    }

    public String getTemplateSuffix() {

        return this.templateSuffix;
    }

    public void setTemplateSuffix(String templateSuffix) {
        this.assertInitialized();
        this.templateSuffix = templateSuffix;
    }

    public String getBasePackageName() {

        return this.basePackageName;
    }

    public void setBasePackageName(String basePackageName) {
        this.assertInitialized();
        this.basePackageName = basePackageName;
    }

    public String getBaseOutputPath() {

        return this.baseOutputPath;
    }

    public void setBaseOutputPath(String baseOutputPath) {
        this.assertInitialized();
        this.baseOutputPath = baseOutputPath;
    }

    public DatabaseClient getDatabaseClient() {

        return databaseClient;
    }

    public void setDatabaseClient(DatabaseClient databaseClient) {
        this.assertInitialized();
        this.databaseClient = databaseClient;
    }

    public Renderer getRenderer() {

        return this.renderer;
    }

    public void setRenderer(Renderer renderer) {
        this.assertInitialized();
        this.renderer = renderer;
    }

    public List<TableInfo> getTableInfoList() {

        return this.tableInfoList;
    }

    public void initialize() {
        this.assertInitialized();
        this.initialized = true;
        this.assertAttributes();
        this.initializePackageName();
        this.initializeTemplatePath();
        this.initializeOutputPath();
        this.initializeControllerGenerator();
        this.initializeVoGenerator();
        this.initializeServiceGenerator();
        this.initializeServiceImplGenerator();
        this.initializeDtoGenerator();
        this.initializeMapperGenerator();
        this.initializeMapperXmlGenerator();
        this.initializeEntityGenerator();
        this.initializeTableInfoList();
        this.printInformation();
    }

    public void generateController() {
        this.assertUninitialized();
        if (CollectionUtils.isEmpty(this.tableInfoList)) {
            return;
        }
        for (TableInfo tableInfo : this.tableInfoList) {
            this.getControllerGenerator().generate(tableInfo);
        }
    }

    public void generateVo() {
        this.assertUninitialized();
        if (CollectionUtils.isEmpty(this.tableInfoList)) {
            return;
        }
        for (TableInfo tableInfo : this.tableInfoList) {
            this.getVoGenerator().generate(tableInfo);
        }
    }

    public void generateService() {
        this.assertUninitialized();
        if (CollectionUtils.isEmpty(this.tableInfoList)) {
            return;
        }
        for (TableInfo tableInfo : this.tableInfoList) {
            this.getServiceGenerator().generate(tableInfo);
            this.getServiceImplGenerator().generate(tableInfo);
        }
    }

    public void generateDto() {
        this.assertUninitialized();
        if (CollectionUtils.isEmpty(this.tableInfoList)) {
            return;
        }
        for (TableInfo tableInfo : this.tableInfoList) {
            this.getDtoGenerator().generate(tableInfo);
        }
    }

    public void generateMapper() {
        this.assertUninitialized();
        if (CollectionUtils.isEmpty(this.tableInfoList)) {
            return;
        }
        for (TableInfo tableInfo : this.tableInfoList) {
            this.getMapperGenerator().generate(tableInfo);
            this.getMapperXmlGenerator().generate(tableInfo);
        }
    }

    public void generateEntity() {
        this.assertUninitialized();
        if (CollectionUtils.isEmpty(this.tableInfoList)) {
            return;
        }
        for (TableInfo tableInfo : this.tableInfoList) {
            this.getEntityGenerator().generate(tableInfo);
        }
    }

    public void generate() {
        this.assertUninitialized();
        if (CollectionUtils.isEmpty(this.tableInfoList)) {
            return;
        }
        for (TableInfo tableInfo : this.tableInfoList) {
            this.getEntityGenerator().generate(tableInfo);
            this.getDtoGenerator().generate(tableInfo);
            this.getVoGenerator().generate(tableInfo);
            this.getMapperGenerator().generate(tableInfo);
            this.getMapperXmlGenerator().generate(tableInfo);
            this.getServiceGenerator().generate(tableInfo);
            this.getServiceImplGenerator().generate(tableInfo);
            this.getControllerGenerator().generate(tableInfo);
        }
    }




    private String controllerPackageName;
    private String voPackageName;
    private String servicePackageName;
    private String serviceImplPackageName;
    private String dtoPackageName;
    private String mapperPackageName;
    private String entityPackageName;

    private String controllerTemplatePath;
    private String voTemplatePath;
    private String serviceTemplatePath;
    private String serviceImplTemplatePath;
    private String dtoTemplatePath;
    private String mapperTemplatePath;
    private String mapperXmlTemplatePath;
    private String entityTemplatePath;

    private String controllerOutputPath;
    private String voOutputPath;
    private String serviceOutputPath;
    private String serviceImplOutputPath;
    private String dtoOutputPath;
    private String mapperOutputPath;
    private String mapperXmlOutputPath;
    private String entityOutputPath;

    private BaseCodeGenerator controllerGenerator;
    private BaseCodeGenerator voGenerator;
    private BaseCodeGenerator serviceGenerator;
    private BaseCodeGenerator serviceImplGenerator;
    private BaseCodeGenerator dtoGenerator;
    private BaseCodeGenerator mapperGenerator;
    private BaseCodeGenerator mapperXmlGenerator;
    private BaseCodeGenerator entityGenerator;

    public String getControllerPackageName() {

        return this.controllerPackageName;
    }

    public void setControllerPackageName(String controllerPackageName) {
        this.assertInitialized();
        this.controllerPackageName = controllerPackageName;
    }

    public String getVoPackageName() {

        return this.voPackageName;
    }

    public void setVoPackageName(String voPackageName) {
        this.assertInitialized();
        this.voPackageName = voPackageName;
    }

    public String getServicePackageName() {

        return this.servicePackageName;
    }

    public void setServicePackageName(String servicePackageName) {
        this.assertInitialized();
        this.servicePackageName = servicePackageName;
    }

    public String getServiceImplPackageName() {

        return this.serviceImplPackageName;
    }

    public void setServiceImplPackageName(String serviceImplPackageName) {
        this.assertInitialized();
        this.serviceImplPackageName = serviceImplPackageName;
    }

    public String getDtoPackageName() {

        return this.dtoPackageName;
    }

    public void setDtoPackageName(String dtoPackageName) {
        this.assertInitialized();
        this.dtoPackageName = dtoPackageName;
    }

    public String getMapperPackageName() {

        return this.mapperPackageName;
    }

    public void setMapperPackageName(String mapperPackageName) {
        this.assertInitialized();
        this.mapperPackageName = mapperPackageName;
    }

    public String getEntityPackageName() {

        return this.entityPackageName;
    }

    public void setEntityPackageName(String entityPackageName) {
        this.assertInitialized();
        this.entityPackageName = entityPackageName;
    }

    public String getControllerTemplatePath() {

        return this.controllerTemplatePath;
    }

    public void setControllerTemplatePath(String controllerTemplatePath) {
        this.assertInitialized();
        this.controllerTemplatePath = controllerTemplatePath;
    }

    public String getVoTemplatePath() {

        return this.voTemplatePath;
    }

    public void setVoTemplatePath(String voTemplatePath) {
        this.assertInitialized();
        this.voTemplatePath = voTemplatePath;
    }

    public String getServiceTemplatePath() {

        return this.serviceTemplatePath;
    }

    public void setServiceTemplatePath(String serviceTemplatePath) {
        this.assertInitialized();
        this.serviceTemplatePath = serviceTemplatePath;
    }

    public String getServiceImplTemplatePath() {

        return this.serviceImplTemplatePath;
    }

    public void setServiceImplTemplatePath(String serviceImplTemplatePath) {
        this.assertInitialized();
        this.serviceImplTemplatePath = serviceImplTemplatePath;
    }

    public String getDtoTemplatePath() {

        return this.dtoTemplatePath;
    }

    public void setDtoTemplatePath(String dtoTemplatePath) {
        this.assertInitialized();
        this.dtoTemplatePath = dtoTemplatePath;
    }

    public String getMapperTemplatePath() {

        return this.mapperTemplatePath;
    }

    public void setMapperTemplatePath(String mapperTemplatePath) {
        this.assertInitialized();
        this.mapperTemplatePath = mapperTemplatePath;
    }

    public String getMapperXmlTemplatePath() {

        return this.mapperXmlTemplatePath;
    }

    public void setMapperXmlTemplatePath(String mapperXmlTemplatePath) {
        this.assertInitialized();
        this.mapperXmlTemplatePath = mapperXmlTemplatePath;
    }

    public String getEntityTemplatePath() {

        return this.entityTemplatePath;
    }

    public void setEntityTemplatePath(String entityTemplatePath) {
        this.assertInitialized();
        this.entityTemplatePath = entityTemplatePath;
    }

    public String getControllerOutputPath() {

        return this.controllerOutputPath;
    }

    public void setControllerOutputPath(String controllerOutputPath) {
        this.assertInitialized();
        this.controllerOutputPath = controllerOutputPath;
    }

    public String getVoOutputPath() {

        return this.voOutputPath;
    }

    public void setVoOutputPath(String voOutputPath) {
        this.assertInitialized();
        this.voOutputPath = voOutputPath;
    }

    public String getServiceOutputPath() {

        return this.serviceOutputPath;
    }

    public void setServiceOutputPath(String serviceOutputPath) {
        this.assertInitialized();
        this.serviceOutputPath = serviceOutputPath;
    }

    public String getServiceImplOutputPath() {

        return this.serviceImplOutputPath;
    }

    public void setServiceImplOutputPath(String serviceImplOutputPath) {
        this.assertInitialized();
        this.serviceImplOutputPath = serviceImplOutputPath;
    }

    public String getDtoOutputPath() {

        return this.dtoOutputPath;
    }

    public void setDtoOutputPath(String dtoOutputPath) {
        this.assertInitialized();
        this.dtoOutputPath = dtoOutputPath;
    }

    public String getMapperOutputPath() {

        return this.mapperOutputPath;
    }

    public void setMapperOutputPath(String mapperOutputPath) {
        this.assertInitialized();
        this.mapperOutputPath = mapperOutputPath;
    }

    public String getMapperXmlOutputPath() {

        return this.mapperXmlOutputPath;
    }

    public void setMapperXmlOutputPath(String mapperXmlOutputPath) {
        this.assertInitialized();
        this.mapperXmlOutputPath = mapperXmlOutputPath;
    }

    public String getEntityOutputPath() {

        return this.entityOutputPath;
    }

    public void setEntityOutputPath(String entityOutputPath) {
        this.assertInitialized();
        this.entityOutputPath = entityOutputPath;
    }

    public BaseCodeGenerator getControllerGenerator() {

        return this.controllerGenerator;
    }

    public void setControllerGenerator(BaseCodeGenerator controllerGenerator) {
        this.assertInitialized();
        this.controllerGenerator = controllerGenerator;
    }

    public BaseCodeGenerator getVoGenerator() {

        return this.voGenerator;
    }

    public void setVoGenerator(BaseCodeGenerator voGenerator) {
        this.assertInitialized();
        this.voGenerator = voGenerator;
    }

    public BaseCodeGenerator getServiceGenerator() {

        return this.serviceGenerator;
    }

    public void setServiceGenerator(BaseCodeGenerator serviceGenerator) {
        this.assertInitialized();
        this.serviceGenerator = serviceGenerator;
    }

    public BaseCodeGenerator getServiceImplGenerator() {

        return this.serviceImplGenerator;
    }

    public void setServiceImplGenerator(BaseCodeGenerator serviceImplGenerator) {
        this.assertInitialized();
        this.serviceImplGenerator = serviceImplGenerator;
    }

    public BaseCodeGenerator getDtoGenerator() {

        return this.dtoGenerator;
    }

    public void setDtoGenerator(BaseCodeGenerator dtoGenerator) {
        this.assertInitialized();
        this.dtoGenerator = dtoGenerator;
    }

    public BaseCodeGenerator getMapperGenerator() {

        return this.mapperGenerator;
    }

    public void setMapperGenerator(BaseCodeGenerator mapperGenerator) {
        this.assertInitialized();
        this.mapperGenerator = mapperGenerator;
    }

    public BaseCodeGenerator getMapperXmlGenerator() {

        return this.mapperXmlGenerator;
    }

    public void setMapperXmlGenerator(BaseCodeGenerator mapperXmlGenerator) {
        this.assertInitialized();
        this.mapperXmlGenerator = mapperXmlGenerator;
    }

    public BaseCodeGenerator getEntityGenerator() {

        return this.entityGenerator;
    }

    public void setEntityGenerator(BaseCodeGenerator entityGenerator) {
        this.assertInitialized();
        this.entityGenerator = entityGenerator;
    }

}
