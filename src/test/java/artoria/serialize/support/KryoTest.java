package artoria.serialize.support;

import artoria.codec.CodecUtils;
import artoria.core.Serializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static artoria.codec.CodecUtils.HEX;

public class KryoTest {
    private static final Logger log = LoggerFactory.getLogger(KryoTest.class);
    private static final Serializer serializer;

    static  {
        KryoFactory factory = new KryoFactory() {
            @Override
            public Kryo create() {
                return new Kryo();
            }
        };
        KryoPool pool = new KryoPool.Builder(factory).softReferences().build();
        serializer = new KryoSerializer(pool);
    }

    @Test
    public void test1() {
        KryoTest kryoTest = new KryoTest();
        log.info("Object: {}", kryoTest);
        byte[] bytes = (byte[]) serializer.serialize(kryoTest);
        log.info("Hex: {}", CodecUtils.encodeToString(HEX, bytes));
        Object obj = serializer.deserialize(bytes);
        log.info("Object: {}", obj);
    }

}
