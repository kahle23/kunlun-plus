package artoria.data.desensitize.support;

import artoria.data.desensitize.Desensitizer;
import artoria.util.StringUtils;

import static artoria.common.Constants.*;

public class PhoneNumberDesensitizer implements Desensitizer {
    private static final Integer PHONE_NUMBER_LENGTH = 11;
    private static final Integer COVER_LENGTH = 5;
    private final String cover;

    public PhoneNumberDesensitizer() {

        this(null);
    }

    public PhoneNumberDesensitizer(String cover) {

        this.cover = StringUtils.isNotBlank(cover) ? cover : ASTERISK;
    }

    public String getCover() {

        return cover;
    }

    @Override
    public String desensitize(CharSequence data) {
        if (StringUtils.isBlank(data)) {
            return String.valueOf(data);
        }
        String dataTmp = String.valueOf(data);
        int length = data.length();
        if (length == PHONE_NUMBER_LENGTH) {
            String prefix = dataTmp.substring(ZERO, THREE);
            String suffix = dataTmp.substring(EIGHT, 11);
            StringBuilder builder = new StringBuilder();
            builder.append(prefix);
            for (int i = ZERO; i < COVER_LENGTH; i++) {
                builder.append(cover);
            }
            builder.append(suffix);
            return builder.toString();
        }
        return dataTmp;
    }

}
