/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.generator.render.support.java;

import kunlun.db.jdbc.support.JdbcTableLoader;
import kunlun.db.jdbc.support.function.MysqlTableCommentConsumer;
import kunlun.io.file.support.SimpleFileLoader;
import kunlun.renderer.support.VelocityTextRenderer;
import org.junit.Ignore;
import org.junit.Test;

import static java.util.Arrays.asList;

/**
 * The java code generator Test.
 * @author Kahle
 */
@Ignore
public class CodeGeneratorTest {

    @Test
    public void generate() {
        // Jdbc table loader config.
        JdbcTableLoader.Config loaderConfig = new JdbcTableLoader.Config();
        loaderConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        loaderConfig.setUrl("jdbc:mysql://127.0.0.1:3306/demo?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8");
        loaderConfig.setUsername("root");
        loaderConfig.setPassword("root");
        loaderConfig.setCatalog("demo");
        //loaderConfig.getExcludedTables().add("t_test");
        loaderConfig.getReservedTables().addAll(asList("account_book_records", "c_messages"));
        loaderConfig.getPostConsumers().add(new MysqlTableCommentConsumer());
        // Java code generator config.
        JavaCodeGenConfig genConfig = new JavaCodeGenConfig();
        genConfig.setTableLoader(new JdbcTableLoader());
        genConfig.setTableLoaderConfig(loaderConfig);
        genConfig.getRemovedTableNamePrefixes().add("t_");
        genConfig.setFileLoader(new SimpleFileLoader());
//        genConfig.setFileLoader(new JarFileLoader());
        genConfig.setRenderer(new VelocityTextRenderer());
        genConfig.setBaseTemplatePath("classpath:templates/generator/spring-boot-mybatis-plus-standard");
        genConfig.setBaseOutputPath("src\\test\\java");
        genConfig.setXmlBaseOutputPath("src\\test\\resources\\mapper");
        genConfig.setBasePackageName("kunlun.generator.out");
        genConfig.getCustomAttributes().put("author", "Kahle");
        genConfig.getCustomAttributes().put("useLombok", true);
        new JavaCodeGenerator().generate(genConfig);
    }

}
