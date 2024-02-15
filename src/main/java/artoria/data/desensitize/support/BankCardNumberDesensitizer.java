package artoria.data.desensitize.support;

import artoria.data.desensitize.Desensitizer;
import artoria.util.StringUtils;

import static artoria.common.constant.Numbers.FOUR;
import static artoria.common.constant.Numbers.ZERO;
import static artoria.common.constant.Symbols.ASTERISK;
import static artoria.common.constant.Symbols.BLANK_SPACE;

public class BankCardNumberDesensitizer implements Desensitizer {
    private final String cover;

    public BankCardNumberDesensitizer() {

        this(null);
    }

    public BankCardNumberDesensitizer(String cover) {

        this.cover = StringUtils.isNotBlank(cover) ? cover : ASTERISK;
    }

    public String getCover() {

        return cover;
    }

    @Override
    public String desensitize(CharSequence data) {
        if (StringUtils.isBlank(data)) { return String.valueOf(data); }
        String dataTmp = String.valueOf(data);
        int dataLength = dataTmp.length();
        if (dataLength <= FOUR) { return dataTmp; }
        int beginIndex = dataLength - FOUR;
        String back = dataTmp.substring(beginIndex, dataLength);
        StringBuilder builder = new StringBuilder();
        for (int i = ZERO; i < 12; i++) {
            if (i % FOUR == ZERO) {
                builder.append(BLANK_SPACE);
            }
            builder.append(cover);
        }
        builder.append(BLANK_SPACE);
        builder.append(back);
        return builder.toString();
    }

}
