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
        JavaGenerateHelper helper = new JavaGenerateHelper();
        helper.createGenerator("mapper_xml,mapper_java,entity_java,serviceImpl_java," +
                "service_java,controller_java,dto_java,vo_java")
//                .setBaseTemplatePath("classpath:generator")
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
