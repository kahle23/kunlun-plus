package artoria.generator;

import artoria.jdbc.DatabaseClient;
import artoria.jdbc.SimpleDataSource;
import com.alibaba.fastjson.JSON;
import org.junit.Ignore;
import org.junit.Test;
import artoria.generator.code.ColumnInfo;
import artoria.generator.code.TableInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Ignore
public class TableInfoGetTest {
    private static DatabaseClient databaseClient = new DatabaseClient(new SimpleDataSource());

    @Test
    public void test1() throws SQLException {
        Connection conn = databaseClient.getConnection();
        DatabaseMetaData databaseMetaData = conn.getMetaData();
        String schemaPattern = null;
        String tableNamePattern = "t_cms_topic";
        ResultSet tablesRs = databaseMetaData.getTables(null, schemaPattern, tableNamePattern, new String[]{"TABLE"});

        List<TableInfo> tableInfoList = new ArrayList<TableInfo>();
        while (tablesRs.next()) {
            TableInfo tableInfo = new TableInfo();
            // TABLE_CAT : test_db_information | TABLE_SCHEM : null | TABLE_NAME : t_cms_topic | TABLE_TYPE : TABLE | REMARKS :  | TYPE_CAT : null | TYPE_SCHEM : null | TYPE_NAME : null | SELF_REFERENCING_COL_NAME : null | REF_GENERATION : null
            tableInfo.setName(tablesRs.getString("TABLE_NAME"));
            tableInfo.setRemarks(tablesRs.getString("REMARKS"));

            StringBuilder primaryKey = new StringBuilder();
            ResultSet primaryKeysRs = databaseMetaData.getPrimaryKeys(null, null, tableInfo.getName());
            while (primaryKeysRs.next()) {
                // TABLE_CAT : test_db_information | TABLE_SCHEM : null | TABLE_NAME : t_cms_topic | COLUMN_NAME : ID | KEY_SEQ : 1 | PK_NAME : PRIMARY
                primaryKey.append(primaryKeysRs.getString("COLUMN_NAME")).append(",");
            }
            primaryKeysRs.close();
            primaryKey.deleteCharAt(primaryKey.length() - 1);
            tableInfo.setPrimaryKey(primaryKey.toString());

            ResultSet columnsRs = databaseMetaData.getColumns(null, null, tableInfo.getName(), null);
            tableInfo.setColumnInfoList(new ArrayList<ColumnInfo>());

            while (columnsRs.next()) {
                // TABLE_CAT : test_db_information | TABLE_SCHEM : null | TABLE_NAME : t_cms_topic | COLUMN_NAME : ID | DATA_TYPE : -5 | TYPE_NAME : BIGINT | COLUMN_SIZE : 19 | BUFFER_LENGTH : 65535 | DECIMAL_DIGITS : 0 | NUM_PREC_RADIX : 10 | NULLABLE : 0 | REMARKS : Topic id | COLUMN_DEF : null | SQL_DATA_TYPE : 0 | SQL_DATETIME_SUB : 0 | CHAR_OCTET_LENGTH : null | ORDINAL_POSITION : 1 | IS_NULLABLE : NO | SCOPE_CATALOG : null | SCOPE_SCHEMA : null | SCOPE_TABLE : null | SOURCE_DATA_TYPE : null | IS_AUTOINCREMENT : YES | IS_GENERATEDCOLUMN :  |
                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.setName(columnsRs.getString("COLUMN_NAME"));
                columnInfo.setType(columnsRs.getString("TYPE_NAME"));
                columnInfo.setSize(columnsRs.getInt("COLUMN_SIZE"));
                columnInfo.setDecimalDigits(columnsRs.getInt("DECIMAL_DIGITS"));
                columnInfo.setRemarks(columnsRs.getString("REMARKS"));
                columnInfo.setNullable(columnsRs.getString("IS_NULLABLE"));
                columnInfo.setAutoincrement(columnsRs.getString("IS_AUTOINCREMENT"));
                if (primaryKey.indexOf(columnInfo.getName()) > 0) {
                    columnInfo.setPrimaryKey(true);
                }
                else {
                    columnInfo.setPrimaryKey(false);
                }
                tableInfo.getColumnInfoList().add(columnInfo);
            }
            columnsRs.close();
            tableInfoList.add(tableInfo);

            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("select * from " + tableInfo.getName() + " where 1 = 2");
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                System.out.println(">>>> " + rsmd.getColumnName(i) + " >> " + rsmd.getColumnClassName(i));
            }

        }
        tablesRs.close();
        conn.close();
        System.out.println(JSON.toJSONString(tableInfoList, true));
    }

}
