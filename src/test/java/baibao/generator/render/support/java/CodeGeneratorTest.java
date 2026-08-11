/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.generator.render.support.java;

import kunlun.db.jdbc.support.JdbcTableLoader;
import kunlun.db.jdbc.support.function.MysqlTableCommentConsumer;
import kunlun.generator.render.support.java.JavaCodeGenConfig;
import kunlun.generator.render.support.java.JavaCodeGenerator;
import kunlun.io.file.support.SimpleFileLoader;
import kunlun.renderer.support.VelocityTextRenderer;
import org.junit.Ignore;
import org.junit.Test;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;

/**
 * The java code generator Test.
 * @author Kahle
 */
@Ignore
public class CodeGeneratorTest {

    @Test
    public void generate() {
        // The module name.
        String module = "test";
        // Jdbc table loader config.
        JdbcTableLoader.Config loaderConfig = new JdbcTableLoader.Config();
        loaderConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        loaderConfig.setUrl("jdbc:mysql://127.0.0.1:3306/demo?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8");
        loaderConfig.setUsername("root");
        loaderConfig.setPassword("root");
        loaderConfig.setCatalog("demo");
        //loaderConfig.getExcludedTables().add("t_test");
        loaderConfig.getReservedTables().addAll(asList("t_test", "t_test1"));
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
        genConfig.setXmlBaseOutputPath("src\\test\\resources\\mapper" + module);
        genConfig.setBasePackageName("kunlun.generator.out." + module);
        genConfig.getCustomAttributes().put("author", "Kahle");
        genConfig.getCustomAttributes().put("useLombok", true);
        // Modify template configs.
        String javaSuffix = ".java", vueSuffix = ".vue", name, tail, pkgName;
        // [smpResult]
        name = "smpResult"; tail = "SmpResult"; pkgName = "pojo.result";
        genConfig.getTemplateConfigs().put(name, new JavaCodeGenConfig.TemplateConfig(name, tail, javaSuffix, pkgName, FALSE));
        // [param]
        genConfig.getTemplateConfigs().remove("param");
        // [addParam]
        name = "addParam"; tail = "AddParam"; pkgName = "pojo.param";
        genConfig.getTemplateConfigs().put(name, new JavaCodeGenConfig.TemplateConfig(name, tail, javaSuffix, pkgName, FALSE));
        // [editParam]
        name = "editParam"; tail = "EditParam"; pkgName = "pojo.param";
        genConfig.getTemplateConfigs().put(name, new JavaCodeGenConfig.TemplateConfig(name, tail, javaSuffix, pkgName, FALSE));
        // [frontListVue]
        name = "frontListVue"; tail = "List"; pkgName = "front";
        genConfig.getTemplateConfigs().put(name, new JavaCodeGenConfig.TemplateConfig(name, tail, vueSuffix, pkgName, TRUE));
        // [frontUpdateVue]
        name = "frontUpdateVue"; tail = "Update"; pkgName = "front";
        genConfig.getTemplateConfigs().put(name, new JavaCodeGenConfig.TemplateConfig(name, tail, vueSuffix, pkgName, TRUE));
        new JavaCodeGenerator().generate(genConfig);
    }

}
