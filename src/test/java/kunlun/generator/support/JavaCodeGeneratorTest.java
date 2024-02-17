/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.generator.support;

import kunlun.generator.JavaCodeGenerator;
import kunlun.jdbc.DatabaseClient;
import kunlun.jdbc.SimpleDataSource;
import kunlun.renderer.support.VelocityTextRenderer;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class JavaCodeGeneratorTest {
    private static DatabaseClient databaseClient = new DatabaseClient(new SimpleDataSource());

    @Test
    public void test1() {
        JavaCodeGenerator generator = new JavaCodeGenerator().newCreator()
                .setDatabaseClient(databaseClient)
//                .setBaseTemplatePath("classpath:templates/generator/java/custom")
                .setBaseOutputPath("src\\test\\java")
                .setBasePackageName("kunlun.generator.out")
                .setTextRenderer(new VelocityTextRenderer())
                .addRemovedTableNamePrefixes("t_")
//                .addExcludedTables("t_15_user")
//                .addReservedTables("t_user")
                ;
        generator.addAttribute("author", "Kahle");
        generator.generate();
    }

}
