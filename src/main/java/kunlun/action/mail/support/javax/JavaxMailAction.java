package kunlun.action.mail.support.javax;

import kunlun.action.mail.AbstractMailAction;
import kunlun.action.mail.MailConfig;
import kunlun.action.mail.MailMessage;
import kunlun.exception.ExceptionUtil;
import kunlun.util.CollUtil;
import kunlun.util.ObjUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

import static kunlun.common.constant.Numbers.ZERO;
import static kunlun.util.StrUtil.isNotBlank;

public class JavaxMailAction extends AbstractMailAction {
    private static final Logger log = LoggerFactory.getLogger(JavaxMailAction.class);

    private static final String MAIL_PROTOCOL = "mail.transport.protocol";
    private static final String SMTP_HOST = "mail.smtp.host";
    private static final String SMTP_PORT = "mail.smtp.port";
    private static final String SMTP_AUTH = "mail.smtp.auth";

    private static final String SSL_ENABLE = "mail.smtp.ssl.enable";

    private static final String MAIL_DEBUG = "mail.debug";

    public Properties getSmtpProps(MailConfig config) {
        // Global system parameters
        final Properties p = new Properties();
        p.put(MAIL_PROTOCOL, "smtp");
        p.put(SMTP_HOST, config.getSmtpHost());
        p.put(SMTP_PORT, String.valueOf(config.getSmtpPort()));
        p.put(SMTP_AUTH, String.valueOf(!ObjUtil.isEmpty(config.getPassword())));
        if (config.getDebug() != null) {
            p.put(MAIL_DEBUG, String.valueOf(config.getDebug()));
        }
        // SSL
        if (config.getSmtpSslEnable() != null) {
            p.put(SSL_ENABLE, String.valueOf(config.getSmtpSslEnable()));
        }
        // Add custom attributes, allowing custom attributes to override the values that have already been set.
        if (config.getOtherProperties() != null) {
            p.putAll(config.getOtherProperties());
        }
        return p;
    }

    public Session getSession(final MailConfig config, final boolean isSingleton) {
        Authenticator authenticator = null;
        if (!ObjUtil.isEmpty(config.getPassword())) {
            authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {

                    return new PasswordAuthentication(config.getUsername(), String.valueOf(config.getPassword()));
                }
            };
        }
        return isSingleton ? Session.getDefaultInstance(getSmtpProps(config), authenticator)
                : Session.getInstance(getSmtpProps(config), authenticator);
    }

    protected void fillAttachments(Multipart multipart, MailConfig config, MailMessage message) throws MessagingException {
        if (CollUtil.isEmpty(message.getAttachments())) { return; }
        List<DataSource> attachments = MailUtil.wrapAttachments(message.getAttachments());
        MimeBodyPart bodyPart;
        String nameEncoded;
        for (DataSource attachment : attachments) {
            bodyPart = new MimeBodyPart();
            bodyPart.setDataHandler(new DataHandler(attachment));
            // The file name.
            nameEncoded = attachment.getName();
            if (config.getEncodeFilename()) {
                nameEncoded = MailUtil.encodeText(nameEncoded, config.getCharset());
            }
            bodyPart.setFileName(nameEncoded);
//            if (StrUtil.startWith(attachment.getContentType(), "image/")) {
            if (isNotBlank(attachment.getContentType())
                    && attachment.getContentType().startsWith("image/")) {
                // Image attachment, for citing images in the main text.
                bodyPart.setContentID(nameEncoded);
                bodyPart.setDisposition(MimeBodyPart.INLINE);
            }
            multipart.addBodyPart(bodyPart);
        }
    }

    protected Multipart buildContent(MailConfig config, MailMessage message) throws MessagingException {
        Multipart multipart = new MimeMultipart();
        // Fill the attachments.
        fillAttachments(multipart, config, message);
        // The text.
        String charset = isNotBlank(config.getCharset()) ? config.getCharset() : MimeUtility.getDefaultJavaCharset();
        boolean isHtml = message.getHtml() != null && message.getHtml();
        String type = String.format("text/%s; charset=%s", (isHtml ? "html" : "plain"), charset);
        MimeBodyPart body = new MimeBodyPart();
        body.setContent(message.getContent(), type);
        multipart.addBodyPart(body);
        return multipart;
    }

    @Override
    public String send(MailConfig config, MailMessage message) {
        try {
            // Get the config charset.
            Charset charset = isNotBlank(config.getCharset()) ? Charset.forName(config.getCharset()) : null;
            // Build the mail mime message.
            MimeMessage msg = new MimeMessage(getSession(config, false));
            // The mail sender.
            String displayName = config.getDisplayName();
            String username = config.getUsername();
            if (isNotBlank(displayName)) {
                msg.setFrom(MailUtil.parseAddress(displayName + "<" + username + ">", charset)[ZERO]);
            } else {
                msg.setFrom(MailUtil.parseAddress(username, charset)[ZERO]);
            }
            // The mail subject.
            msg.setSubject(message.getTitle(), (charset == null) ? null : charset.name());
            // The mail sent date.
            msg.setSentDate(message.getSendTime());
            // The mail content and attachments.
            msg.setContent(buildContent(config, message));
            // The "To" (primary) recipients.
            msg.setRecipients(MimeMessage.RecipientType.TO, MailUtil.parseAddress(message.getTo(), charset));
            // The "Cc" (carbon copy) recipients.
            if (CollUtil.isNotEmpty(message.getCc())) {
                msg.setRecipients(MimeMessage.RecipientType.CC, MailUtil.parseAddress(message.getCc(), charset));
            }
            // The "Bcc" (blind carbon copy) recipients.
            if (CollUtil.isNotEmpty(message.getBcc())) {
                msg.setRecipients(MimeMessage.RecipientType.BCC, MailUtil.parseAddress(message.getBcc(), charset));
            }
            // The reply-to.
            if (CollUtil.isNotEmpty(message.getReply())) {
                msg.setReplyTo(MailUtil.parseAddress(message.getReply(), charset));
            }
            // Do send.
            Transport.send(msg);
            // The result.
            return msg.getMessageID();
        } catch (Exception e) { throw ExceptionUtil.wrap(e); }
    }

}
