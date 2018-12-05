package artoria.document.pdf;

import artoria.util.Assert;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTreeConfig;
import org.fit.pdfdom.resource.HtmlResourceHandler;
import artoria.document.common.AbstractConverter;
import artoria.document.common.ImageExtractor;
import artoria.document.common.URIResolver;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * PDF converter.
 * @author Kahle
 */
public class PDFConverter extends AbstractConverter {

    @Override
    public void convertToHtml(InputStream fileInputStream, OutputStream htmlOutputStream
            , String charset, URIResolver resolver, ImageExtractor extractor) throws Exception {
        Assert.notNull(fileInputStream, "Parameter \"fileInputStream\" must not null. ");
        Assert.notNull(htmlOutputStream, "Parameter \"htmlOutputStream\" must not null. ");
        Assert.notNull(charset, "Parameter \"charset\" must not blank. ");
        Assert.notNull(resolver, "Parameter \"resolver\" must not null. ");
        Assert.notNull(extractor, "Parameter \"extractor\" must not null. ");
        PDDocument document = PDDocument.load(fileInputStream);
        PDFDomTreeConfig config = PDFDomTreeConfig.createDefaultConfig();
        HtmlResourceHandler imageHandler = new HtmlResourceHandlerAdapter(resolver, extractor);
        config.setImageHandler(imageHandler);
        PDFDomTree pdfDomTree = new PDFDomTree(config);
        pdfDomTree.writeText(document, new OutputStreamWriter(htmlOutputStream, charset));
    }

}
