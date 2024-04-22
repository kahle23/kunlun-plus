package kunlun.generator.render.support.java;

import kunlun.data.Dict;
import kunlun.data.bean.BeanUtils;
import kunlun.db.jdbc.meta.Column;
import kunlun.db.jdbc.meta.Table;
import kunlun.generator.render.support.AbstractRenderFileGenerator;
import kunlun.util.Assert;
import kunlun.util.CollectionUtils;
import kunlun.util.MapUtils;
import kunlun.util.StringUtils;

import java.io.File;
import java.util.*;

import static kunlun.common.constant.Numbers.ONE;
import static kunlun.common.constant.Symbols.*;
import static kunlun.common.constant.Words.GET;
import static kunlun.common.constant.Words.SET;
import static kunlun.io.util.IOUtils.EOF;

/**
 * The java code generator.
 * @author Kahle
 */
public class JavaCodeGenerator extends AbstractRenderFileGenerator {
    protected static final String XML_FILE_SUFFIX = ".xml";
    protected static final String GETTER_NAME_KEY = "getterName";
    protected static final String SETTER_NAME_KEY = "setterName";
    protected static final String FIELD_NAME_KEY  = "fieldName";
    protected static final String JAVA_TYPE_KEY   = "javaType";
    protected static final String COLUMNS_KEY = "columns";
    protected static final String TABLE_KEY   = "table";
    protected static final String JAVA_TYPE_IMPORTS_KEY = "javaTypeImports";
    protected static final String PACKAGE_NAME_KEY_TAIL = "PackageName";
    protected static final String CLASS_NAME_KEY_TAIL   = "ClassName";
    protected static final String OBJECT_NAME_KEY_TAIL  = "ObjectName";
    protected static final String ENTITY_NAME_KEY_TAIL  = "EntityName";

    /**
     * Get the entity name in java based on the table name.
     * @param tableName The table name
     * @param removedTableNamePrefixes The prefix of table names to be removed
     * @return The entity name
     */
    protected String getEntityName(String tableName, Collection<String> removedTableNamePrefixes) {
        Assert.notBlank(tableName, "Parameter \"tableName\" must not blank. ");
        String result = tableName;
        if (CollectionUtils.isNotEmpty(removedTableNamePrefixes)) {
            for (String prefix : removedTableNamePrefixes) {
                if (result.startsWith(prefix)) {
                    result = result.replaceAll(prefix, EMPTY_STRING);
                    break;
                }
            }
        }
        result = StringUtils.underlineToCamel(result);
        result = StringUtils.capitalize(result);
        return result;
    }

    /**
     * Build template configurations based on context and input configuration.
     * @param context The context of the generator
     * @param config The input configuration
     */
    protected void buildTemplateConfigs(Context context, Config config) {
        // Get parameters.
        JavaCodeGenConfig genConfig = (JavaCodeGenConfig) config;
        String baseTemplatePath = genConfig.getBaseTemplatePath();
        String templateSuffix = withLeftDot(genConfig.getTemplateSuffix());
        String basePackageName = genConfig.getBasePackageName();
        String baseOutputPath = genConfig.getBaseOutputPath();
        String xmlBaseOutputPath = genConfig.getXmlBaseOutputPath();
        // Loop template configs.
        for (JavaCodeGenConfig.TemplateConfig templateConfig : genConfig.getTemplateConfigs().values()) {
            // Get parameters.
            String templateName = templateConfig.getTemplateName();
            String fileSuffix = withLeftDot(templateConfig.getFileSuffix());
            String bizPackageName = withLeftDot(templateConfig.getBizPackageName());
            // Build new template config.
            TemplateConfigImpl cfg = new TemplateConfigImpl(templateName);
            cfg.setTemplateCharset(genConfig.getTemplateCharset());
            cfg.setTemplatePath(new File(baseTemplatePath, templateName + templateSuffix).getPath());
            cfg.setOutputCharset(genConfig.getOutputCharset());
            if (XML_FILE_SUFFIX.equalsIgnoreCase(fileSuffix) && StringUtils.isNotBlank(xmlBaseOutputPath)) {
                cfg.setOutputPath(xmlBaseOutputPath);
            }
            else {
                String childPath = StringUtils.replace(basePackageName + bizPackageName, DOT, BACKSLASH);
                cfg.setOutputPath(new File(baseOutputPath, childPath).getPath());
            }
            cfg.setFileSuffix(fileSuffix);
            // Add to templateConfigs.
            context.getTemplateConfigs().add(cfg);
        }
    }

