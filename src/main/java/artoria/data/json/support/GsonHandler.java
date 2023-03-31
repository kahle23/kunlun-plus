package artoria.data.json.support;

import artoria.data.json.JsonFormat;
import artoria.util.ArrayUtils;
import artoria.util.Assert;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

import static artoria.data.json.JsonFormat.PRETTY_FORMAT;

/**
 * The json handler simple implement by gson.
 * @author Kahle
 */
public class GsonHandler extends AbstractJsonHandler {
    private final Gson prettyFormatGson;
    private final Gson gson;

    public GsonHandler() {

        this(new Gson());
    }

    public GsonHandler(Gson gson) {
        Assert.notNull(gson, "Parameter \"gson\" must not null. ");
        this.gson = gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        this.prettyFormatGson = gsonBuilder.create();
    }

    protected Gson getGson(Object... arguments) {
        if (ArrayUtils.isEmpty(arguments)) { return gson; }
        for (Object arg : arguments) {
            if (arg == null) { continue; }
            if (arg instanceof JsonFormat
                    && PRETTY_FORMAT.equals(arg)) {
                return prettyFormatGson;
            }
        }
        return gson;
    }

    @Override
    public String toJsonString(Object object, Object... arguments) {

        return getGson(arguments).toJson(object);
    }

    @Override
    public <T> T parseObject(String jsonString, Type type, Object... arguments) {

        return getGson(arguments).fromJson(jsonString, type);
    }

}
