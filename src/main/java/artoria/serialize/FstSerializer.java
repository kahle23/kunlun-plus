package artoria.serialize;

import artoria.util.Assert;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Fst serializer.
 * @author Kahle
 */
public class FstSerializer implements Serializer<Object> {

    @Override
    public void serialize(Object object, OutputStream outputStream) throws IOException {
        Assert.notNull(object, "Parameter \"object\" must not null. ");
        Assert.notNull(outputStream, "Parameter \"outputStream\" must not null. ");
        Assert.isInstanceOf(Serializable.class, object, FstSerializer.class.getSimpleName()
                + " requires a Serializable payload but received an object of type ["
                + object.getClass().getName() + "]. ");
        FSTObjectOutput output = new FSTObjectOutput(outputStream);
        output.writeObject(object);
        output.flush();
    }

}
