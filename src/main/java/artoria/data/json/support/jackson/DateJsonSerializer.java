package artoria.data.json.support.jackson;

import artoria.time.DateUtils;
import artoria.util.Assert;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.Date;

@JsonComponent
public class DateJsonSerializer extends JsonSerializer<Date> {
    private final String pattern;

    public DateJsonSerializer(String pattern) {
        Assert.notBlank(pattern, "Parameter \"pattern\" must not blank. ");
        this.pattern = pattern;
    }

    public DateJsonSerializer() {

        this("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String format = DateUtils.format(value, pattern);
        gen.writeString(format);
    }

}
