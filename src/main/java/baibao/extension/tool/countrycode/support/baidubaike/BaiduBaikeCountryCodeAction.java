package baibao.extension.tool.countrycode.support.baidubaike;

import baibao.extension.tool.countrycode.AbstractCountryCodeAction;
import baibao.extension.tool.countrycode.CountryCode;
import baibao.extension.tool.countrycode.CountryCodeQuery;
import kunlun.net.http.HttpMethod;
import kunlun.net.http.HttpUtil;
import kunlun.net.http.support.SimpleRequest;
import kunlun.util.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static kunlun.common.constant.Numbers.ONE;
import static kunlun.common.constant.Numbers.ZERO;
import static kunlun.common.constant.Symbols.EMPTY_STRING;
import static kunlun.common.constant.Symbols.MINUS;

/**
 * 百度百科 ISO 3166-1，国家/地区编码
 * @see <a href="https://baike.baidu.com/item/ISO%203166-1/5269555#1">ISO 3166-1 正式代码列表</>
 * @author Kahle
 */
@Slf4j
public class BaiduBaikeCountryCodeAction extends AbstractCountryCodeAction {
    private static final String URL = "https://baike.baidu.com/item/ISO%203166-1/5269555";

    @Override
    protected List<CountryCode> mainQuery(CountryCodeQuery countryCodeQuery) {
        // 构建请求，调用接口
        SimpleRequest request = SimpleRequest.of(HttpMethod.GET, URL);
        String bodyAsString = HttpUtil.execute(request).getBodyAsString();
        log.debug("Http response body: {}", bodyAsString);
        // Html 解析，提取 tbody 中的 tr
        Document document = Jsoup.parse(bodyAsString);
        Element tbody = CollUtil.getFirst(document.getElementsByClass("tableBody_sNwsk"));
        if (tbody == null) { return emptyList(); }
        Elements trs = tbody.getElementsByTag("tr");
        if (CollUtil.isEmpty(trs)) { return emptyList(); }
        // 声明结果对象
        List<CountryCode> result = new ArrayList<>();
        boolean isFirst = true;
        // 循环 tr，填充结果对象
        for (Element tr : trs) {
            if (isFirst) { isFirst = false; continue; }
            // 从 tr 中提取 td，忽略 td 特别少的行
            Elements tds = tr.getElementsByTag("td");
            if (tds.size() <= ONE) { continue; }
            // 循环 td，构建 CountryCode 对象
            CountryCode countryCode = new CountryCode(); int tdIdx = ZERO;
            for (Element td : tds) {
                // td 判空并且判断长度，tdText 预处理
                if (td == null || tdIdx >= 6) { continue; }
                String tdText = td.text();
                if (MINUS.equals(tdText)) { tdText = EMPTY_STRING; }
                // 0 中文简称，1 英文简称，2 英文全称，3 两字母代码，4 三字母代码，5 数字代码
                switch (tdIdx) {
                    case 0: { countryCode.setNameZh(tdText); } break;
                    case 1: { countryCode.setNameEn(tdText); } break;
                    case 2: {
                        if (!"209".equals(td.attr("width"))) {
                            countryCode.setFullNameEn(EMPTY_STRING); tdIdx++;
                            countryCode.setAlphaCode2(tdText);
                        } else { countryCode.setFullNameEn(tdText); }
                    } break;
                    case 3: { countryCode.setAlphaCode2(tdText); } break;
                    case 4: { countryCode.setAlphaCode3(tdText); } break;
                    case 5: { countryCode.setNumericCode3(tdText); } break;
                    default: break;
                }
                // 索引自增
                tdIdx++;
            }
            result.add(countryCode);
        }
        // 结束
        return result;
    }
}
