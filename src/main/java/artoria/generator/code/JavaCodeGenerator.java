package artoria.generator.code;

import artoria.data.bean.BeanUtils;
import artoria.exception.ExceptionUtils;
import artoria.generator.Generator;
import artoria.generator.code.support.java.SimpleFileBuilder;
import artoria.generator.code.support.java.SimpleFileContext;
import artoria.jdbc.ColumnMeta;
import artoria.jdbc.TableMeta;
import artoria.jdbc.TableMetaUtils;
import artoria.renderer.TextRenderer;
import artoria.util.Assert;
import artoria.util.CollectionUtils;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

import static artoria.common.Constants.*;
import static artoria.io.util.IOUtils.EOF;

@Deprecated
public class JavaCodeGenerator implements Generator {
    private static Logger log = LoggerFactory.getLogger(JavaCodeGenerator.class);
    private static final String JAVA_TYPE = "javaType";
    private static final String ENTITY = "entity";
    private static final String DTO = "dto";
    private static final String VO = "vo";
    private static final String JAVA = "java";
    private static final String XML = "xml";
    private Map<String, FileBuilder> builderMap = new HashMap<String, FileBuilder>();

    protected String getFilename(Map<String, Object> tableInfo, String templateName) {
        Assert.notNull(tableInfo, "Parameter \"tableInfo\" must not null. ");
        int index = templateName.lastIndexOf(UNDERLINE), length;
        String begin = templateName, end = EMPTY_STRING;
        if (index != -1 && index + 1 !=
                (length = templateName.length())) {
            begin = templateName.substring(ZERO, index);
            end = templateName.substring(index + ONE, length);
        }
        if (begin.endsWith(UNDERLINE)) {
            begin = begin.substring(ZERO, begin.length() - ONE);
        }
        begin = StringUtils.uncapitalize(begin);
        String className = (String) tableInfo.get(begin + "ClassName");
        return className + DOT + end;
    }

