package artoria.data.validation.support;

import artoria.data.validation.BooleanValidator;
import artoria.util.Assert;
import artoria.util.StringUtils;

/**
 * The is blank validator.
 * @author Kahle
 */
public class IsBlankValidator implements BooleanValidator {

    @Override
    public Boolean validate(Object target) {
        if (target == null) { return true; }
        Assert.isInstanceOf(CharSequence.class, target
                , "The argument must be of type char sequence. ");
        return StringUtils.isBlank((CharSequence) target);
    }

}
