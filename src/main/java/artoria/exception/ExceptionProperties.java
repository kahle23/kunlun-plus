package artoria.exception;

import artoria.util.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Exception properties.
 * @author Kahle
 */
@ConfigurationProperties(prefix = "artoria.exception")
public class ExceptionProperties {
    /**
     * Enabled internal error page.
     */
    private Boolean internalErrorPage = true;
    /**
     * Error display page base template path.
     */
    private String baseTemplatePath = "/error";
    /**
     * Enabled show exception message.
     */
    private Boolean showErrorMessage = false;

    public Boolean getInternalErrorPage() {

        return this.internalErrorPage;
    }

    public void setInternalErrorPage(Boolean internalErrorPage) {
        if (internalErrorPage == null) { return; }
        this.internalErrorPage = internalErrorPage;
    }

    public String getBaseTemplatePath() {

        return this.baseTemplatePath;
    }

    public void setBaseTemplatePath(String baseTemplatePath) {
        if (StringUtils.isBlank(baseTemplatePath)) { return; }
        this.baseTemplatePath = baseTemplatePath;
    }

    public Boolean getShowErrorMessage() {

        return this.showErrorMessage;
    }

    public void setShowErrorMessage(Boolean showErrorMessage) {
        if (showErrorMessage == null) { return; }
        this.showErrorMessage = showErrorMessage;
    }

}
