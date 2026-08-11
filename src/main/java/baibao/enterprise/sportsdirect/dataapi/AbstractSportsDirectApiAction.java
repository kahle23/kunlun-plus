package baibao.enterprise.sportsdirect.dataapi;

import baibao.enterprise.sportsdirect.dataapi.pojo.query.VisualizerPwarQuery;
import baibao.enterprise.sportsdirect.dataapi.pojo.result.PoDetailsResult;
import baibao.enterprise.sportsdirect.dataapi.pojo.result.PodOrderListResult;
import baibao.enterprise.sportsdirect.dataapi.pojo.result.VisualizerPwarDataResult;
import baibao.enterprise.sportsdirect.dataapi.pojo.result.VisualizerPwarResult;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.csv.CsvReadConfig;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.ReflectUtil;
import kunlun.action.util.net.HttpCallAction;
import kunlun.core.function.Function;
import kunlun.data.Array;
import kunlun.data.bean.BeanUtil;
import kunlun.data.map.FmMap;
import kunlun.data.map.ToSoMap;
import kunlun.net.UrlUtil;
import kunlun.net.http.HttpMethod;
import kunlun.net.http.HttpResponse;
import kunlun.net.http.HttpUtil;
import kunlun.net.http.support.SimpleRequest;
import kunlun.util.Assert;
import lombok.Getter;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.convert.Convert.toStr;
import static kunlun.common.constant.Numbers.ZERO;
import static kunlun.util.Assert.*;

/**
 * Sports Direct 的数据提供者.
 * @see <a href="https://www.sportsdirect.com">Sports Direct</a>
 * @author Kahle
 */
public abstract class AbstractSportsDirectApiAction extends HttpCallAction {

    @Override
    protected HttpCallContext buildContext(String strategy, Object input, Object[] arguments) {
        Assert.notBlank(strategy, "方法名不能为空！");
        strategy = strategy.trim().toLowerCase();
        HttpCallContext context = new HttpCallContext(strategy, input, arguments);
        context.setUrlPrefix("https://api.sportsdirectservices.com");
        return context;
    }

    @Override
    protected void fillToken(HttpCallContext context) {
        String username = toStr(context.getStorage().get(KEY_USERNAME));
        String password = toStr(context.getStorage().get(KEY_PASSWORD));
        context.setAuthToken(HttpUtil.basicAuthToken(username, password));
    }

    @Override
    protected void preProcess(HttpCallContext context) {
        List<String> pwarDataSts = Arrays.asList("visualizer-pwar-data", "visualizer-pwar-eu-data");
        List<String> pwarSts = Arrays.asList("visualizer-pwar", "visualizer-pwar-eu");
        List<String> noEuList = Arrays.asList("994", "988", "999");
        List<String> euList = Arrays.asList("992", "976");
        // pwar
        if (pwarSts.contains(context.getStrategy())) {
            String wh = ((VisualizerPwarQuery) notNull(context.getInput())).getWarehouse();
            // 接口 visualizer-pwar：994、988、999
            if (noEuList.contains(wh)) { context.setStrategy("visualizer-pwar"); }
            // 接口 visualizer-pwar-eu：992、976
            if (euList.contains(wh)) { context.setStrategy("visualizer-pwar-eu"); }
        }
        // pwar-data
        if (pwarDataSts.contains(context.getStrategy())) {
            String wh = ((VisualizerPwarQuery) notNull(context.getInput())).getWarehouse();
            // 接口 visualizer-pwar-data：994、988、999
            if (noEuList.contains(wh)) { context.setStrategy("visualizer-pwar-data"); }
            // 接口 visualizer-pwar-eu-data：992、976
            if (euList.contains(wh)) { context.setStrategy("visualizer-pwar-eu-data"); }
        }
        // PODetails AAPEN    994 988 999    Excel 走的是 uk 接口
        // PODetails DISPORT  992 976        Excel 走的是 eu 接口
        List<String> poDetailsSts = Arrays.asList("po-details-uk", "po-details-eu", "po-details");
        List<String> poOrdersSts = Arrays.asList("pod-order-list", "pod-order-list-eu");
        // po-details
        if (poDetailsSts.contains(context.getStrategy())) {
            // 转换成 Map
            Map<String, Object> toMap = Collections.emptyMap();
            if (context.getInput() != null) {
                toMap = ((ToSoMap) isInstanceOf(ToSoMap.class, context.getInput())).toMap();
            }
            String wh = Convert.toStr(toMap.get("WAREHOUSE"));
            // 接口 po-details-uk：994、988、999
            if (noEuList.contains(wh)) { context.setStrategy("po-details-uk"); }
            // 接口 po-details-eu：992、976
            if (euList.contains(wh)) { context.setStrategy("po-details-eu"); }
        }
        // pod-order-list
        if (poOrdersSts.contains(context.getStrategy())) {
            // 转换成 Map
            Map<String, Object> toMap = Collections.emptyMap();
            if (context.getInput() != null) {
                toMap = ((ToSoMap) isInstanceOf(ToSoMap.class, context.getInput())).toMap();
            }
            String wh = Convert.toStr(toMap.get("WAREHOUSE"));
            // 接口 pod-order-list：994、988、999
            if (noEuList.contains(wh)) { context.setStrategy("pod-order-list"); }
            // 接口 pod-order-list-eu：992、976
            if (euList.contains(wh)) { context.setStrategy("pod-order-list-eu"); }
        }
    }