    protected List<TableMeta> getTables(JavaCodeGenConfig javaCodeGenConfig) {
        Set<String> removedTableNamePrefixes = javaCodeGenConfig.getRemovedTableNamePrefixes();
        Set<String> reservedTables = javaCodeGenConfig.getReservedTables();
        Set<String> excludedTables = javaCodeGenConfig.getExcludedTables();
        String jdbcUrl = javaCodeGenConfig.getJdbcUrl();
        String user = javaCodeGenConfig.getJdbcUser();
        String password = javaCodeGenConfig.getJdbcPassword();
        String catalog = javaCodeGenConfig.getJdbcCatalog();
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
            List<TableMeta> tableMetaList = TableMetaUtils.getTableMetaList(connection, catalog);
            if (CollectionUtils.isEmpty(tableMetaList)) { return tableMetaList; }
            // Table filtering and alias handling.
            List<TableMeta> result = new ArrayList<TableMeta>();
            for (TableMeta tableMeta : tableMetaList) {
                String tableName = tableMeta.getName();
                if (!reservedTables.isEmpty() && !reservedTables.contains(tableName)) {
                    continue;
                }
                if (!excludedTables.isEmpty() && excludedTables.contains(tableName)) {
                    continue;
                }
                // Handle entity name (alias).
                String entityName = tableName;
                if (!removedTableNamePrefixes.isEmpty()) {
                    for (String prefix : removedTableNamePrefixes) {
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
                tableMeta.setAlias(entityName);
                result.add(tableMeta);
            }
            return result;
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    protected Map<String, Object> convert(TableMeta tableMeta) {
        if (tableMeta == null) { return null; }
        Map<String, Object> result = BeanUtils.beanToMap(tableMeta);
        // Get the column list and create a column map list.
        List<Map<String, Object>> columnMapList = new ArrayList<Map<String, Object>>();
        List<ColumnMeta> columnMetaList = tableMeta.getColumnList();
        result.put("columnList", columnMapList);
        if (CollectionUtils.isEmpty(columnMetaList)) { return result; }
        // Create a map of the import classes.
        Map<String, String> importClassMap = new HashMap<String, String>(columnMetaList.size());
        Set<String> typeImportList = new HashSet<String>();
        result.put("typeImportList", typeImportList);
        // Processing column data.
        for (ColumnMeta columnMeta : columnMetaList) {
            // Create column map.
            if (columnMeta == null) { continue; }
            Map<String, Object> columnMap = BeanUtils.beanToMap(columnMeta);
            columnMapList.add(columnMap);
            // Set column field, getter and setter.
            String columnName = columnMeta.getName();
            String fieldName = StringUtils.underlineToCamel(columnName);
            columnMap.put("fieldName", StringUtils.uncapitalize(fieldName));
            String capFieldName = StringUtils.capitalize(fieldName);
            columnMap.put("getterName", GET + capFieldName);
            columnMap.put("setterName", SET + capFieldName);
            // Handle java type.
            String columnClassName = columnMeta.getClassName();
            String columnType = columnMeta.getType();
            String typeMapping =
                    DatabaseTypeMapper.getTypeMapping(columnType, columnClassName);
            columnClassName =
                    StringUtils.isNotBlank(typeMapping) ? typeMapping : columnClassName;
            if (StringUtils.isBlank(columnClassName)) { continue; }
            // Try to convert short java type.
            int index = columnClassName.lastIndexOf(DOT);
            if (index == EOF) {
                columnMap.put(JAVA_TYPE, columnClassName);
                continue;
            }
            String shortName = columnClassName.substring(index + ONE);
            String className = importClassMap.get(shortName);
            if (className != null && !className.equals(columnClassName)) {
                columnMap.put(JAVA_TYPE, columnClassName);
            }
            else {
                importClassMap.put(shortName, columnClassName);
                typeImportList.add(columnClassName);
                columnMap.put(JAVA_TYPE, shortName);
            }
        }
        return result;
    }

    protected void fillTableInfo(FileContext buildContext, JavaCodeGenConfig javaCodeGenConfig) {
        //
        String xmlBaseOutputPath = javaCodeGenConfig.getXmlBaseOutputPath();
        boolean notBlankXmlDir = StringUtils.isNotBlank(xmlBaseOutputPath);
        //
        List<TableMeta> tableList = getTables(javaCodeGenConfig);
        // Clipping template name.
        Map<String, String> typeNameMap = new HashMap<String, String>(builderMap.size());
        for (FileBuilder codeBuilder : builderMap.values()) {
            String templateName = codeBuilder.getName();
            String typeName = templateName;
            int index = typeName.lastIndexOf(UNDERLINE);
            if (index != MINUS_ONE) {
                typeName = typeName.substring(ZERO, index);
            }
            typeName = StringUtils.uncapitalize(typeName);
            typeNameMap.put(templateName, typeName);
        }
        // Convert table meta list to table map list.
        List<String> tableNames = new ArrayList<String>();
        for (TableMeta tableMeta : tableList) {
            if (tableMeta == null) { continue; }
            String tableName = tableMeta.getName();
            tableNames.add(tableName);
            //
            Map<String, Object> tableInfo = convert(tableMeta);
            if (tableInfo == null) { continue; }
            // Get the name of the entity.
            String entityName = tableMeta.getAlias();
            // Create class name, object name and package name.
            for (Map.Entry<String, FileBuilder> entry : builderMap.entrySet()) {
                FileBuilder codeBuilder = entry.getValue();
                if (codeBuilder == null) { continue; }
                String templateName = codeBuilder.getName();
                String baseOutputPath = javaCodeGenConfig.getBaseOutputPath();
                String basePackageName = javaCodeGenConfig.getBasePackageName();
                String businessPackageName = (String) buildContext.getAttribute(templateName, "businessPackageName");
                // typeName
                String typeName = typeNameMap.get(templateName);
                String backTypeName = StringUtils.capitalize(typeName);
                if (VO.equalsIgnoreCase(backTypeName)
                        || DTO.equalsIgnoreCase(backTypeName)) {
                    backTypeName = backTypeName.toUpperCase();
                }
                else if (ENTITY.equalsIgnoreCase(backTypeName)) {
                    backTypeName = EMPTY_STRING;
                }
                String className = entityName + backTypeName;
                tableInfo.put(typeName + "ClassName", className);
                String objectName = StringUtils.uncapitalize(className);
                tableInfo.put(typeName + "ObjectName", objectName);
                String packageName = basePackageName + businessPackageName;
                tableInfo.put(typeName + "PackageName", packageName);
                //
                boolean existXmlDir = templateName.toLowerCase().endsWith(XML) && notBlankXmlDir;
                packageName = StringUtils.replace(packageName, DOT, SLASH);
                File outputDir = existXmlDir ? new File(xmlBaseOutputPath) : new File(baseOutputPath, packageName);
                String filename = getFilename(tableInfo, templateName);
                String outputPath = new File(outputDir, filename).toString();
                ((SimpleFileContext) buildContext).setOutputPath(templateName, tableName, outputPath);
            }
            // Add to the result.
            ((SimpleFileContext) buildContext).setTableInfo(tableName, tableInfo);
        }
        ((SimpleFileContext) buildContext).setTableNames(tableNames);
    }

    protected FileContext createContext(JavaCodeGenConfig javaCodeGenConfig) {
        TextRenderer textRenderer = javaCodeGenConfig.getTextRenderer();
        SimpleFileContext buildContext = new SimpleFileContext();
        Map<String, Object> attributes = javaCodeGenConfig.getCustomAttributes();

        List<JavaTemplateConfig> templateConfigs = javaCodeGenConfig.getTemplateConfigs();
        for (JavaTemplateConfig templateConfig : templateConfigs) {
            if (templateConfig == null) { continue; }
            String templateName = templateConfig.getTemplateName();
            String businessPackageName = templateConfig.getBusinessPackageName();
            Boolean skipExisted = templateConfig.getSkipExisted();
            if (StringUtils.isBlank(templateName)) { continue; }
            //
            buildContext.putAttributes(templateName, attributes);
            // Charset
            buildContext.setTemplateCharset(templateName, UTF_8);
            buildContext.setOutputCharset(templateName, UTF_8);
            // SkipExisted
            buildContext.setSkipExisted(templateName, skipExisted);
            // businessPackageName
            buildContext.putAttribute(templateName, "businessPackageName", businessPackageName);
            // CoverMark
            if (templateName.toLowerCase().contains(XML)) {
                buildContext.putAttribute(templateName, "beginCoverTag", javaCodeGenConfig.getXmlBeginCoverTag());
                buildContext.putAttribute(templateName, "endCoverTag", javaCodeGenConfig.getXmlEndCoverTag());
            }
            else if (templateName.toLowerCase().contains(JAVA)) {
                buildContext.putAttribute(templateName, "beginCoverTag", javaCodeGenConfig.getJavaBeginCoverTag());
                buildContext.putAttribute(templateName, "endCoverTag", javaCodeGenConfig.getJavaEndCoverTag());
            }
            // getTemplatePath
            String baseTemplatePath = javaCodeGenConfig.getBaseTemplatePath();
            String extensionName = javaCodeGenConfig.getTemplateExtensionName();
            String fileName = templateName + extensionName;
            String templatePath = new File(baseTemplatePath, fileName).toString();
            buildContext.setTemplatePath(templateName, templatePath);
            // Code builder
            SimpleFileBuilder codeBuilder = new SimpleFileBuilder(templateName, textRenderer);
            builderMap.put(templateName, codeBuilder);
        }
        return buildContext;
    }

    public void generate(JavaCodeGenConfig javaCodeGenConfig) {
        if (javaCodeGenConfig == null) { return; }
        FileContext buildContext = createContext(javaCodeGenConfig);
        fillTableInfo(buildContext, javaCodeGenConfig);
        for (Map.Entry<String, FileBuilder> entry : builderMap.entrySet()) {
            FileBuilder codeBuilder = entry.getValue();
            String result = codeBuilder.build(buildContext);
        }
    }

}
