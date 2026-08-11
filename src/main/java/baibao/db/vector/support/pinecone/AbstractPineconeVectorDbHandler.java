/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.db.vector.support.pinecone;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import kunlun.data.Dict;
import kunlun.data.bean.BeanUtil;
import kunlun.data.json.JsonUtil;
import kunlun.data.tuple.Triple;
import kunlun.db.AbstractDbHandler;
import kunlun.db.vector.VectorDbHandler;
import kunlun.net.http.HttpMethod;
import kunlun.util.ArgumentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.Proxy;

/**
 * The base pinecone vector database handler.
 * <a href="https://www.pinecone.io/">Pinecone</a>
 * <a href="https://docs.pinecone.io/reference">Pinecone API Reference</a>
 * @author Kahle
 */
public abstract class AbstractPineconeVectorDbHandler extends AbstractDbHandler implements VectorDbHandler {
    private static final Logger log = LoggerFactory.getLogger(AbstractPineconeVectorDbHandler.class);

    /**
     * Get the database configuration according to the arguments.
     * @param input The input arguments
     * @return The database configuration
     */
    protected abstract Config getConfig(Object input);

    protected Object doHttp(HttpMethod method, String url, Object input, Config config) {
        Boolean debug = config.getDebug();
        Proxy proxy = config.getProxy();

        Method mtd;
        if (HttpMethod.POST.equals(method)) {
            mtd = Method.POST;
        }
        else if (HttpMethod.GET.equals(method)) {
            mtd = Method.GET;
        }
        else {
            throw new UnsupportedOperationException("The http method is unsupported. ");
        }

        HttpRequest request = HttpUtil.createRequest(mtd, url);
        request.header("content-type", "application/json");
        request.header("Api-Key", config.getApiKey());

        if (HttpMethod.POST.equals(method)) {
            request.body(JsonUtil.toJsonString(input));
        }
        else {
            request.form(BeanUtil.beanToMap(input));
        }


        if (proxy != null) {
            request.setProxy(proxy);
        }

        if (debug != null && debug) {
            log.info("Http pinecone input: {}", JsonUtil.toJsonString(input));
        }
        HttpResponse response = request.execute();
        String responseBody = response.body();
        if (debug != null && debug) {
            log.info("Http pinecone output: {}", responseBody);
        }

        if (StrUtil.isBlank(responseBody)) { return null; }
        Dict respData = JsonUtil.parseObject(responseBody, Dict.class);

        String message = respData.getString("message");
        Integer code = respData.getInteger("code");
        if (code != null) {
            throw new IllegalStateException(String.format("%s (code: %s)", message, code));
        }
        return respData;
    }

    @Override
    public Object execute(Object[] arguments) {
        Triple<Object, String, Class<?>> triple = ArgumentUtil.parseToObjStrCls(arguments);
        Object input = triple.getLeft();
        String strategy = triple.getMiddle();
        Class<?> clazz = triple.getRight();
        if ("describeIndexStats".equals(strategy)) { return describeIndexStats(input, clazz); }
        else if ("docQuery".equals(strategy)) { return docQuery(input, clazz); }
        else if ("docDelete".equals(strategy)) { return docDelete(input, clazz); }
        else if ("docFetch".equals(strategy)) { return docFetch(input, clazz); }
        else if ("docUpdate".equals(strategy)) { return docUpdate(input, clazz); }
        else if ("docUpsert".equals(strategy)) { return docUpsert(input, clazz); }
        else {
            throw new UnsupportedOperationException(
                "The method is unsupported. \n\n" +
                "The pinecone vector database handler. \n" +
                "(The arguments see api documents \"https://docs.pinecone.io/reference\")\n" +
                "Supported method:\n" +
                " - describeIndexStats\n" +
                " - docQuery\n" +
                " - docDelete\n" +
                " - docFetch\n" +
                " - docUpdate\n" +
                " - docUpsert\n"
            );
        }
    }

    public Object describeIndexStats(Object condition, Class<?> clazz) {
        Config config = getConfig(condition);
        String url = config.getHost() + "/describe_index_stats";
        return doHttp(HttpMethod.POST, url, condition, config);
    }

    public Object docQuery(Object condition, Class<?> clazz) {
        Config config = getConfig(condition);
        String url = config.getHost() + "/query";
        return doHttp(HttpMethod.POST, url, condition, config);
    }

    public Object docDelete(Object condition, Class<?> clazz) {
        Config config = getConfig(condition);
        String url = config.getHost() + "/vectors/delete";
        return doHttp(HttpMethod.POST, url, condition, config);
    }

    public Object docFetch(Object condition, Class<?> clazz) {
        Config config = getConfig(condition);
        String url = config.getHost() + "/vectors/fetch";
        return doHttp(HttpMethod.GET, url, BeanUtil.beanToMap(condition), config);
    }

    public Object docUpdate(Object data, Class<?> clazz) {
        Config config = getConfig(data);
        String url = config.getHost() + "/vectors/update";
        return doHttp(HttpMethod.POST, url, data, config);
    }

    public Object docUpsert(Object data, Class<?> clazz) {
        Config config = getConfig(data);
        String url = config.getHost() + "/vectors/upsert";
        return doHttp(HttpMethod.POST, url, data, config);
    }

    /**
     * The pinecone database configuration.
     * @author Kahle
     */
    public static class Config implements Serializable {
        private String host;
        private String apiKey;
        private Proxy  proxy;
        private Boolean debug;

        public Config(String host, String apiKey, Proxy proxy) {
            this.apiKey = apiKey;
            this.proxy = proxy;
            this.host = host;
        }

        public Config(String host, String apiKey) {
            this.apiKey = apiKey;
            this.host = host;
        }

        public Config() {

        }

        public String getHost() {

            return host;
        }

        public void setHost(String host) {

            this.host = host;
        }

        public String getApiKey() {

            return apiKey;
        }

        public void setApiKey(String apiKey) {

            this.apiKey = apiKey;
        }

        public Proxy getProxy() {

            return proxy;
        }

        public void setProxy(Proxy proxy) {

            this.proxy = proxy;
        }

        public Boolean getDebug() {

            return debug;
        }

        public void setDebug(Boolean debug) {

            this.debug = debug;
        }
    }

}
