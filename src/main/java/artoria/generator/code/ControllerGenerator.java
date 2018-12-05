package artoria.generator.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Controller generator.
 * @author Kahle
 */
public class ControllerGenerator extends BaseCodeGenerator {
    private static Logger log = LoggerFactory.getLogger(ControllerGenerator.class);
    private static final String START_COVERED_MARK = "/* (Start) This will be covered, please do not modify. */";
    private static final String END_COVERED_MARK = "/* (End) This will be covered, please do not modify. */";

    @Override
    protected String takeFileName(TableInfo tableInfo) {

        return tableInfo.getControllerClassName() + ".java";
    }

    @Override
    protected String handleExistedFile(String generatedStr, File existedFile) {
        return this.replaceExistedFile(existedFile
                , generatedStr
                , START_COVERED_MARK
                , END_COVERED_MARK);
    }

}
