package artoria.generator;

import artoria.generator.java.JavaGenerateHelper;
import artoria.jdbc.DatabaseClient;
import artoria.jdbc.SimpleDataSource;
import artoria.template.VelocityRenderer;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class GeneratorTest {
    private static DatabaseClient databaseClient = new DatabaseClient(new SimpleDataSource());

    @Test
    public void test1() {
        JavaGenerateHelper helper = new JavaGenerateHelper();
        helper.createGenerator()
                .setBaseTemplatePath("classpath:generator\\java")
                .setBaseOutputPath("src\\test\\java")
                .setBasePackageName("artoria.generator.out")
                .setDatabaseClient(databaseClient)
                .setRenderer(new VelocityRenderer())
                .addRemovedTableNamePrefixes("t_")
                .addExcludedTables("t_15_user")
                .addReservedTables("t_user");
        helper.addAttribute("author", "Kahle");
        helper.generate();
    }

}
