package artoria.mail;

import artoria.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Email client object.
 * @author Kahle
 */
public class EmailClient {
    private static Logger log = LoggerFactory.getLogger(EmailClient.class);
    private static final String EMAIL_CONFIG_NAME = "mail.properties";
    private static final String INBOX = "INBOX";
    private Properties config;
    private String user;
    private String password;
    private Session session;

    public EmailClient() {
        String path = PathUtils.findClasspath(EMAIL_CONFIG_NAME);
        File file;
        if (StringUtils.isBlank(path) ||
                !(file = new File(path)).exists()) {
            return;
        }
        log.info("Find default email config file \"" + EMAIL_CONFIG_NAME + "\" in classpath. ");
        Properties properties = PropertiesUtils.create(file);
        this.setConfig(properties);
    }

    public EmailClient(String path) {
        File file = new File(path);
        Assert.state(file.exists(), "Can not find email config file \"" + path + "\". ");
        Properties properties = PropertiesUtils.create(file);
        this.setConfig(properties);
    }

    private Session getSession() {
        if (session == null) {
            // session = Session.getInstance(config, null);
            session = Session.getDefaultInstance(config, null);
        }
        return session;
    }

    public Properties getConfig() {

        return config;
    }

    public EmailClient setConfig(Properties config) {
        Assert.notNull(config, "Parameter \"parameter\" must not null. ");
        this.config = config;
        this.user = config.getProperty("mail.user");
        this.password = config.getProperty("mail.password");
        return this;
    }

    public EmailClient setConfig(Config config) {
        Assert.notNull(config, "Parameter \"parameter\" must not null. ");
        Properties properties = new Properties();
        properties.putAll(System.getProperties());
        String smtpHost = config.getSmtpHost();
        if (StringUtils.isNotBlank(smtpHost)) {
            properties.setProperty("mail.smtp.host", smtpHost);
        }
        Integer smtpPort = config.getSmtpPort();
        if (smtpPort != null) {
            properties.setProperty("mail.smtp.port", smtpPort + "");
        }
        String imapHost = config.getImapHost();
        if (StringUtils.isNotBlank(imapHost)) {
            properties.setProperty("mail.imap.host", imapHost);
        }
        Integer imapPort = config.getImapPort();
        if (imapPort != null) {
            properties.setProperty("mail.imap.port", imapPort + "");
        }
        String pop3Host = config.getPop3Host();
        if (StringUtils.isNotBlank(pop3Host)) {
            properties.setProperty("mail.pop3.host", pop3Host);
        }
        Integer pop3Port = config.getPop3Port();
        if (pop3Port != null) {
            properties.setProperty("mail.pop3.port", pop3Port + "");
        }
        String storeProtocol = config.getStoreProtocol();
        if (StringUtils.isNotBlank(storeProtocol)) {
            properties.setProperty("mail.store.protocol", storeProtocol);
        }
        Boolean debug = config.getDebug();
        if (debug != null) {
            properties.setProperty("mail.debug", debug + "");
        }
        Boolean sslOnConnect = config.getSslOnConnect();
        if (sslOnConnect != null && sslOnConnect) {
            if (StringUtils.isNotBlank(smtpHost)) {
                properties.setProperty("mail.smtp.ssl.enable", "true");
            }
            if (StringUtils.isNotBlank(imapHost)) {
                properties.setProperty("mail.imap.ssl.enable", "true");
            }
        }
        this.config = properties;
        this.user = config.getUser();
        this.password = config.getPassword();
        return this;
    }

    public String getUser() {

        return user;
    }

    public EmailClient setUser(String user) {
        Assert.notBlank(user, "Parameter \"user\" must not blank. ");
        this.user = user;
        return this;
    }

    public String getPassword() {

        return password;
    }

    public EmailClient setPassword(String password) {
        Assert.notBlank(password, "Parameter \"password\" must not blank. ");
        this.password = password;
        return this;
    }

    public void send(Email... emails) throws IOException, MessagingException {
        Assert.notEmpty(emails, "Parameter \"emails\" must not empty. ");
        this.send(Arrays.asList(emails));
    }

    public void send(List<Email> emails) throws IOException, MessagingException {
        Assert.notEmpty(emails, "Parameter \"emails\" must not empty. ");
        Session session = this.getSession();
        for (Email email : emails) {
            MimeMessage message = email.build(session);
            email.setMessage(message);
            if (StringUtils.isNotBlank(user) && StringUtils.isNotBlank(password)) {
                Transport.send(message, user, password);
            }
            else {
                Transport.send(message);
            }
            email.setMessageId(message.getMessageID());
        }
    }

    public void closeQuietly(Store store) {
        if (store != null && store.isConnected()) {
            try {
                store.close();
            }
            catch (MessagingException e) {
                log.error(e.getMessage(), e);
                store = null;
            }
        }
    }

    public void closeQuietly(Folder folder) {

        this.closeQuietly(folder, false);
    }

    public void closeQuietly(Folder folder, boolean expunge) {
        if (folder != null && folder.isOpen()) {
            try {
                folder.close(expunge);
            }
            catch (MessagingException e) {
                log.error(e.getMessage(), e);
                folder = null;
            }
        }
    }

    public Store getStore() throws MessagingException {
        Store store = this.getSession().getStore();
        store.connect(user, password);
        return store;
    }

    public Folder[] getAllFolders(Store store) throws MessagingException {

        return store.getDefaultFolder().list();
    }

    public Folder getInbox(Store store) throws MessagingException {

        return this.getFolder(store, INBOX, true);
    }

    public Folder getInbox(Store store, boolean isReadOnly) throws MessagingException {

        return this.getFolder(store, INBOX, isReadOnly);
    }

    public Folder getFolder(Store store, String folderName) throws MessagingException {

        return this.getFolder(store, folderName, true);
    }

    public Folder getFolder(Store store, String folderName, boolean isReadOnly) throws MessagingException {
        Folder folder = store.getFolder(folderName);
        folder.open(isReadOnly ? Folder.READ_ONLY : Folder.READ_WRITE);
        return folder;
    }

    public List<Email> convert(Message[] messages) throws IOException, MessagingException {
        List<Email> result = new ArrayList<Email>();
        if (ArrayUtils.isEmpty(messages)) { return result; }
        for (Message message : messages) {
            result.add(Email.parse(message));
        }
        return result;
    }

}
