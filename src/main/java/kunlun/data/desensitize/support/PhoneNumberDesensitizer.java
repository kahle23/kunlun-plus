/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.desensitize.support;

import kunlun.data.desensitize.Desensitizer;
import kunlun.util.StringUtils;

import static kunlun.common.constant.Numbers.*;
import static kunlun.common.constant.Symbols.ASTERISK;

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
