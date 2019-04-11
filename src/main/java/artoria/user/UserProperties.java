package artoria.user;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * User properties.
 * @author Kahle
 */
@ConfigurationProperties("artoria.user")
public class UserProperties {
    /**
     * Enabled user interceptor.
     */
    private Boolean enabled;
    /**
     * Token name.
     */
    private String tokenName = "Authorization";
    /**
     * Token expire.
     */
    private Long tokenExpire = 7 * 24 * 60 * 60 * 1000L;
    /**
     * User information expire.
     */
    private Long userExpire = 7 * 24 * 60 * 60 * 1000L;
    /**
     * URL patterns to which the registered interceptor should not apply to.
     */
    private String[] excludePathPatterns = new String[0];

    public Boolean getEnabled() {

        return this.enabled;
    }

    public void setEnabled(Boolean enabled) {

        this.enabled = enabled;
    }

    public String getTokenName() {

        return this.tokenName;
    }

    public void setTokenName(String tokenName) {

        this.tokenName = tokenName;
    }

    public Long getTokenExpire() {

        return this.tokenExpire;
    }

    public void setTokenExpire(Long tokenExpire) {

        this.tokenExpire = tokenExpire;
    }

    public Long getUserExpire() {

        return this.userExpire;
    }

    public void setUserExpire(Long userExpire) {

        this.userExpire = userExpire;
    }

    public String[] getExcludePathPatterns() {

        return this.excludePathPatterns;
    }

    public void setExcludePathPatterns(String[] excludePathPatterns) {

        this.excludePathPatterns = excludePathPatterns;
    }

}
