/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.validation.support.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * The validate properties.
 * @author Kahle
 */
@ConfigurationProperties("kunlun.validate")
public class ValidateProperties {
    /**
     * Regex validator configuration, and key is name and value is regex.
     */
    private Map<String, String> regexValidators = new HashMap<String, String>();

    public Map<String, String> getRegexValidators() {

        return regexValidators;
    }

    public void setRegexValidators(Map<String, String> regexValidators) {

        this.regexValidators = regexValidators;
    }

}
