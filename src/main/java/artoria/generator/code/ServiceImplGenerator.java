package artoria.generator.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Service implements generator.
 * @author Kahle
 */
public class ServiceImplGenerator extends BaseCodeGenerator {
    private static Logger log = LoggerFactory.getLogger(ServiceImplGenerator.class);
    private static final String START_COVERED_MARK = "/* (Start) This will be covered, please do not modify. */";
    private static final String END_COVERED_MARK = "/* (End) This will be covered, please do not modify. */";

    @Override
    protected String takeFileName(TableInfo tableInfo) {

        return tableInfo.getServiceImplClassName() + ".java";
    }

    @Override
    protected String handleExistedFile(String generatedStr, File existedFile) {
        return this.replaceExistedFile(existedFile
                , generatedStr
                , START_COVERED_MARK
                , END_COVERED_MARK);
    }

}
