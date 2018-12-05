package artoria.document.word;

import artoria.util.Assert;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.w3c.dom.Document;
import artoria.document.common.AbstractConverter;
import artoria.document.common.ImageExtractor;
import artoria.document.common.URIResolver;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Doc file converter.
 * @author Kahle
 */
public class DocFileConverter extends AbstractConverter {
    private static final String HTML = "html";
    private static final String YES = "yes";

    @Override
    public void convertToHtml(InputStream fileInputStream, OutputStream htmlOutputStream
            , String charset, URIResolver resolver, ImageExtractor extractor) throws Exception {
        Assert.notNull(fileInputStream, "Parameter \"fileInputStream\" must not null. ");
        Assert.notNull(htmlOutputStream, "Parameter \"htmlOutputStream\" must not null. ");
        Assert.notNull(charset, "Parameter \"charset\" must not blank. ");
        Assert.notNull(resolver, "Parameter \"resolver\" must not null. ");
        Assert.notNull(extractor, "Parameter \"extractor\" must not null. ");
        HWPFDocument wordDocument = new HWPFDocument(fileInputStream);
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        wordToHtmlConverter.setPicturesManager(new PicturesManagerAdapter(resolver, extractor));
        wordToHtmlConverter.processDocument(wordDocument);
        Document htmlDocument = wordToHtmlConverter.getDocument();
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(htmlOutputStream);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, charset);
        serializer.setOutputProperty(OutputKeys.INDENT, YES);
        serializer.setOutputProperty(OutputKeys.METHOD, HTML);
        serializer.transform(domSource, streamResult);
    }

}
