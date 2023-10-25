package artoria.data.desensitize.support;

import artoria.data.desensitize.Desensitizer;
import artoria.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WithPhoneNumberDesensitizer implements Desensitizer {
    private final Desensitizer phoneNumberMasker;
    private final Pattern pattern;

    public WithPhoneNumberDesensitizer() {

        this(null, null);
    }

    public WithPhoneNumberDesensitizer(String regex, String cover) {
        this.phoneNumberMasker = new PhoneNumberDesensitizer(cover);
        if (StringUtils.isBlank(regex)) {
            regex = "1[3|4|5|6|7|8|9]\\d{9}";
        }
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public String desensitize(CharSequence data) {
        if (StringUtils.isBlank(data)) {
            return String.valueOf(data);
        }
        String dataTmp = String.valueOf(data);
        Matcher matcher = pattern.matcher(dataTmp);
        while (matcher.find()) {
            String phoneNumber = matcher.group();
            String mask = phoneNumberMasker.desensitize(phoneNumber);
            dataTmp = StringUtils.replace(dataTmp, phoneNumber, mask);
        }
        return dataTmp;
    }

}
