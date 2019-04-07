package artoria.serialize;

import artoria.codec.Hex;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

public class FstTest implements Serializable {
    private Hex hex = new Hex();

    @Before
    public void init() {
        SerializeUtils.setSerializer(new FstSerializer());
        SerializeUtils.setDeserializer(new FstDeserializer());
    }

    @Test
    public void test1() {
        FstTest fstTest = new FstTest();
        System.out.println(fstTest);
        byte[] bytes = SerializeUtils.serialize(fstTest);
        System.out.println(hex.encodeToString(bytes));
        Object obj = SerializeUtils.deserialize(bytes);
        System.out.println(obj);
    }

}
