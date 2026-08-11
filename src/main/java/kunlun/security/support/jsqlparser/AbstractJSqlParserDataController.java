/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.security.support.jsqlparser;

import kunlun.core.function.Function;
import kunlun.security.SecurityUtil;
import kunlun.security.support.AbstractSecurityContext;
import kunlun.security.support.AbstractSqlBasedDataController;
import kunlun.util.Assert;
import kunlun.util.CollUtil;
import kunlun.util.StrUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.parser.JSqlParser;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static kunlun.common.constant.Numbers.ZERO;
import static kunlun.common.constant.Symbols.DOT;

/**
 * The abstract JSqlParser-based data controller.
 * @see JSqlParser
 * @author Kahle
 */
public abstract class AbstractJSqlParserDataController extends AbstractSqlBasedDataController {
    private static final Logger log = LoggerFactory.getLogger(AbstractJSqlParserDataController.class);
    private final Map<String, Function<ColumnCfg, Expression>> builders;

    protected AbstractJSqlParserDataController(Map<String, Function<ColumnCfg, Expression>> builders) {
        Assert.notNull(builders, "Parameter \"builders\" must not null. ");
        this.builders = builders;
        this.init();
    }

    public AbstractJSqlParserDataController() {

        this(new ConcurrentHashMap<String, Function<ColumnCfg, Expression>>());
    }

    protected void init() {
        registerBuilder(Op.EQ, new Function<ColumnCfg, Expression>() {
            @Override
            public Expression apply(ColumnCfg columnCfg) {
                Object value = columnCfg.getValue();
                EqualsTo equalsTo = new EqualsTo();
                equalsTo.setLeftExpression(new Column(columnCfg.getColumnName()));
                equalsTo.setRightExpression(value != null ? new StringValue(String.valueOf(value)) : new NullValue());
                return equalsTo;
            }
        });
        registerBuilder(Op.IN, new Function<ColumnCfg, Expression>() {
            @Override
            public Expression apply(ColumnCfg columnCfg) {
                Object value = columnCfg.getValue();
                List<Expression> exprValList = new ArrayList<Expression>();
                if (value == null) { value = emptyList(); }
                if (value instanceof Collection) {
                    for (Object obj : (Collection<?>) value) {
                        exprValList.add(obj != null ? new StringValue(String.valueOf(obj)) : new NullValue());
                    }
                } else if (value.getClass().isArray()) {
                    int length = Array.getLength(value);
                    for (int i = ZERO; i < length; i++) {
                        Object obj = Array.get(value, i);
                        exprValList.add(obj != null ? new StringValue(String.valueOf(obj)) : new NullValue());
                    }
                } else {
                    throw new IllegalArgumentException("cfg value type is unsupported! ");
                }
                InExpression inExp = new InExpression();
                inExp.setLeftExpression(new Column(columnCfg.getColumnName()));
                inExp.setRightItemsList(new ExpressionList(exprValList));
                return inExp;
            }
        });
        registerBuilder(Op.LIKE, new Function<ColumnCfg, Expression>() {
            @Override
            public Expression apply(ColumnCfg columnCfg) {
                Object value = columnCfg.getValue();
                if (value == null) { return null; }
                LikeExpression likeExpression = new LikeExpression();
                likeExpression.setLeftExpression(new Column(columnCfg.getColumnName()));
                likeExpression.setRightExpression(new StringValue("%" + value + "%"));
                return likeExpression;
            }
        });
    }

    public void registerBuilder(String operator, Function<ColumnCfg, Expression> builder) {
        Assert.notBlank(operator, "Parameter \"operator\" must not blank. ");
        Assert.notNull(builder, "Parameter \"builder\" must not null. ");
        String className = builder.getClass().getName();
        builders.put(operator, builder);
        log.debug("Register the expression builder \"{}\" to \"{}\". ", className, operator);
    }

