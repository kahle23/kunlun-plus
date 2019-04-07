package artoria.serialize;

import artoria.codec.Hex;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.junit.Before;
import org.junit.Test;

public class KryoTest {
    private Hex hex = new Hex();

    @Before
    public void init() {
        KryoFactory factory = new KryoFactory() {
            @Override
            public Kryo create() {
                return new Kryo();
            }
        };
        KryoPool pool = new KryoPool.Builder(factory).softReferences().build();
        SerializeUtils.setSerializer(new KryoSerializer(pool));
        SerializeUtils.setDeserializer(new KryoDeserializer(pool));
    }

    @Test
    public void test1() {
        KryoTest kryoTest = new KryoTest();
        System.out.println(kryoTest);
        byte[] bytes = SerializeUtils.serialize(kryoTest);
        System.out.println(hex.encodeToString(bytes));
        Object obj = SerializeUtils.deserialize(bytes);
        System.out.println(obj);
    }

}
