package kunlun.action.mail;

import java.io.Serializable;
import java.util.Map;

/**
 * The mail config.
 * @author Kahle
 */
public class MailConfig implements Serializable {
    private String  charset;
    private String  smtpHost;
    private Integer smtpPort;
    private Boolean smtpSslEnable;

    private String displayName;
    private String username;
    private char[] password;
    private Map<String, Object> otherProperties;
    private Boolean debug;

    private Boolean encodeFilename = true;

    public String getCharset() {

        return charset;
    }

    public void setCharset(String charset) {

        this.charset = charset;
    }

    public String getSmtpHost() {

        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {

        this.smtpHost = smtpHost;
    }

    public Integer getSmtpPort() {

        return smtpPort;
    }

    public void setSmtpPort(Integer smtpPort) {

        this.smtpPort = smtpPort;
    }

    public Boolean getSmtpSslEnable() {

        return smtpSslEnable;
    }

    public void setSmtpSslEnable(Boolean smtpSslEnable) {

        this.smtpSslEnable = smtpSslEnable;
    }

    public String getDisplayName() {

        return displayName;
    }

    public void setDisplayName(String displayName) {

        this.displayName = displayName;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public char[] getPassword() {

        return password;
    }

    public void setPassword(char[] password) {

        this.password = password;
    }

    public Map<String, Object> getOtherProperties() {

        return otherProperties;
    }

    public void setOtherProperties(Map<String, Object> otherProperties) {

        this.otherProperties = otherProperties;
    }

    public Boolean getDebug() {

        return debug;
    }

    public void setDebug(Boolean debug) {

        this.debug = debug;
    }

    public Boolean getEncodeFilename() {

        return encodeFilename;
    }

    public void setEncodeFilename(Boolean encodeFilename) {

        this.encodeFilename = encodeFilename;
    }
}
