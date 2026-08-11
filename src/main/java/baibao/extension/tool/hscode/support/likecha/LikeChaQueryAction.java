package baibao.extension.tool.hscode.support.likecha;

import baibao.extension.tool.hscode.AbstractHsCodeQueryAction;
import baibao.extension.tool.hscode.HsData;
import baibao.extension.tool.hscode.HsQuery;
import kunlun.common.Page;
import kunlun.net.http.HttpMethod;
import kunlun.net.http.HttpUtil;
import kunlun.net.http.support.SimpleRequest;
import kunlun.util.CollUtil;
import kunlun.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static baibao.util.JsoupUtil.getHtml;
import static baibao.util.JsoupUtil.getText;
import static cn.hutool.core.net.URLEncodeUtil.encode;
import static kunlun.common.constant.Numbers.*;
import static kunlun.common.constant.Symbols.*;
import static kunlun.util.Assert.isFalse;
import static kunlun.util.Assert.notBlank;
import static kunlun.util.CollUtil.getFirst;
import static kunlun.util.CollUtil.isEmpty;
import static kunlun.util.ObjUtil.ifNull;
import static kunlun.util.StrUtil.*;

/**
 * 立刻查 海关编码的 Action.
 * @see <a href="http://www.likecha.com/">立刻查 – 海关编码</a>
 * @author Kahle
 */
@Slf4j
public class LikeChaQueryAction extends AbstractHsCodeQueryAction {

    protected String processCode(String code) {
        if (isBlank(code)) { return code; }
        int indexOf = code.indexOf("<br>");
        code = indexOf > ZERO ? code.substring(ZERO, indexOf) : code;
        code = code.replace(DOT, EMPTY_STRING);
        return code;
    }

    protected void processDeclarationElements(HsData hsData, Elements lis) {
        List<String> declarationElements = new ArrayList<>();
        hsData.setDeclarationElements(declarationElements);
        String text;
        for (Element li : lis) {
            if (li == null || isBlank(text = li.text())) {
                continue;
            }
            text = text.trim();
            int indexOf = text.indexOf(":"), idx;
            if (indexOf > ZERO && (idx = indexOf + ONE) < text.length()) {
                text = text.substring(idx);
            }
            if (text.endsWith("；")) {
                text = text.replace("；", EMPTY_STRING);
            }
            declarationElements.add(text);
        }
        log.debug("{}", declarationElements);
    }

    protected void processBaseInfo(HsData hsData, Elements tds) {
        // 提取关键数据
        String code = processCode(getHtml(tds, ZERO)), name = getText(tds, ONE);
        String basicTaxRateTxt = getText(tds, THREE);
        String vatRateTxt = getText(tds, FIVE);
        String unit = getText(tds, SEVEN);
        // 处理单位信息
        String firstLegalUnit = null, secondLegalUnit = null;
        if (isNotBlank(unit)) {
            String[] split = unit.trim().split(SLASH);
            firstLegalUnit = split.length >= ONE ? split[ZERO] : null;
            secondLegalUnit = split.length >= TWO ? split[ONE] : null;
        }
        // 处理税率信息
        BigDecimal basicTaxRate = null, vatRate = null, oneHundred = new BigDecimal(ONE_HUNDRED);
        if (isNumeric(basicTaxRateTxt)) {
            basicTaxRate = new BigDecimal(basicTaxRateTxt).divide(oneHundred, SIX, RoundingMode.HALF_UP);
        }
        if (isNumeric(vatRateTxt)) {
            vatRate = new BigDecimal(vatRateTxt).divide(oneHundred, SIX, RoundingMode.HALF_UP);
        }
        // 填充数据
        log.debug("{}, {}, {}, {}, {}, {}", code, name, basicTaxRate, vatRate, firstLegalUnit, secondLegalUnit);
        hsData.setName(name);
        hsData.setCode(code);
        hsData.setFirstLegalUnit(firstLegalUnit);
        hsData.setSecondLegalUnit(secondLegalUnit);
        hsData.setBasicTaxRate(basicTaxRate);
        hsData.setVatRate(vatRate);
    }

