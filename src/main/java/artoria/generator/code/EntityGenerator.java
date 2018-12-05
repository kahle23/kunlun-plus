package artoria.generator.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Entity generator.
 * @author Kahle
 */
public class EntityGenerator extends BaseCodeGenerator {
    private static Logger log = LoggerFactory.getLogger(EntityGenerator.class);

    @Override
    protected String takeFileName(TableInfo tableInfo) {

        return tableInfo.getEntityClassName() + ".java";
    }

    @Override
    protected String handleExistedFile(String generatedStr, File existedFile) {
        log.info("The file \"{}\" already exists, it will be covered. ", existedFile.getName());
        return generatedStr;
    }

}
