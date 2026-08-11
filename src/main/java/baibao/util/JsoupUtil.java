package baibao.util;

import org.jsoup.nodes.Element;

import java.util.List;

/**
 * JsoupUtil.
 * @author Kahle
 */
public class JsoupUtil {

    public static String getHtml(List<Element> list, int index) {

        return list.get(index) != null ? list.get(index).html() : null;
    }

    public static String getText(List<Element> list, int index) {

        return list.get(index) != null ? list.get(index).text() : null;
    }

}
