package artoria.generator.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * VO generator.
 * @author Kahle
 */
public class VoGenerator extends BaseCodeGenerator {
    private static Logger log = LoggerFactory.getLogger(VoGenerator.class);

    @Override
    protected String takeFileName(TableInfo tableInfo) {

        return tableInfo.getVoClassName() + ".java";
    }

    @Override
    protected String handleExistedFile(String generatedStr, File existedFile) {
        log.info("The file \"{}\" already exists, it will be skip. ", existedFile.getName());
        return null;
    }

}
