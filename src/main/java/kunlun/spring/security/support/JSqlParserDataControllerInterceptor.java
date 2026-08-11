package kunlun.spring.security.support;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import kunlun.security.SecurityUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class JSqlParserDataControllerInterceptor extends JsqlParserSupport implements InnerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(JSqlParserDataControllerInterceptor.class);

    @Override
    public void beforeQuery(Executor executor,
                            MappedStatement ms,
                            Object parameter,
                            RowBounds rowBounds,
                            ResultHandler resultHandler,
                            BoundSql boundSql) throws SQLException {
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        mpBs.sql(this.parserSingle(mpBs.sql(), ms.getId()));
    }

    @Override
    protected void processSelect(Select select, int index, String sql, Object obj) {
        SelectBody selectBody = select.getSelectBody();
        //只对简单查询(包含子查询的语句只对最外层做处理)和union语句处理
        if (selectBody instanceof PlainSelect) {
            PlainSelect plainSelect = (PlainSelect) selectBody;
            SecurityUtil.getDataController()
                    .execute("processPlainSelect", plainSelect, new Object[]{});
        } else if (selectBody instanceof SetOperationList) {
            // 针对 UNION、UNION ALL、INTERSECT、EXCEPT 等
            List<SelectBody> selects = ((SetOperationList) selectBody).getSelects();
            for (SelectBody body : selects) {
                SecurityUtil.getDataController()
                        .execute("processPlainSelect", (PlainSelect) body, new Object[]{});
            }
        }
    }

}
