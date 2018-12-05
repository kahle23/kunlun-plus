package artoria.document.word;

import artoria.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import artoria.document.common.AbstractConverter;

import java.io.*;

@Ignore
public class DocxFileConverterTest {
    private AbstractConverter converter = new DocxFileConverter();

    @Test
    public void test1() throws IOException {
        InputStream in = new FileInputStream("E:\\Temp\\Test\\Test.docx");
        OutputStream out = new FileOutputStream("E:\\Temp\\Test\\docx_test.html");
        converter.convertToHtml(in, out, "utf-8"
                , "./", new File("E:\\Temp\\Test"));
        IOUtils.closeQuietly(out);
        IOUtils.closeQuietly(in);
    }

}
