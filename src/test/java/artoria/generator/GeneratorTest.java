package artoria.generator;

import artoria.generator.java.JavaGenerateHelper;
import artoria.jdbc.DatabaseClient;
import artoria.jdbc.SimpleDataSource;
import artoria.template.VelocityRenderer;
import org.junit.Before;
import org.junit.Ignore;

@Ignore
public class GeneratorTest {
    private static DatabaseClient databaseClient = new DatabaseClient(new SimpleDataSource());

    @Before
    public void init() {
        JavaGenerateHelper helper = new JavaGenerateHelper();
        helper.createGenerator()
                .setBaseTemplatePath("classpath:java")
                .setBaseOutputPath("src\\test\\java")
                .setBasePackageName("artoria.extend.generator.out")
                .setDatabaseClient(databaseClient)
                .setRenderer(new VelocityRenderer())
                .addRemovedTableNamePrefixes("t_")
                .addExcludedTables("t_15_user")
                .addReservedTables("t_user")
                .addAttribute("author", "Kahle");
        helper.generate();
    }

}
