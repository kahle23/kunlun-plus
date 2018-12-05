package artoria.config;

import artoria.util.ClassUtils;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * PageHelper auto configuration.
 * @author Kahle
 */
@Configuration
@ConditionalOnBean(SqlSessionFactory.class)
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class PageHelperAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(PageHelperAutoConfiguration.class);
    private static final String PAGE_HELPER_CLASS = "com.github.pagehelper.PageHelper";
    private final SqlSessionFactory sqlSessionFactory;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public PageHelperAutoConfiguration(SqlSessionFactory sqlSessionFactory) {

        this.sqlSessionFactory = sqlSessionFactory;
    }

    @PostConstruct
    public void configuration() {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        if (!ClassUtils.isPresent(PAGE_HELPER_CLASS, classLoader)) {
            log.warn("Can not found \"{}\". ", PAGE_HELPER_CLASS);
            return;
        }
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("pageSizeZero", "true");
        pageHelper.setProperties(properties);
        this.sqlSessionFactory.getConfiguration().addInterceptor(pageHelper);
        log.info("PageHelper configuration succeeded. ");
    }

}