    @SneakyThrows(Exception.class)
    @Override
    protected Page<HsData> pagingQuery(HsQuery hsQuery) {
        // 构建数据结果对象
        Page<HsData> page = Page.of(new ArrayList<>());
        // 构建URL地址
        String nameQry = isNotBlank(hsQuery.getName()) ? encode(encode(hsQuery.getName())) : EMPTY_STRING;
        String codeQry = isNotBlank(hsQuery.getCode()) ? hsQuery.getCode() : EMPTY_STRING;
        isFalse(StrUtil.isBlank(nameQry) && StrUtil.isBlank(codeQry));
        Integer pageNum = ifNull(hsQuery.getPageNum(), ONE);
        page.setPageNum(pageNum);
        String url = String.format("http://www.likecha.com/tools/hscode.html?code=%s&name=%s&pageIndex=%s"
                , codeQry, nameQry, pageNum);
        // 构建请求，调用接口
        SimpleRequest request = SimpleRequest.of(HttpMethod.GET, url);
        request.addHeader(USER_AGENT_KEY, USER_AGENT_VAL);
        request.setValidateCertificate(false);
        String bodyAsString = HttpUtil.execute(request).getBodyAsString();
        log.debug("Http response body: {}", bodyAsString);
        if (StrUtil.isBlank(bodyAsString)) { return page; }
        // 解析 HTML 文档
        Document document = Jsoup.parse(bodyAsString);
        Elements tbodys = document.getElementsByTag("tbody");
        if (CollUtil.isEmpty(tbodys)) { return page; }
        // 构建数据结果对象
        Element tbody = tbodys.get(TWO);
        // 获取所有的 tr 行数据
        if (tbody == null) { return page; }
        Elements trs = tbody.getElementsByTag("tr"), tds;
        if (CollUtil.isEmpty(trs)) { return page; }
        // 遍历 tr 行数据
        for (Element tr : trs) {
            // 获取 tr 的 td 数据
            if (tr == null || isEmpty(tds = tr.getElementsByTag("td"))) {
                continue;
            }
            String code = processCode(getText(tds, ZERO)), name = getText(tds, ONE);
            String spec = getText(tds, TWO), mfnRate = getText(tds, THREE);
            String taxRefundRate = getText(tds, FOUR);
            log.debug("{}, {}, {}, {}, {}", code, name, spec, mfnRate, taxRefundRate);
            // 构建数据对象
            HsData hsData = new HsData();
            hsData.setCode(code);
            hsData.setName(name);
            hsData.getOthers().put("商品规格", spec);
            hsData.getOthers().put("最惠国税率", mfnRate);
            hsData.getOthers().put("退税率", taxRefundRate);
            page.getData().add(hsData);
        }
        // 结束
        page.setPageSize(page.getData().size());
        return page;
    }

    @Override
    protected HsData detailQuery(String hsCode) {
        // 构建URL地址
        String url = String.format("http://www.likecha.com/tools/hscode/%s.html", notBlank(hsCode));
        // 构建请求，调用接口
        SimpleRequest request = SimpleRequest.of(HttpMethod.GET, url);
        request.addHeader(USER_AGENT_KEY, USER_AGENT_VAL);
        String bodyAsString = HttpUtil.execute(request).getBodyAsString();
        log.debug("Http response body: {}", bodyAsString);
        if (isBlank(bodyAsString)) { return null; }
        // 构建结果对象，并解析 HTML 文档
        HsData hsData = new HsData();
        Document document = Jsoup.parse(bodyAsString);
        // 提取基础信息，并处理
        Elements tbodys = document.getElementsByTag("tbody");
        Element tbody, tr, ul; Elements trs, tds, lis;
        if ((tbody = getFirst(tbodys)) == null ||
                isEmpty(trs = tbody.getElementsByTag("tr")) ||
                trs.size() < TWO || (tr = trs.get(ONE)) == null ||
                isEmpty(tds = tr.getElementsByTag("td"))) {
            return null;
        }
        processBaseInfo(hsData, tds);
        // 提取申报要素，并处理
        Elements ul1s = document.getElementsByClass("inforul1");
        if ((ul = getFirst(ul1s)) == null ||
                isEmpty(lis = ul.getElementsByTag("li"))) {
            return null;
        }
        processDeclarationElements(hsData, lis);
        // 结束
        return hsData;
    }

}
