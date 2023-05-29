package artoria.data.validation.support;

import artoria.data.validation.BooleanValidator;
import artoria.util.ObjectUtils;

/**
 * The is empty validator.
 * @author Kahle
 */
public class IsEmptyValidator implements BooleanValidator {

    @Override
    public Boolean validate(Object target) {

        return ObjectUtils.isEmpty(target);
    }

}