    public void deregisterBuilder(String operator) {
        Assert.notBlank(operator, "Parameter \"operator\" must not blank. ");
        Function<ColumnCfg, Expression> remove = builders.remove(operator);
        if (remove != null) {
            String className = remove.getClass().getName();
            log.debug("Deregister the expression builder \"{}\" from \"{}\". ", className, operator);
        }
    }

    public Function<ColumnCfg, Expression> getBuilder(String operator) {
        Assert.notBlank(operator, "Parameter \"operator\" must not blank. ");
        return builders.get(operator);
    }

    public Expression buildExpression(ColumnCfg cfg) {
        Function<ColumnCfg, Expression> builder = getBuilder(cfg.getOperator());
        Assert.notNull(builder, "The expression builder is null! ");
        return builder.apply(cfg);
    }

    public void processPlainSelect(PlainSelect plainSelect, Collection<ColumnCfg> configs) {
        if (CollUtil.isEmpty(configs)) { return; }
        AndExpression andExpr = new AndExpression(null, null);
        for (ColumnCfg cfg : configs) {
            if (andExpr.getLeftExpression() == null) {
                andExpr.setLeftExpression(buildExpression(cfg));
            } else {
                if (andExpr.getRightExpression() == null) {
                    andExpr.setRightExpression(buildExpression(cfg));
                } else {
                    andExpr = new AndExpression(andExpr, buildExpression(cfg));
                }
            }
        }
        //
        Expression where = plainSelect.getWhere();
        if (andExpr.getRightExpression() == null) {
            Expression leftExpr = andExpr.getLeftExpression();
            plainSelect.setWhere(where == null ? leftExpr : new AndExpression(where, leftExpr));
        } else {
            plainSelect.setWhere(where == null ? andExpr : new AndExpression(where, andExpr));
        }
    }

    private String obtainAlias(String fieldName) {
        if (StrUtil.isBlank(fieldName)) { return null; }
        int indexOf = fieldName.indexOf(DOT);
        if (indexOf <= ZERO) { return null; }
        return fieldName.substring(ZERO, indexOf);
    }

