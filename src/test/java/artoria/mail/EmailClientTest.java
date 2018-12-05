package artoria.mail;

import artoria.time.DateUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import java.io.File;
import java.util.List;
import java.util.Map;

import static artoria.common.Constants.NEWLINE;

@Ignore
public class EmailClientTest {
    private static Logger log = LoggerFactory.getLogger(EmailClientTest.class);
    private static EmailClient client;
    private static Email email;
    private static String to = "to@email.com";

    static {
        Config config = new Config()
                .setSmtpHost("smtp.host.com")
                .setSmtpPort(465)
                .setImapHost("imap.host.com")
                .setImapPort(993)
                .setStoreProtocol("imap")
                .setSslOnConnect(true)
                .setDebug(false);
        // Can use default email config file
        client = new EmailClient()
        // client = new EmailClient("E:\\Project4j\\mine\\hirasawa\\hirasawa-mail\\target\\test-classes\\email.properties")
                // .setConfig(config)
                // .setUser("user@email.com")
                // .setPassword("password")
        ;
        email = new Email()
                .addFrom(client.getUser(), "Email Test User")
                .addTo(to)
                .setSentDate(DateUtils.create(1995, 5, 5).getDate())
                .setSubject("Email Test")
                .setHtmlContent("<html><h1>Email Test</h1><br />This is a test email.<br /><img src=\"" +
                        "https://github.com/Luck.jpg\" /><br /><br /></html>")
                // .addFiles(new File("E:\\test.csv"))
        ;
    }

    @Test
    public void sendMail() throws Exception {

        client.send(email);
    }

    @Test
    public void sendMailMultiThread() throws Exception {
        for (int i = 0; i < 4; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 3; j++) {
                        try {
                            client.send(email);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
        Thread.sleep(20000);
    }

    @Test
    public void getAllFolders() throws Exception {
        Store store = client.getStore();
        Folder[] list = client.getAllFolders(store);
        for (Folder folder : list) {
            log.info(">>>>>>>>" + folder.getFullName() + " > " + folder.getName());
            // Only folder open, need close, in here is test.
            client.closeQuietly(folder, false);
        }
        client.closeQuietly(store);
    }

    @Test
    public void receiveMail() throws Exception {
        Store store = client.getStore();
        Folder inbox = client.getInbox(store);

        int messageCount = inbox.getMessageCount();
        Message[] messages = inbox.getMessages(messageCount - 10, messageCount);
        List<Email> emails = client.convert(messages);

        for (Email email : emails) {
            log.info(NEWLINE + email);
        }

        client.closeQuietly(inbox);
        client.closeQuietly(store);
    }

    @Test
    public void receiveMailAndSaveAttach() throws Exception {
        Store store = client.getStore();
        Folder inbox = client.getInbox(store);

        int messageCount = inbox.getMessageCount();
        log.info("Message count: " + messageCount);
        Message message = inbox.getMessages(messageCount, messageCount)[0];
        Email parse = Email.parse(message);
        log.info(NEWLINE + parse);

        if (parse.getHasAttach()) {
            Map<String, File> files = parse.saveAttach(
                    new File("E:\\_cache")).getFiles();
            for (Map.Entry<String, File> entry : files.entrySet()) {
                log.info(">>>> " + entry.getKey() + " >> " + entry.getValue());
            }
        }

        client.closeQuietly(inbox);
        client.closeQuietly(store);
    }

}
