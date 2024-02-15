package artoria.generator.id.support;

import artoria.exception.ExceptionUtils;
import artoria.util.Assert;
import artoria.util.CollectionUtils;
import org.springframework.jdbc.core.*;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static artoria.common.constant.Numbers.ONE;
import static artoria.common.constant.Numbers.ZERO;

/**
 * Jdbc string identifier generator.
 * @author Kahle
 */
@Deprecated
public class JdbcStringIdGenerator extends AbstractIncrementalIdGenerator {
    private static final String SQL_QUERY_TEMPLATE = "SELECT `%s` FROM `%s` WHERE `%s` = ? FOR UPDATE;";
    private static final String SQL_INSERT_TEMPLATE = "INSERT INTO `%s` (`%s`, `%s`) VALUES (?, ?);";
    private static final String SQL_UPDATE_TEMPLATE = "UPDATE `%s` SET `%s` = ? WHERE `%s` = ?;";
    private final TransactionTemplate transactionTemplate;
    private final JdbcTemplate jdbcTemplate;
    private String valueColumn;
    private String nameColumn;
    private String tableName;

    public JdbcStringIdGenerator(TransactionTemplate transactionTemplate,
                                 JdbcTemplate jdbcTemplate,
                                 IncrementalIdConfig config) {
        super(config);
        Assert.notNull(transactionTemplate, "Parameter \"transactionTemplate\" must not null. ");
        Assert.notNull(jdbcTemplate, "Parameter \"jdbcTemplate\" must not null. ");
        this.transactionTemplate = transactionTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getValueColumn() {

        return valueColumn;
    }

    public void setValueColumn(String valueColumn) {

        this.valueColumn = valueColumn;
    }

    public String getNameColumn() {

        return nameColumn;
    }

    public void setNameColumn(String nameColumn) {

        this.nameColumn = nameColumn;
    }

    public String getTableName() {

        return tableName;
    }

    public void setTableName(String tableName) {

        this.tableName = tableName;
    }

    private void insert(String name, Long value) {
        String insertSql = String.format(SQL_INSERT_TEMPLATE, tableName, nameColumn, valueColumn);
        int effect = jdbcTemplate.update(insertSql, name, value);
        if (effect != ONE) {
            throw new IllegalStateException("Failed to insert the value of identifier. ");
        }
    }

    private void update(String name, Long value) {
        String updateSql = String.format(SQL_UPDATE_TEMPLATE, tableName, valueColumn, nameColumn);
        int effect = jdbcTemplate.update(updateSql, value, name);
        if (effect != ONE) {
            throw new IllegalStateException("Failed to update the value of identifier. ");
        }
    }

    private List<Long> query(String name) {
        String querySql = String.format(SQL_QUERY_TEMPLATE, valueColumn, tableName, nameColumn);
        RowMapper<Long> rowMapper = new SingleColumnRowMapper<Long>(Long.class);
        ResultSetExtractor<List<Long>> resultSetExtractor = new RowMapperResultSetExtractor<Long>(rowMapper, ONE);
        return jdbcTemplate.query(querySql, new Object[]{name}, resultSetExtractor);
    }

    private Long increment() {
        long stepLength = getConfig().getStepLength();
        String name = getConfig().getName();
        List<Long> longList = query(name);
        Long result;
        if (CollectionUtils.isNotEmpty(longList)) {
            result = longList.get(ZERO);
            result = result != null
                    ? result + stepLength : stepLength;
            update(name, result);
        }
        else {
            result = stepLength;
            insert(name, result);
        }
        return result;
    }

    @Override
    protected Long incrementAndGet() {
        return transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(@Nullable TransactionStatus status) {
                if (status == null) {
                    throw new IllegalArgumentException(
                            "This is a mistake that should not have happened. "
                    );
                }
                try {
                    return increment();
                }
                catch (Exception e) {
                    status.setRollbackOnly();
                    throw ExceptionUtils.wrap(e);
                }
            }
        });
    }

}
