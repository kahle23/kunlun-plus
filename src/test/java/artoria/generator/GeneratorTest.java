package artoria.generator;

import artoria.generator.code.CodeGenerateManager;
import artoria.jdbc.DatabaseClient;
import artoria.jdbc.SimpleDataSource;
import artoria.template.VelocityRenderer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class GeneratorTest {
    private static DatabaseClient databaseClient = new DatabaseClient(new SimpleDataSource());
    private CodeGenerateManager manager;

    @Before
    public void init() {
        manager = new CodeGenerateManager();
        manager.addAttribute("author", "Kahle");
        manager.setBasePackageName("artoria.extend.generator.out");
        manager.setBaseOutputPath("src\\test\\java");
        manager.setRenderer(new VelocityRenderer());
        manager.setDatabaseClient(databaseClient);
        manager.addRemovedTableNamePrefixes("t_");
        manager.addExcludedTables("t_15_user");
        manager.addReservedTables("t_user");
        manager.initialize();
    }

    @Test
    public void generate() {
//        manager.generateController();
//        manager.generateService();
//        manager.generateDomain();
//        manager.generateMapper();
        manager.generate();
    }

}