    protected void initProcessors() {
        // Visualizer-PWAR（可视化PWAR）
        registerProcessor("visualizer-pwar", new SdiApiCallProcessor("/query/CSV/VISUALIZERPWAR", VisualizerPwarResult.class));
        // Visualizer-PWAR-EU（可视化PWAR-欧洲）
        registerProcessor("visualizer-pwar-eu", new SdiApiCallProcessor("/query/CSV/VISUALIZERPWAREU", VisualizerPwarResult.class));
        // Visualizer-PWAR-data（可视化PWAR数据）
        registerProcessor("visualizer-pwar-data", new SdiApiCallProcessor("/query/CSV/VISUALIZERPWAR-DATA", VisualizerPwarDataResult.class));
        // Visualizer-PWAR-EU-data（可视化PWAR数据-欧洲）
        registerProcessor("visualizer-pwar-eu-data", new SdiApiCallProcessor("/query/CSV/VISUALIZERPWAREU-DATA", VisualizerPwarDataResult.class));
        // POD-order-list（订单列表）
        registerProcessor("pod-order-list", new SdiApiCallProcessor("/query/CSV/POD-ORDERLIST", PodOrderListResult.class));
        // POD-order-list-EU（订单列表-欧洲）
        registerProcessor("pod-order-list-eu", new SdiApiCallProcessor("/query/CSV/POD-ORDERLIST-EU", PodOrderListResult.class));
        // PO-details-UK（订单详情-英国）
        registerProcessor("po-details-uk", new SdiApiCallProcessor("/query/CSV/PODETAILSUK", PoDetailsResult.class));
        // PO-details-EU（订单详情-欧洲）
        registerProcessor("po-details-eu", new SdiApiCallProcessor("/query/CSV/PODETAILSEU", PoDetailsResult.class));
        // visualiser-review-weeks（可视化每周审查）
        registerProcessor("visualiser-review-weeks", new SdiApiCallProcessor("/query/CSV/VISUALISER-REVIEW-WEEKS", Object.class));
        // redirects（重新寄送）
        registerProcessor("redirects", new SdiApiCallProcessor("/query/CSV/REDIRECTS", Object.class));
        // product-performance（产品性能）
        registerProcessor("product-performance", new SdiApiCallProcessor("/query/CSV/PRODUCT-PERFORMANCE", Object.class));
    }

    /**
     * Sports Direct 的接口调用处理器.
     * @author Kahle
     */
    @Getter
    public static class SdiApiCallProcessor implements Function<HttpCallContext, Object> {
        private final Class<?> resultClass;
        private final Integer timeout;
        private final String uri;

        public SdiApiCallProcessor(Integer timeout, String uri, Class<?> resultClass) {
            this.resultClass = notNull(resultClass);
            this.timeout = notNull(timeout);
            this.uri = notBlank(uri);
        }

        public SdiApiCallProcessor(String uri, Class<?> resultClass) {

            this(120000, uri, resultClass);
        }

        @Override
        public Object apply(HttpCallContext ct) {
            // 构建 URL 地址
            String url = UrlUtil.completeUrl(ct.getUrlPrefix(), getUri());
            // 将参数转换为 Map
            Map<String, Object> toMap = Collections.emptyMap();
            if (ct.getInput() instanceof ToSoMap) {
                toMap = ((ToSoMap) ct.getInput()).toMap();
            } else if (ct.getInput() != null) {
                toMap = BeanUtil.beanToMap(ct.getInput());
            }
            // 构建 Http 请求对象
            SimpleRequest request = SimpleRequest.of(HttpMethod.GET, url);
            request.setValidateCertificate(Boolean.FALSE);
            request.setConnectTimeout(timeout);
            request.setReadTimeout(timeout);
            request.addHeader(KEY_AUTHORIZATION, ct.getAuthToken());
            request.addParameters(toMap);
            // 进行 Http 调用
            HttpResponse response = HttpUtil.execute(request);
            String bodyAsString = response.getBodyAsString();
            // 转换为 CSV 对象
            CsvReadConfig csvConfig = CsvReadConfig.defaultConfig();
            csvConfig.setHeaderLineNo(ZERO);
            CsvReader reader = CsvUtil.getReader(new StringReader(bodyAsString), csvConfig);
            // 进行数据转换
            Array results = new Array();
            for (CsvRow csvRow : reader) {
                Object result = ReflectUtil.newInstance(getResultClass());
                if (result instanceof FmMap) {
                    ((FmMap) result).fromMap(csvRow.getFieldMap());
                } else {
                    throw new UnsupportedOperationException("The result class must implement \"FromMap\". ");
                }
                results.add(result);
            }
            return results;
        }
    }

}
