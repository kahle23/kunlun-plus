package artoria.mail;

/**
 * Email config object.
 * @author Kahle
 */
public class Config {
    private String smtpHost;
    private Integer smtpPort;
    private String imapHost;
    private Integer imapPort;
    private String pop3Host;
    private Integer pop3Port;
    private String storeProtocol;
    private Boolean debug;
    private Boolean sslOnConnect;
    private String user;
    private String password;

    public String getSmtpHost() {

        return this.smtpHost;
    }

    public Config setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
        return this;
    }

    public Integer getSmtpPort() {

        return this.smtpPort;
    }

    public Config setSmtpPort(Integer smtpPort) {
        this.smtpPort = smtpPort;
        return this;
    }

    public String getImapHost() {

        return this.imapHost;
    }

    public Config setImapHost(String imapHost) {
        this.imapHost = imapHost;
        return this;
    }

    public Integer getImapPort() {

        return this.imapPort;
    }

    public Config setImapPort(Integer imapPort) {
        this.imapPort = imapPort;
        return this;
    }

    public String getPop3Host() {

        return this.pop3Host;
    }

    public Config setPop3Host(String pop3Host) {
        this.pop3Host = pop3Host;
        return this;
    }

    public Integer getPop3Port() {

        return this.pop3Port;
    }

    public Config setPop3Port(Integer pop3Port) {
        this.pop3Port = pop3Port;
        return this;
    }

    public String getStoreProtocol() {

        return this.storeProtocol;
    }

    public Config setStoreProtocol(String storeProtocol) {
        this.storeProtocol = storeProtocol;
        return this;
    }

    public Boolean getDebug() {

        return this.debug;
    }

    public Config setDebug(Boolean debug) {
        this.debug = debug;
        return this;
    }

    public Boolean getSslOnConnect() {

        return this.sslOnConnect;
    }

    public Config setSslOnConnect(Boolean sslOnConnect) {
        this.sslOnConnect = sslOnConnect;
        return this;
    }

    public String getUser() {

        return this.user;
    }

    public Config setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {

        return this.password;
    }

    public Config setPassword(String password) {
        this.password = password;
        return this;
    }

}
