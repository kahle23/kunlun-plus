package artoria.data.json.support.jackson;

import artoria.time.DateUtils;
import artoria.util.StringUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.Date;

/**
 * Jackson json date deserializer.
 * @author Kahle
 */
@JsonComponent
public class DateJsonDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        if (StringUtils.isNumeric(text)) {
            return DateUtils.parse(Long.parseLong(text));
        }
        else {
            return DateUtils.parse(text);
        }
    }

}
