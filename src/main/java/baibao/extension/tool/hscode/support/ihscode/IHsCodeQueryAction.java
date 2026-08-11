package baibao.extension.tool.hscode.support.ihscode;

import baibao.extension.tool.hscode.AbstractHsCodeQueryAction;
import baibao.extension.tool.hscode.HsData;
import baibao.extension.tool.hscode.HsQuery;
import cn.hutool.core.util.URLUtil;
import kunlun.common.Page;
import kunlun.net.http.HttpMethod;
import kunlun.net.http.HttpUtil;
import kunlun.net.http.support.SimpleRequest;
import kunlun.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static baibao.util.JsoupUtil.getText;
import static kunlun.common.constant.Numbers.*;
import static kunlun.common.constant.Symbols.EMPTY_STRING;
import static kunlun.util.Assert.notBlank;
import static kunlun.util.CollUtil.isEmpty;
import static kunlun.util.ObjUtil.ifNull;

/**
 * HS海关编码查询的 Action.
 * @see <a href="https://www.ihscode.com/">HS海关编码查询</a>
 * @author Kahle
 */
@Slf4j
public class IHsCodeQueryAction extends AbstractHsCodeQueryAction {

    protected List<String> parseDeclarationElements(String text) {
        List<String> result = new ArrayList<>();
        String[] split = text.split("<br>");
        int idx = ONE;
        for (String str : split) {
            if (StrUtil.isBlank(str)) { continue; }
            str = str.trim();
            if (str.startsWith(String.valueOf(idx))) {
                str = str.substring(String.valueOf(idx).length());
            }
            result.add(str);
            idx++;
        }
        return result;
    }

    protected void fillBaseInfo(HsData hsData, String key, String val) {
        // 将原始数据填充到 others 中
        hsData.getOthers().put(key, val);
        // 根据 key 设置 val 到对应的字段
        if ("商品编码".equals(key)) {
            hsData.setCode(val);
        } else if ("商品名称".equals(key)) {
            hsData.setName(val);
        } else if ("第一法定单位".equals(key)) {
            hsData.setFirstLegalUnit(val);
        } else if ("第二法定单位".equals(key)) {
            hsData.setSecondLegalUnit(val);
        } else if ("申报要素".equals(key)) {
            hsData.setDeclarationElements(parseDeclarationElements(val));
        }
    }

    protected void fillTaxRate(HsData hsData, String key, String val) {
        // 将原始数据填充到 others 中
        hsData.getOthers().put(key, val);
        // 根据 key 设置 val 到对应的字段
        if ("普通税率".equals(key)) {
            hsData.setBasicTaxRate(StrUtil.isNumeric(val) ? new BigDecimal(val) : null);
        } else if ("增值税率".equals(key)) {
            hsData.setVatRate(StrUtil.isNumeric(val) ? new BigDecimal(val) : null);
        }
    }

    @Override
    protected Page<HsData> pagingQuery(HsQuery hsQuery) {
        // 构建数据结果对象
        Page<HsData> page = Page.of(new ArrayList<>());
        // 构建URL地址
        String keywords = URLUtil.encodeQuery(notBlank(hsQuery.getKeywords()));
        Integer pageNum = ifNull(hsQuery.getPageNum(), ONE);
        page.setPageNum(pageNum);
        String url = String.format("https://www.ihscode.com/hs/list?keyword=%s&countrycode=CN&pageindex=%s", keywords, pageNum);
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
        if (isEmpty(tbodys)) { return page; }
        // 构建数据结果对象
        for (Element tbody : tbodys) {
            // 获取所有的 tr 行数据
            if (tbody == null) { continue; }
            Elements trs = tbody.getElementsByTag("tr"), tds;
            if (isEmpty(trs)) { continue; }
            // 遍历 tr 行数据
            for (Element tr : trs) {
                // 获取 tr 的 td 数据
                if (tr == null || isEmpty(tds = tr.getElementsByTag("td"))) {
                    continue;
                }
                // 如果有 6 列，则是数据搜索页，如果就 5 列，则是分类列表页
                // 6 列：商品编码、商品名称、品名关键词、国家、第一法定单位、更多信息
                // 5 列：商品编码、商品名称、国家、第一法定单位、更多信息
                if (tds.size() < FIVE) { continue; }
                // 提取 商品编码 和 商品名称 数据
                String code = getText(tds, ZERO), name = getText(tds, ONE);
                if (StrUtil.isNotBlank(name)) {
                    name = name.replace("商品名称 ", EMPTY_STRING);
                }
                // 提取 品名关键词、国家和第一法定单位 数据
                String nameKw = null, country, firstLegalUnit;
                if (tds.size() == SIX) {
                    nameKw = getText(tds, TWO); country = getText(tds, THREE);
                    firstLegalUnit = getText(tds, FOUR);
                    if (StrUtil.isNotBlank(nameKw)) {
                        nameKw = nameKw.replace("品名关键词 ", EMPTY_STRING);
                    }
                } else {
                    country = getText(tds, TWO); firstLegalUnit = getText(tds, THREE);
                }
                log.debug("{}, {}, {}, {}, {}", code, name, nameKw, country, firstLegalUnit);
                // 构建数据对象
                HsData hsData = new HsData();
                hsData.setCode(code);
                hsData.setName(name);
                hsData.setFirstLegalUnit(firstLegalUnit);
                hsData.getOthers().put("品名关键词", nameKw);
                hsData.getOthers().put("国家", country);
                page.getData().add(hsData);
            }
        }
        // 结束
        page.setPageSize(page.getData().size());
        return page;
    }

    @Override
    protected HsData detailQuery(String hsCode) {
        // 构建URL地址
        String url = String.format("https://www.ihscode.com/hscode/CN/%s.html", notBlank(hsCode));
        // 构建请求，调用接口
        SimpleRequest request = SimpleRequest.of(HttpMethod.GET, url);
        request.addHeader(USER_AGENT_KEY, USER_AGENT_VAL);
        request.setValidateCertificate(false);
        String bodyAsString = HttpUtil.execute(request).getBodyAsString();
        log.debug("Http response body: {}", bodyAsString);
        if (StrUtil.isBlank(bodyAsString)) { return null; }
        // 解析 HTML 文档
        Document document = Jsoup.parse(bodyAsString);
        Elements tbodys = document.getElementsByTag("tbody");
        if (isEmpty(tbodys)) { return null; }
        // 构建数据结果对象
        HsData hsData = new HsData();
        int tbyIdx = ZERO;
        for (Element tbody : tbodys) {
            // 只有前两个 tbody 是带有数据的
            if (tbyIdx >= TWO) { break; }
            // 获取所有的 tr 行数据
            if (tbody == null) { continue; }
            Elements trs = tbody.getElementsByTag("tr"), tds;
            if (isEmpty(trs)) { continue; }
            // 遍历 tr 行数据
            for (Element tr : trs) {
                // 获取 tr 的 td 数据
                if (tr == null || isEmpty(tds = tr.getElementsByTag("td"))) {
                    continue;
                }
                if (tds.size() < TWO) { continue; }
                String key = getText(tds, ZERO), val = getText(tds, ONE);
                // 填充数据
                if (tbyIdx == ZERO) {
                    fillBaseInfo(hsData, key, val);
                } else {
                    fillTaxRate(hsData, key, val);
                }
                log.debug("key={}, val={}", key, val);
            }
            tbyIdx++;
        }
        // 结束
        return hsData;
    }

}