    public void processPlainSelect(PlainSelect plainSelect, Context context) {
        if (context == null) { return; }
        SimpleRule rule = (SimpleRule) context.getRule();
        if (rule == null) { return; }
        Collection<String> userGroups = context.getUserGroups();
        Object userId = context.getUserId();
        // nowFromTable maybe is "t_test t"
        FromItem fromItem = plainSelect.getFromItem();
        String nowFromTable = fromItem instanceof Table
                ? ((Table) fromItem).getName() : (fromItem != null ? String.valueOf(fromItem) : null);
        if (StrUtil.isBlank(nowFromTable)) { return; }
        // from table alias
        String fromTableAlias = fromItem.getAlias() != null ? fromItem.getAlias().getName() : null;
        List<String> tableAliases = new ArrayList<String>();
        if (CollUtil.isNotEmpty(plainSelect.getJoins())) {
            for (Join join : plainSelect.getJoins()) {
                if (join == null) { continue; }
                FromItem rightItem = join.getRightItem();
                if (rightItem == null || rightItem.getAlias() == null) { continue; }
                tableAliases.add(rightItem.getAlias().getName());
            }
        }
        if (StrUtil.isNotBlank(fromTableAlias)) {
            tableAliases.add(fromTableAlias);
        }
        //
        Collection<ColumnCfg> configs = new ArrayList<ColumnCfg>();
        switch (rule.getDataScope()) {
            case ALL: { return; }
            case SELF: {
                Collection<DataConfig> dataConfigs = rule.getDataConfigs();
                if (dataConfigs == null) { dataConfigs = singletonList(new DataConfig()); }
                for (DataConfig dataCfg : dataConfigs) {
                    String fromTable = dataCfg.getFromTable();
                    if (StrUtil.isNotBlank(fromTable) &&
                            !nowFromTable.equals(fromTable)) {
                        continue;
                    }
                    Collection<String> userIdFields = dataCfg.getUserIdFields();
                    if (CollUtil.isEmpty(userIdFields)) {
                        userIdFields = getDefaultUserIdFields();
                    }
                    for (String str : userIdFields) {
                        String alias = obtainAlias(str);
                        // 如果字段包含表别名（表别名不为空），表别名必须是 SQL 中存在的
                        if (StrUtil.isNotBlank(alias) && !tableAliases.contains(alias)) {
                            continue;
                        }
                        // 如果字段的表别名是空的，from table 有别名，则给字段拼上
                        if (StrUtil.isBlank(alias) && StrUtil.isNotBlank(fromTableAlias)) {
                            str = fromTableAlias + DOT + str;
                        }
                        configs.add(new ColumnCfg(Op.EQ, str, userId));
                    }
                }
            } break;
            case NONE: {
                Collection<DataConfig> dataConfigs = rule.getDataConfigs();
                if (dataConfigs == null) { dataConfigs = emptyList(); }
                boolean isSkip = false;
                for (DataConfig dataCfg : dataConfigs) {
                    String tableName = dataCfg.getFromTable();
                    if (StrUtil.isNotBlank(tableName) &&
                            !nowFromTable.equals(tableName)) {
                        isSkip = true; break;
                    }
                }
                if (!isSkip) {
                    configs = singletonList(new ColumnCfg(Op.EQ, "1", "0"));
                }
            } break;
            case GROUP: {
                Collection<DataConfig> dataConfigs = rule.getDataConfigs();
                if (dataConfigs == null) { dataConfigs = singletonList(new DataConfig()); }
                for (DataConfig dataCfg : dataConfigs) {
                    String tableName = dataCfg.getFromTable();
                    if (StrUtil.isNotBlank(tableName) &&
                            !nowFromTable.equals(tableName)) {
                        continue;
                    }
                    Collection<String> orgIdFields = dataCfg.getOrgIdFields();
                    if (CollUtil.isEmpty(orgIdFields)) {
                        orgIdFields = getDefaultOrgIdFields();
                    }
                    for (String str : orgIdFields) {
                        String alias = obtainAlias(str);
                        // 如果字段包含表别名（表别名不为空），表别名必须是 SQL 中存在的
                        if (StrUtil.isNotBlank(alias) && !tableAliases.contains(alias)) {
                            continue;
                        }
                        // 如果字段的表别名是空的，from table 有别名，则给字段拼上
                        if (StrUtil.isBlank(alias) && StrUtil.isNotBlank(fromTableAlias)) {
                            str = fromTableAlias + DOT + str;
                        }
                        configs.add(new ColumnCfg(Op.IN, str, userGroups));
                    }
                }
            } break;
            case CUSTOM:
            default: {
                throw new IllegalArgumentException("The data scope is not supported! ");
            }
        }
        processPlainSelect(plainSelect, configs);
    }

    @Override
    public Object execute(String strategy, Object input, Object... arguments) {
        if ("processPlainSelect".equals(strategy)) {
            Context context = ((AbstractSecurityContext) SecurityUtil.getContext())
                    .getProperty(CONTEXT_KEY, Context.class);
            processPlainSelect((PlainSelect) input, context);
            return null;
        } else { throw new UnsupportedOperationException("The strategy is not supported! "); }
    }

    public static class Op {
        public static final String EQ = "EQ";
        public static final String IN = "IN";
        public static final String LIKE = "LIKE";
//        public static final String SUB_SELECT = "SUB_SELECT";
    }

    public static class ColumnCfg {
        private String columnName;
        private String operator;
        private Object value;

        public ColumnCfg(String operator, String columnName, Object value) {
            this.columnName = columnName;
            this.operator = operator;
            this.value = value;
        }

        public ColumnCfg() {

        }

        public String getColumnName() {

            return columnName;
        }

        public void setColumnName(String columnName) {

            this.columnName = columnName;
        }

        public String getOperator() {

            return operator;
        }

        public void setOperator(String operator) {

            this.operator = operator;
        }

        public Object getValue() {

            return value;
        }

        public void setValue(Object value) {

            this.value = value;
        }
    }

}
