package artoria.exception;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Exception properties.
 * @author Kahle
 */
@Deprecated // TODO: can delete
@ConfigurationProperties(prefix = "artoria.exception")
public class ExceptionProperties {
    /**
     * Enabled default exception handler.
     */
    private Boolean enabled;
    /**
     * Enabled internal error page.
     */
    private Boolean internalErrorPage = true;
    /**
     * Error display page base template path.
     */
    private String baseTemplatePath = "/error";

    public Boolean getEnabled() {

        return enabled;
    }

    public void setEnabled(Boolean enabled) {

        this.enabled = enabled;
    }

    public Boolean getInternalErrorPage() {

        return internalErrorPage;
    }

    public void setInternalErrorPage(Boolean internalErrorPage) {

        this.internalErrorPage = internalErrorPage;
    }

    public String getBaseTemplatePath() {

        return baseTemplatePath;
    }

    public void setBaseTemplatePath(String baseTemplatePath) {

        this.baseTemplatePath = baseTemplatePath;
    }

}
