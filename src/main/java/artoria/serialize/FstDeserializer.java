package artoria.serialize;

import artoria.util.Assert;
import org.nustaq.serialization.FSTObjectInput;

import java.io.IOException;
import java.io.InputStream;

/**
 * Fst deserializer.
 * @author Kahle
 */
public class FstDeserializer implements Deserializer<Object> {

    @Override
    public Object deserialize(InputStream inputStream) throws IOException {
        Assert.notNull(inputStream, "Parameter \"inputStream\" must not null. ");
        FSTObjectInput input = new FSTObjectInput(inputStream);
        try {
            return input.readObject();
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException("Failed to deserialize object type. ", e);
        }
    }

}
