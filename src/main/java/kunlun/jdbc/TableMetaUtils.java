package kunlun.jdbc;

import kunlun.util.CloseUtils;

import java.sql.*;
import java.util.*;

import static kunlun.common.constant.Numbers.*;
import static kunlun.common.constant.Symbols.COMMA;

@Deprecated
public class TableMetaUtils {

    public static List<TableMeta> getTableMetaList(Connection connection, String catalog) throws SQLException {
        List<TableMeta> tableMetaList = new ArrayList<TableMeta>();
        String[] types = new String[]{"TABLE"};
        ResultSet tableResultSet = null;
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            tableResultSet = databaseMetaData.getTables(catalog, (String) null, (String) null, types);
            while (tableResultSet.next()) {
                Map<String, ColumnMeta> columnMap = new HashMap<String, ColumnMeta>(TWENTY);
                String tableName = tableResultSet.getString((String) "TABLE_NAME");
                String remarks = tableResultSet.getString((String) "REMARKS");
                TableMeta tableMeta = new TableMeta();
                tableMetaList.add(tableMeta);
                tableMeta.setName(tableName);
                tableMeta.setRemarks(remarks);
                fillColumnMeta(databaseMetaData, catalog, tableMeta, columnMap);
                fillClassName(connection, tableName, columnMap);
            }
            return tableMetaList;
        }
        finally {
            CloseUtils.closeQuietly(tableResultSet);
            CloseUtils.closeQuietly(connection);
        }
    }

    private static void fillColumnMeta(DatabaseMetaData dbMetaData, String catalog, TableMeta tableMeta, Map<String, ColumnMeta> columnMap) throws SQLException {
        String tableName = tableMeta.getName();
        ResultSet columnResultSet = null;
        try {
            String primaryKey = findPrimaryKey(catalog, tableName, dbMetaData);
            tableMeta.setPrimaryKey(primaryKey);
            columnResultSet = dbMetaData.getColumns(catalog, null, tableName, null);
            tableMeta.setColumnList(new ArrayList<ColumnMeta>());
            List<String> primaryKeyList = Arrays.asList(primaryKey.split(COMMA));
            while (columnResultSet.next()) {
                ColumnMeta columnMeta = createColumnMeta(primaryKeyList, columnResultSet);
                tableMeta.getColumnList().add(columnMeta);
                columnMap.put(columnMeta.getName(), columnMeta);
            }
        }
        finally {
            CloseUtils.closeQuietly(columnResultSet);
        }
    }

    private static ColumnMeta createColumnMeta(List<String> primaryKeyList, ResultSet columnResultSet) throws SQLException {
        ColumnMeta columnMeta = new ColumnMeta();
        String columnName = columnResultSet.getString("COLUMN_NAME");
        columnMeta.setName(columnName);
        columnMeta.setType(columnResultSet.getString("TYPE_NAME"));
        columnMeta.setSize(columnResultSet.getInt("COLUMN_SIZE"));
        columnMeta.setDecimalDigits(columnResultSet.getInt("DECIMAL_DIGITS"));
        columnMeta.setRemarks(columnResultSet.getString("REMARKS"));
        columnMeta.setNullable(columnResultSet.getString("IS_NULLABLE"));
        columnMeta.setAutoincrement(columnResultSet.getString("IS_AUTOINCREMENT"));
        columnMeta.setPrimaryKey(primaryKeyList.contains(columnName));
        return columnMeta;
    }

    private static String findPrimaryKey(String catalog, String tableName, DatabaseMetaData dbMetaData) throws SQLException {
        StringBuilder primaryKey = new StringBuilder();
        ResultSet primaryKeysResultSet = null;
        try {
            primaryKeysResultSet = dbMetaData.getPrimaryKeys(catalog, null, tableName);
            while (primaryKeysResultSet.next()) {
                primaryKey.append(primaryKeysResultSet.getString("COLUMN_NAME")).append(COMMA);
            }
            if (primaryKey.length() > ZERO) {
                primaryKey.deleteCharAt(primaryKey.length() - ONE);
            }
            return primaryKey.toString();
        }
        finally {
            CloseUtils.closeQuietly(primaryKeysResultSet);
        }
    }

    private static void fillClassName(Connection connection, String tableName, Map<String, ColumnMeta> columnMap) throws SQLException {
        String sql = "select * from " + tableName + " where 1 = 2";
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                String columnClassName = metaData.getColumnClassName(i);
                ColumnMeta columnMeta = columnMap.get(columnName);
                if (columnMeta == null) { continue; }
                columnMeta.setClassName(columnClassName);
            }
        }
        finally {
            CloseUtils.closeQuietly(resultSet);
            CloseUtils.closeQuietly(statement);
        }
    }

}
