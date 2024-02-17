/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.property.support;

import kunlun.property.BaseReadOnlyPropertySource;
import kunlun.util.Assert;
import org.springframework.core.env.Environment;

import java.util.Map;

public class SpringEnvPropertySource extends BaseReadOnlyPropertySource {
    private final Environment env;

    public SpringEnvPropertySource(String name, Environment env) {
        super(name);
        Assert.notNull(env, "Parameter \"env\" must not null. ");
        this.env = env;
    }

    @Override
    public Map<String, Object> getProperties() {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsProperty(String name) {

        return env.containsProperty(name);
    }

    @Override
    public Object getProperty(String name) {

        return env.getProperty(name);
    }

}
