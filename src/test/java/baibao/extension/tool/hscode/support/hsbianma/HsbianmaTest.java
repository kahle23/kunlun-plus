package baibao.extension.tool.hscode.support.hsbianma;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import kunlun.net.http.HttpMethod;
import kunlun.net.http.HttpUtil;
import kunlun.net.http.support.SimpleRequest;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @see <a href="https://www.hsbianma.com/">HS Code HS编码查询</a>
 * @author Kahle
 */
@Slf4j
@Ignore
public class HsbianmaTest {

    @Test
    public void test1() {
        // 构建请求，调用接口
        SimpleRequest request = SimpleRequest.of(HttpMethod.GET, "https://www.hsbianma.com/Code/6202401000.html");
        request.setValidateCertificate(false);
        request.setConnectTimeout(60000);
        request.setReadTimeout(60000);
        request.addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        request.addHeader("accept-encoding", "gzip, deflate, br, zstd");
        request.addHeader("accept-language", "zh-CN,zh;q=0.9");
        request.addHeader("cache-control", "no-cache");
        request.addHeader("dnt", "1");
        request.addHeader("pragma", "no-cache");
        request.addHeader("priority", "u=0, i");
        request.addHeader("sec-ch-ua", "\"Google Chrome\";v=\"141\", \"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"141\"");
        request.addHeader("sec-ch-ua-mobile", "?0");
        request.addHeader("sec-ch-ua-platform", "\"Windows\"");
        request.addHeader("sec-fetch-dest", "document");
        request.addHeader("sec-fetch-mode", "navigate");
        request.addHeader("sec-fetch-site", "none");
        request.addHeader("sec-fetch-user", "?1");
        request.addHeader("upgrade-insecure-requests", "1");
        request.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36");

        String bodyAsString = HttpUtil.execute(request).getBodyAsString();
        log.debug("Http response body: {}", bodyAsString);
        //
        Document document = Jsoup.parse(bodyAsString);
        Elements tbodys = document.getElementsByTag("tbody");

        for (Element tbody : tbodys) {
            log.info(tbody.text());
        }
    }

}
