/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.util;

import com.github.pagehelper.PageHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * PageHelper auto configuration.
 * @author Kahle
 */
@Deprecated
@Configuration
@ConditionalOnBean(SqlSessionFactory.class)
@ConditionalOnClass(name = {"com.github.pagehelper.PageHelper"})
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class PageHelperAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(PageHelperAutoConfiguration.class);
    private final SqlSessionFactory sqlSessionFactory;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public PageHelperAutoConfiguration(SqlSessionFactory sqlSessionFactory) {

        this.sqlSessionFactory = sqlSessionFactory;
    }

    @PostConstruct
    public void configuration() {
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("pageSizeZero", "true");
        pageHelper.setProperties(properties);
        this.sqlSessionFactory.getConfiguration().addInterceptor(pageHelper);
        log.info("The page helper was initialized success. ");
    }

}