    /**
     * Create the table information object.
     * @param context The context of the generator
     * @param config The input configuration
     * @param tables The loaded table information
     * @param table The iterated single table information
     * @return The table information object
     */
    protected Dict createTableInfo(Context context, Config config, List<Table> tables, Table table) {
        // Get parameters.
        JavaCodeGenConfig genConfig = (JavaCodeGenConfig) config;
        Dict columnType2Java = genConfig.getColumnTypeToJavaTypeMap();
        // Declare variables.
        Set<String> javaTypeImports = new HashSet<String>();
        List<Dict>  columns = new ArrayList<Dict>();
        // Create table info object.
        Dict tableInfo = Dict.of(BeanUtils.beanToMap(table));
        tableInfo.set(JAVA_TYPE_IMPORTS_KEY, javaTypeImports);
        tableInfo.set(COLUMNS_KEY, columns);
        // Loop table columns.
        for (Column column : table.getColumns()) {
            // Create column info object.
            Dict columnInfo = Dict.of(BeanUtils.beanToMap(column));
            columns.add(columnInfo);
            // Set column field, getter and setter.
            String columnName = column.getName();
            String fieldName = StringUtils.underlineToCamel(columnName);
            columnInfo.put(FIELD_NAME_KEY, StringUtils.uncapitalize(fieldName));
            String capFieldName = StringUtils.capitalize(fieldName);
            columnInfo.put(GETTER_NAME_KEY, GET + capFieldName);
            columnInfo.put(SETTER_NAME_KEY, SET + capFieldName);
            // Get column's java type.
            String columnType = column.getType().toLowerCase();
            String javaType = columnType2Java.getString(columnType, columnType);
            columnInfo.put(JAVA_TYPE_KEY, javaType);
            // Try to convert short java type.
            int index = javaType.lastIndexOf(DOT);
            if (index != EOF) {
                String shortName = javaType.substring(index + ONE);
                if (StringUtils.isNotBlank(shortName)) {
                    javaTypeImports.add(javaType);
                    columnInfo.set(JAVA_TYPE_KEY, shortName);
                }
            }
        }
        return tableInfo;
    }

    /**
     * Build attributes object based on context, input configuration, and table information.
     * @param context The context of the generator
     * @param config The input configuration
     * @param tables The loaded table information
     * @param table The iterated single table information
     */
    protected void buildAttributes(Context context, Config config, List<Table> tables, Table table) {
        // Get parameters.
        JavaCodeGenConfig genConfig = (JavaCodeGenConfig) config;
        String basePackageName = genConfig.getBasePackageName();
        String tableName = table.getName();
        String entityName = getEntityName(tableName, genConfig.getRemovedTableNamePrefixes());
        // Create attributes.
        Map<String, Object> attributes = new LinkedHashMap<String, Object>();
        // Loop template configs.
        for (JavaCodeGenConfig.TemplateConfig templateConfig : genConfig.getTemplateConfigs().values()) {
            // Get parameters.
            String templateName = templateConfig.getTemplateName();
            String bizPackageName = withLeftDot(templateConfig.getBizPackageName());
            String nameTail = templateConfig.getNameTail();
            String fileSuffix = withLeftDot(templateConfig.getFileSuffix());
            // Set package name.
            attributes.put(templateName + PACKAGE_NAME_KEY_TAIL, basePackageName + bizPackageName);
            // Set entity name.
            attributes.put(templateName + ENTITY_NAME_KEY_TAIL, entityName);
            // Set class name.
            String className = entityName + nameTail;
            attributes.put(templateName + CLASS_NAME_KEY_TAIL, className);
            // Set object name.
            String objectName = StringUtils.uncapitalize(className);
            attributes.put(templateName + OBJECT_NAME_KEY_TAIL, objectName);
            // Set filename.
            attributes.put(templateName + FILENAME_KEY_TAIL, className);
            // Set skip existed
            attributes.put(templateName + SKIP_EXISTED_KEY_TAIL, templateConfig.getSkipExisted());
            // Set overlay tag.
            if (XML_FILE_SUFFIX.equalsIgnoreCase(fileSuffix)) {
                attributes.put(templateName + START_OVERRIDE_TAG_KEY_TAIL, genConfig.getXmlStartOverrideTag());
                attributes.put(templateName + END_OVERRIDE_TAG_KEY_TAIL, genConfig.getXmlEndOverrideTag());
            }
            else {
                attributes.put(templateName + START_OVERRIDE_TAG_KEY_TAIL, genConfig.getJavaStartOverrideTag());
                attributes.put(templateName + END_OVERRIDE_TAG_KEY_TAIL, genConfig.getJavaEndOverrideTag());
            }
            // Create table information and put.
            attributes.put(TABLE_KEY, createTableInfo(context, config, tables, table));
            // End loop.
        }
        // Custom can override the generated.
        Map<String, Object> customAttributes = genConfig.getCustomAttributes();
        if (MapUtils.isNotEmpty(customAttributes)) {
            attributes.putAll(customAttributes);
        }
        // Set to context.
        context.setAttributes(tableName, attributes);
    }

