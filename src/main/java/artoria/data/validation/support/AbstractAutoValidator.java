package artoria.data.validation.support;

import artoria.data.validation.AutoValidator;
import artoria.util.Assert;

/**
 * The abstract automated validator.
 * @author Kahle
 */
public abstract class AbstractAutoValidator implements AutoValidator {
    private final String name;

    public AbstractAutoValidator(String name) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        this.name = name;
    }

    @Override
    public String getName() {

        return name;
    }

}
