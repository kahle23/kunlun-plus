package baibao.db.vector.support.pinecone;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import kunlun.data.Dict;
import kunlun.data.json.JsonUtil;
import kunlun.data.json.support.FastJsonProcessor;
import kunlun.generator.id.IdUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Ignore
public class PineconeVectorDbHandlerTest {
    private static final Logger log = LoggerFactory.getLogger(PineconeVectorDbHandlerTest.class);
    private static final BasePineconeVectorDbHandler vectorStorage = new PineconeVectorDbHandlerImpl(
            "host",
            "apiKey",
            new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 51837)));

    static {

        JsonUtil.registerProcessor(JsonUtil.getDefaultProcessorName(), new FastJsonProcessor());
    }

    @Test
    public void testUpsert() {
        Dict input = Dict.of();
        // 向量ID，必填
        input.set("id", IdUtil.nextString("uuid"));
        // 向量数据列表，必填，长度必须是1536
        List<Float> values = new ArrayList<Float>();
        for (int i = 0; i < 1536; i++) {
            values.add(Double.valueOf(RandomUtil.randomDouble()).floatValue());
        }
        input.set("values", values);
        // sparseValues 向量稀疏数据。表示为索引列表和对应值列表，它们必须具有相同的长度。
        // 元数据，非必填
        Dict metadata = Dict.of("测试Key", "测试Val")
                .set("key1", "val1");
        input.set("metadata", metadata);
        // upsert
        Dict inputData = Dict.of("vectors", Collections.singletonList(input));
        Object result = vectorStorage.docUpsert(inputData, Dict.class);
        log.info("upsert result: {}", JSON.toJSONString(result, true));
    }

    @Test
    public void testQuery() {
        // namespace 命名空间
        // topK 查询数量
        // filter 过滤器 - 可以使用元数据
        // includeValues 是否包含向量值
        // includeMetadata 是否包含元数据
        // vector 向量数据列表
        // sparseValues 向量稀疏数据。表示为索引列表和对应值列表，它们必须具有相同的长度。
        Dict query = Dict.of();
        query.set("topK", 20);
        // id 向量ID
//        query.set("id", "8ecb6a9a3e5546cc956b1c6e23034dcc");
        // 向量数据列表，必填，长度必须是1536
        List<Float> vector = new ArrayList<Float>();
        for (int i = 0; i < 1536; i++) {
            vector.add(Double.valueOf(RandomUtil.randomDouble()).floatValue());
        }
        query.set("vector", vector);
        Object result = vectorStorage.docQuery(query, Dict.class);
        log.info("query result: {}", JSON.toJSONString(result, true));
    }

    @Test
    public void testFetch() {
        // namespace 命名空间
        // ids 向量ID列表
        Dict query = Dict.of();
        query.set("ids", Collections.singletonList("2487333abb0a44f086d7c7d993c25a0b"));
        Object result = vectorStorage.docFetch(query, Dict.class);
        log.info("fetch result: {}", JSON.toJSONString(result, true));
    }

    @Test
    public void testUpdate() {
        Dict input = Dict.of();
        // 向量ID，必填
        input.set("id", "8ecb6a9a3e5546cc956b1c6e23034dcc");
        // 向量数据列表，必填，长度必须是1536
        List<Float> values = new ArrayList<Float>();
        for (int i = 0; i < 1536; i++) {
            values.add(Double.valueOf(RandomUtil.randomDouble()).floatValue());
        }
        input.set("values", values);
        // sparseValues 向量稀疏数据。表示为索引列表和对应值列表，它们必须具有相同的长度。
        // 元数据，非必填
        Dict metadata = Dict.of("测试元数据", "测试元数据Val")
                .set("key1", "val1");
        input.set("setMetadata", metadata);
        // update
        Object result = vectorStorage.docUpdate(input, Dict.class);
        log.info("update result: {}", JSON.toJSONString(result, true));
    }

    @Test
    public void testDelete() {
        // ids       向量id列表
        // deleteAll 是否删除命名空间所有数据
        // filter    过滤器 - 可以使用元数据
        // namespace 命名空间
        Dict input = Dict.of();
        input.set("ids", Collections.singletonList("2487333abb0a44f086d7c7d993c25a0b"));
        Object result = vectorStorage.docDelete(input, Dict.class);
        log.info("delete result: {}", JSON.toJSONString(result, true));
    }

    @Test
    public void testDescribeIndexStats() {
        // namespace 命名空间
        // topK 查询数量
        // filter 过滤器 - 可以使用元数据
        // includeValues 是否包含向量值
        // includeMetadata 是否包含元数据
        // vector 向量数据列表
        // sparseValues 向量稀疏数据。表示为索引列表和对应值列表，它们必须具有相同的长度。
        // id 向量ID
        Object result = vectorStorage.describeIndexStats(Dict.of(), Dict.class);
        log.info("describeIndexStats result: {}", JSON.toJSONString(result, true));
    }

}
