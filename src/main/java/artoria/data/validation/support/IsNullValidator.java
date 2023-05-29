package artoria.data.validation.support;

import artoria.data.validation.BooleanValidator;

/**
 * The is null validator.
 * @author Kahle
 */
public class IsNullValidator implements BooleanValidator {

    @Override
    public Boolean validate(Object target) {

        return target == null;
    }

}
