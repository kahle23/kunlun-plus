package artoria.generator.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Mapper xml generator.
 * @author Kahle
 */
public class MapperXmlGenerator extends BaseCodeGenerator {
    private static Logger log = LoggerFactory.getLogger(MapperXmlGenerator.class);
    private static final String START_COVERED_MARK = "<!-- **** (Start) This will be covered, please do not modify. **** -->";
    private static final String END_COVERED_MARK = "<!-- **** (End) This will be covered, please do not modify. **** -->";

    @Override
    protected String takeFileName(TableInfo tableInfo) {

        return tableInfo.getMapperClassName() + ".xml";
    }

    @Override
    protected String handleExistedFile(String generatedStr, File existedFile) {
        return this.replaceExistedFile(existedFile
                , generatedStr
                , START_COVERED_MARK
                , END_COVERED_MARK);
    }

}
