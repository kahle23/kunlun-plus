package artoria.generator;

import artoria.exception.ExceptionUtils;
import artoria.file.Prop;
import artoria.jdbc.DatabaseClient;
import artoria.jdbc.SimpleDataSource;
import artoria.template.VelocityRenderer;
import org.junit.Ignore;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.Properties;

@Ignore
public class JavaGenerateHelperTest {
    private static DatabaseClient databaseClient;

    static {
        try {
            Prop prop = new Prop();
            prop.readFromClasspath("jdbc.properties");
            Properties properties = prop.getProperties();
            DataSource dataSource = new SimpleDataSource(properties);
            databaseClient = new DatabaseClient(dataSource);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Test
    public void test1() {
        JavaGenerateHelper helper = new JavaGenerateHelper().createGenerator()
                .setDatabaseClient(databaseClient)
//                .setBaseTemplatePath("classpath:templates/generator/custom")
                .setBaseOutputPath("src\\test\\java")
                .setBasePackageName("artoria.generator.out")
                .setRenderer(new VelocityRenderer())
                .addRemovedTableNamePrefixes("t_")
//                .addExcludedTables("t_15_user")
//                .addReservedTables("t_user")
                ;
        helper.put("author", "Kahle");
        helper.generate();
    }

}
