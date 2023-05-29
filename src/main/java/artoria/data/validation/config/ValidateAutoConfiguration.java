package artoria.data.validation.config;

import artoria.data.validation.AutoValidator;
import artoria.data.validation.ValidatorUtils;
import artoria.data.validation.support.*;
import artoria.property.PropertySource;
import artoria.property.PropertyUtils;
import artoria.util.MapUtils;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * The validate auto configuration.
 * @author Kahle
 */
@Configuration
@EnableConfigurationProperties({ValidateProperties.class})
public class ValidateAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ValidateAutoConfiguration.class);

    @Autowired
    public ValidateAutoConfiguration(ValidateProperties validateProperties,
                                     ApplicationContext applicationContext) {
        registerDefaultValidator();
        registerConfiguredRegexValidator(validateProperties);
        registerSpringValidator(applicationContext);
        registerPropertySourceRegexValidator();
    }

    private void registerDefaultValidator() {
        String idNumberRegex = "(^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
        String urlRegex = "^(?:([A-Za-z]+):)?(\\/{0,3})([0-9.\\-A-Za-z]+)(?::(\\d+))?(?:\\/([^?#]*))?(?:\\?([^#]*))?(?:#(.*))?$";
        String emailRegex = "^[0-9A-Za-z][\\.-_0-9A-Za-z]*@[0-9A-Za-z]+(?:\\.[0-9A-Za-z]+)+$";
        String bankCardNumberRegex = "^([1-9]{1})(\\d{14}|\\d{18})$";
        String phoneNumberRegex = "^1\\d{10}$";
        String numericRegex = "^(-|\\+)?\\d+\\.?\\d*$";
        ValidatorUtils.registerValidator("not_blank", new NotBlankValidator());
        ValidatorUtils.registerValidator("not_empty", new NotEmptyValidator());
        ValidatorUtils.registerValidator("not_null", new NotNullValidator());
        ValidatorUtils.registerValidator("is_blank", new IsBlankValidator());
        ValidatorUtils.registerValidator("is_empty", new IsEmptyValidator());
        ValidatorUtils.registerValidator("is_null", new IsNullValidator());
        ValidatorUtils.registerValidator("is_false", new IsFalseValidator());
        ValidatorUtils.registerValidator("is_true", new IsTrueValidator());
        ValidatorUtils.registerValidator("regex:bank_card_number", new RegexValidator(bankCardNumberRegex));
        ValidatorUtils.registerValidator("regex:phone_number", new RegexValidator(phoneNumberRegex));
        ValidatorUtils.registerValidator("regex:id_number", new RegexValidator(idNumberRegex));
        ValidatorUtils.registerValidator("regex:numeric", new RegexValidator(numericRegex));
        ValidatorUtils.registerValidator("regex:email", new RegexValidator(emailRegex));
        ValidatorUtils.registerValidator("regex:url", new RegexValidator(urlRegex));
        ValidatorUtils.registerValidator("bean:bank_card_number:luhn", new BankCardNumberLuhnValidator());
    }

    private void registerSpringValidator(ApplicationContext applicationContext) {
        Map<String, AutoValidator> beansOfType = applicationContext.getBeansOfType(AutoValidator.class);
        if (MapUtils.isEmpty(beansOfType)) { return; }
        for (Map.Entry<String, AutoValidator> entry : beansOfType.entrySet()) {
            AutoValidator validator = entry.getValue();
            if (validator == null) { continue; }
            ValidatorUtils.registerValidator(validator.getName(), validator);
        }
    }

    private void registerConfiguredRegexValidator(ValidateProperties validateProperties) {
        if (validateProperties == null) { return; }
        Map<String, String> regexValidators =
                validateProperties.getRegexValidators();
        if (MapUtils.isEmpty(regexValidators)) { return; }
        for (Map.Entry<String, String> entry : regexValidators.entrySet()) {
            String regex = entry.getValue();
            if (StringUtils.isBlank(regex)) { continue; }
            String name = entry.getKey();
            if (StringUtils.isBlank(name)) { continue; }
            ValidatorUtils.registerValidator(name, new RegexValidator(regex));
        }
    }

    private void registerPropertySourceRegexValidator() {
        String sourceName = "_validator";
        PropertySource source = PropertyUtils.getPropertySource(sourceName);
        if (source == null) { return; }
        Map<String, Object> properties = PropertyUtils.getProperties(sourceName);
        if (MapUtils.isEmpty(properties)) { return; }
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String regex = entry.getValue() != null ? String.valueOf(entry.getValue()) : null;
            if (StringUtils.isBlank(regex)) { continue; }
            String name = entry.getKey();
            if (StringUtils.isBlank(name)) { continue; }
            ValidatorUtils.registerValidator(name, new RegexValidator(regex));
        }
    }

}
