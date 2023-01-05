package artoria.spring.config.xxljob;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The xxl-job configuration.
 * @author Kahle
 */
@ConfigurationProperties("spring.extension.xxl-job")
public class XxlJobProperties {
    /**
     * Enable the xxl-job.
     */
    private Boolean enabled;
    /**
     * The xxl-job admin address list, such as "http://address" or "http://address01,http://address02".
     */
    private String adminAddresses;
    /**
     * The xxl-job, access token.
     */
    private String accessToken;
    /**
     * The xxl-job executor config.
     */
    private ExecutorConfig executor;

    public Boolean getEnabled() {

        return enabled;
    }

    public void setEnabled(Boolean enabled) {

        this.enabled = enabled;
    }

    public String getAdminAddresses() {

        return adminAddresses;
    }

    public void setAdminAddresses(String adminAddresses) {

        this.adminAddresses = adminAddresses;
    }

    public String getAccessToken() {

        return accessToken;
    }

    public void setAccessToken(String accessToken) {

        this.accessToken = accessToken;
    }

    public ExecutorConfig getExecutor() {

        return executor;
    }

    public void setExecutor(ExecutorConfig executor) {

        this.executor = executor;
    }

    public static class ExecutorConfig {
        /**
         * The xxl-job executor app name.
         */
        private String  appName;
        /**
         * The xxl-job executor registry-address: default use address to registry , otherwise use ip:port if address is null.
         */
        private String  address;
        /**
         * The xxl-job executor server-info.
         */
        private String  ip;
        /**
         * The xxl-job executor server port.
         */
        private Integer port;
        /**
         * The xxl-job executor log-path.
         */
        private String  logPath;
        /**
         * The xxl-job executor log-retention-days.
         */
        private Integer logRetentionDays;

        public String getAppName() {

            return appName;
        }

        public void setAppName(String appName) {

            this.appName = appName;
        }

        public String getAddress() {

            return address;
        }

        public void setAddress(String address) {

            this.address = address;
        }

        public String getIp() {

            return ip;
        }

        public void setIp(String ip) {

            this.ip = ip;
        }

        public Integer getPort() {

            return port;
        }

        public void setPort(Integer port) {

            this.port = port;
        }

        public String getLogPath() {

            return logPath;
        }

        public void setLogPath(String logPath) {

            this.logPath = logPath;
        }

        public Integer getLogRetentionDays() {

            return logRetentionDays;
        }

        public void setLogRetentionDays(Integer logRetentionDays) {

            this.logRetentionDays = logRetentionDays;
        }
    }

}