    @Override
    protected Context buildContext(Config config) {
        // Type conversion and validate.
        Assert.notNull(config, "Parameter \"config\" must not null. ");
        JavaCodeGenConfig genConfig = (JavaCodeGenConfig) config;
        Assert.notNull(genConfig.getTableLoader(), "Config \"tableLoader\" must not null. ");
        Assert.notNull(genConfig.getFileLoader(), "Config \"fileLoader\" must not null. ");
        Assert.notNull(genConfig.getRenderer(), "Config \"renderer\" must not null. ");
        Assert.notBlank(genConfig.getTemplateCharset(), "Config \"templateCharset\" must not blank. ");
        Assert.notBlank(genConfig.getOutputCharset(), "Config \"outputCharset\" must not blank. ");
        Assert.notBlank(genConfig.getBaseTemplatePath(), "Config \"baseTemplatePath\" must not blank. ");
        Assert.notBlank(genConfig.getBasePackageName(), "Config \"basePackageName\" must not blank. ");
        Assert.notBlank(genConfig.getBaseOutputPath(), "Config \"baseOutputPath\" must not blank. ");
        Assert.notEmpty(genConfig.getTemplateConfigs(), "Config \"templateConfigs\" must not empty. ");
        for (JavaCodeGenConfig.TemplateConfig templateConfig : genConfig.getTemplateConfigs().values()) {
            Assert.notNull(templateConfig, "Config \"templateConfig\" must not null. ");
            Assert.notBlank(templateConfig.getTemplateName()
                    , "Config \"templateConfig.templateName\" must not blank. ");
        }
        // Load tables information.
        Object loaderConfig = genConfig.getTableLoaderConfig();
        List<Table> tables = genConfig.getTableLoader().load(loaderConfig);
        if (tables == null) { tables = Collections.emptyList(); }
        // New context and init.
        ContextImpl context = new ContextImpl();
        context.setFileLoader(genConfig.getFileLoader());
        context.setRenderer(genConfig.getRenderer());
        context.setResourceNames(new ArrayList<String>());
        context.setTemplateConfigs(new ArrayList<TemplateConfig>());
        // Build template configs.
        buildTemplateConfigs(context, config);
        // Loop tables and handle.
        for (Table table : tables) {
            // Validate.
            Assert.notNull(table, "Variable \"table\" must not null. ");
            Assert.notBlank(table.getName(), "Variable \"table.name\" must not blank. ");
            Assert.notEmpty(table.getColumns(), "Variable \"table.columns\" must not empty. ");
            for (Column column : table.getColumns()) {
                Assert.notNull(column, "Variable \"table.column\" must not null. ");
                Assert.notBlank(column.getName(), "Variable \"table.column.name\" must not blank. ");
                Assert.notBlank(column.getType(), "Variable \"table.column.type\" must not blank. ");
            }
            // Handle.
            context.getResourceNames().add(table.getName());
            buildAttributes(context, genConfig, tables, table);
        }
        // End.
        return context;
    }

}
