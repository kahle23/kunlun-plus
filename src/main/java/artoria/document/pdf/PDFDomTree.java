package artoria.document.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.fit.pdfdom.BoxStyle;
import org.fit.pdfdom.PDFDomTreeConfig;
import org.fit.pdfdom.TextMetrics;
import org.fit.pdfdom.resource.ImageResource;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PDF content stripper.
 * @see org.fit.pdfdom.PDFDomTree
 * @author Kahle
 */
@Deprecated
public class PDFDomTree extends org.fit.pdfdom.PDFDomTree {

    public PDFDomTree() throws IOException, ParserConfigurationException {
        super();
        this.defaultStyle = "p{margin-top:0pt;margin-bottom:1pt;}";
    }

    public PDFDomTree(PDFDomTreeConfig config) throws IOException, ParserConfigurationException {
        this();
        if (config != null) {
            this.config = config;
        }
    }

    @Override
    protected String createFontFaces() {
        return "";
    }


    @Override
    protected Element createPageElement() {
//        String pstyle = "";
        PDRectangle layout = this.getCurrentMediaBox();
        if (layout != null) {
            float w = layout.getWidth();
            float h = layout.getHeight();
            int rot = this.pdpage.getRotation();
            if (rot == 90 || rot == 270) {
                float x = w;
                w = h;
                h = x;
            }

//            pstyle = "width:" + w + "pt" + ";" + "height:" + h + "pt" + ";";
//            pstyle = pstyle + "overflow:hidden;";

        }
//        else {
//            log.warn("No media box found");
//        }

        Element el = this.doc.createElement("div");
//        this.pagecnt++;
        el.setAttribute("id", "page_" + this.pagecnt++);
        el.setAttribute("class", "page");
//        el.setAttribute("style", pstyle);
        return el;
    }

    private BoxStyle lastStyle = null;
    private float lastFontSize = 0;
    private StringBuilder dataBuilder = new StringBuilder();

    @Override
    protected void startNewPage() {
        if (this.pagecnt != 0) {
            // paragraph end add br p
            Element p = this.doc.createElement("p");
            Element br = this.doc.createElement("br");
            p.appendChild(br);
            this.body.appendChild(p);
            // paragraph end add br p
        }
        super.startNewPage();
        this.lastStyle = null;
        System.out.println(">> startNewPage");
    }

    @Override
    protected void endDocument(PDDocument document) throws IOException {
        super.endDocument(document);
        if (this.dataBuilder.length() > 0) {
            this.curpage.appendChild(this.createTextElement(this.dataBuilder.toString(), -1));
            this.dataBuilder.setLength(0);
        }
    }

    @Override
    protected void renderText(String data, TextMetrics metrics) {
        float width = metrics.getWidth();
        if (this.lastStyle == null) {
            if (this.dataBuilder.length() > 0) {
                this.curpage.appendChild(this.createTextElement(this.dataBuilder.toString(), width));
                this.dataBuilder.setLength(0);
                // paragraph end add br p
                Element p = this.doc.createElement("p");
                Element br = this.doc.createElement("br");
                p.appendChild(br);
                this.curpage.appendChild(p);
                // paragraph end add br p
            }
            this.dataBuilder.append(data);
            System.out.println(this.curstyle.getTop() + ", " + this.curstyle.getFontSize() + ", " + this.curstyle.getLineHeight());
            System.out.println("top: " + this.curstyle.getTop() + " | " + (this.curstyle.getTop()) + " <= " + 0 + " >> " + data);
            // 这里也需要做 pendingImageResourceMap 判断
            this.lastStyle = this.curstyle;
            this.lastFontSize = this.curstyle.getFontSize();
            return;
        }

        float space = this.curstyle.getTop() - this.lastStyle.getTop() - this.lastStyle.getFontSize();
        System.out.println("top: " + this.curstyle.getTop() + ", FontSize: " + this.curstyle.getFontSize() + " | " + space + " <= " + this.lastStyle.getLineHeight() + " >> " + data);
        if (space <= this.curstyle.getLineHeight()) {
            this.dataBuilder.append(data);
        }
        else {
            this.curpage.appendChild(this.createTextElement(this.dataBuilder.toString(), width));
            this.dataBuilder.setLength(0);
            // paragraph end add br p
            Element p = this.doc.createElement("p");
            Element br = this.doc.createElement("br");
            p.appendChild(br);
            this.curpage.appendChild(p);
            // paragraph end add br p
            this.dataBuilder.append(data);
        }

        if (this.pendingImageResourceMap.size() > 0) {
            for (Map.Entry<Float, Element> entry : this.pendingImageResourceMap.entrySet()) {
                float lastTop = this.lastStyle != null ? this.lastStyle.getTop() : 0;
                float curTop = this.curstyle.getTop();
                Float key = entry.getKey();
                Element value = entry.getValue();
                if (value != null && key > lastTop && key <= curTop) {
                    this.curpage.appendChild(value);
                    // paragraph end add br p
                    Element p = this.doc.createElement("p");
                    Element br = this.doc.createElement("br");
                    p.appendChild(br);
                    this.curpage.appendChild(p);
                    // paragraph end add br p
                    this.pendingImageResourceMap.remove(key);
                }
            }
        }

        this.lastStyle = this.curstyle;
        this.lastFontSize = this.curstyle.getFontSize();
    }

    @Override
    protected Element createTextElement(float width) {

        throw new UnsupportedOperationException();
    }

    @Override
    protected Element createTextElement(String data, float width) {
        Element el = this.doc.createElement("p");
        this.textcnt++;
//        el.setAttribute("id", "p" + this.textcnt++);
//        el.setAttribute("class", "p");
//        String style = this.curstyle.toString();
//        style = style + "width:" + width + "pt" + ";";
        float fontSize = this.lastFontSize == 0 ? this.curstyle.getFontSize() : this.lastFontSize;
        String style = "text-align:left;text-indent:" + fontSize * 2 + "pt;";
        el.setAttribute("style", style);
        Text text = this.doc.createTextNode(data);
        el.appendChild(text);
        return el;
    }

    private Map<Float, Element> pendingImageResourceMap = new LinkedHashMap<Float, Element>();

    @Override
    protected Element createImageElement(float x, float y, float width, float height, ImageResource resource) throws IOException {
//        StringBuilder pstyle = new StringBuilder("position:absolute;");
        StringBuilder pstyle = new StringBuilder();
//        pstyle.append("left:").append(x).append("pt").append(';');
//        pstyle.append("top:").append(y).append("pt").append(';');
        pstyle.append("width:").append(width).append("pt").append(';');
        pstyle.append("height:").append(height).append("pt").append(';');
        System.out.println("img top: " + y + ", height: " + height);
        Element p = this.doc.createElement("p");
        p.setAttribute("style", "text-align:center;");

        Element el = this.doc.createElement("img");

        p.appendChild(el);
        el.setAttribute("style", pstyle.toString());
        el.setAttribute("width", width + "");
        el.setAttribute("height", height + "");
        String imgSrc = this.config.getImageHandler().handleResource(resource);
        if (!this.disableImageData && !imgSrc.isEmpty()) {
            el.setAttribute("src", imgSrc);
        } else {
            el.setAttribute("src", "");
        }

        this.pendingImageResourceMap.put(y + height, p);

        return this.doc.createElement("span");
    }

}
