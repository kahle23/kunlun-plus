package kunlun.spring.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

@ConfigurationProperties(prefix = "kunlun.security.servlet")
public class SecurityProperties {

    private Boolean enabled;

    private Long tokenTimeToLive;

    private DataFieldPermissionConfig dataField = new DataFieldPermissionConfig();

    private AccessPermissionConfig access = new AccessPermissionConfig();

    public Boolean getEnabled() {

        return enabled;
    }

    public void setEnabled(Boolean enabled) {

        this.enabled = enabled;
    }

    public Long getTokenTimeToLive() {
        return tokenTimeToLive;
    }

    public void setTokenTimeToLive(Long tokenTimeToLive) {
        this.tokenTimeToLive = tokenTimeToLive;
    }

    public DataFieldPermissionConfig getDataField() {
        return dataField;
    }

    public void setDataField(DataFieldPermissionConfig dataField) {
        this.dataField = dataField;
    }

    public AccessPermissionConfig getAccess() {
        return access;
    }

    public void setAccess(AccessPermissionConfig access) {
        this.access = access;
    }

    public static class DataFieldPermissionConfig {
        private Boolean enabled;
        private List<String> ignoredUrls = Collections.emptyList();

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getIgnoredUrls() {
            return ignoredUrls;
        }

        public void setIgnoredUrls(List<String> ignoredUrls) {
            this.ignoredUrls = ignoredUrls;
        }
    }


    /**
     * @see ResourceAccessPreHandleConfiguration
     * @see kunlun.core.handler.ResourceAccessPreHandler
     */
    public static class AccessPermissionConfig {
        private Boolean enabled;

        private List<String> pathPatterns;

        private List<String> excludePathPatterns;

        private Boolean showLog = false;

        private List<String> ignoredUrls = Collections.emptyList();

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getPathPatterns() {
            return pathPatterns;
        }

        public void setPathPatterns(List<String> pathPatterns) {
            this.pathPatterns = pathPatterns;
        }

        public List<String> getExcludePathPatterns() {
            return excludePathPatterns;
        }

        public void setExcludePathPatterns(List<String> excludePathPatterns) {
            this.excludePathPatterns = excludePathPatterns;
        }

        public Boolean getShowLog() {
            return showLog;
        }

        public void setShowLog(Boolean showLog) {
            this.showLog = showLog;
        }

        public List<String> getIgnoredUrls() {
            return ignoredUrls;
        }

        public void setIgnoredUrls(List<String> ignoredUrls) {
            this.ignoredUrls = ignoredUrls;
        }
    }




}
