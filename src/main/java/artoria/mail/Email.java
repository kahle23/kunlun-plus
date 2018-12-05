package artoria.mail;

import artoria.exception.UncheckedException;
import artoria.io.IOUtils;
import artoria.time.DateUtils;
import artoria.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.*;

import static artoria.common.Constants.*;
import static javax.mail.Flags.Flag.SEEN;
import static javax.mail.Message.RecipientType.*;

/**
 * Email object.
 * @author Kahle
 */
public class Email {
    private static final String APPLICATION_ALL = "application/*";
    private static final String MESSAGE_RFC822 = "message/rfc822";
    private static final String MULTIPART_ALL = "multipart/*";
    private static final String X_PRIORITY = "X-Priority";
    private static final String STRING_NAME = "name";
    private static final String TEXT_ALL = "text/*";
    private static Logger log = LoggerFactory.getLogger(Email.class);

    private String charset = DEFAULT_CHARSET_NAME;
    private List<Address> from = new ArrayList<Address>();
    private List<Address> to = new ArrayList<Address>();
    private List<Address> cc = new ArrayList<Address>();
    private List<Address> bcc = new ArrayList<Address>();
    private Date sentDate;
    private String subject;
    private String textContent;
    private String htmlContent;
    private Map<String, File> files = new HashMap<String, File>();
    private Boolean debug;

    private String messageId;
    private String priority;
    private Integer size;
    private Boolean isRead;
    private Message message;
    private boolean isTextEmail = false;
    private boolean hasAttach = false;

    public String getCharset() {

        return this.charset;
    }

    public Email setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public List<Address> getFrom() {

        return this.from;
    }

    public Email addFrom(List<Address> from) {
        this.from.addAll(from);
        return this;
    }

    public Email addFrom(String... from) {
        try {
            for (String s : from) {
                Address[] parse = InternetAddress.parse(s);
                this.from.addAll(Arrays.asList(parse));
            }
            return this;
        }
        catch (Exception e) {
            throw new UncheckedException(e);
        }
    }

    public Email addFrom(String from, String displayName) {
        try {
            if (StringUtils.isNotBlank(displayName)) {
                String encodeName = MimeUtility.encodeText(displayName);
                this.from.add(new InternetAddress(encodeName + " <" + from + ">"));
            }
            else {
                Address[] parse = InternetAddress.parse(from);
                this.from.addAll(Arrays.asList(parse));
            }
            return this;
        }
        catch (Exception e) {
            throw new UncheckedException(e);
        }
    }

    public Email clearFrom() {
        this.from.clear();
        return this;
    }

    public List<Address> getTo() {
        return to;
    }

    public Email addTo(List<Address> to) {
        this.to.addAll(to);
        return this;
    }

    public Email addTo(String... to) {
        try {
            for (String s : to) {
                Address[] parse = InternetAddress.parse(s);
                this.to.addAll(Arrays.asList(parse));
            }
            return this;
        } catch (AddressException e) {
            throw new RuntimeException(e);
        }
    }

    public Email clearTo() {
        this.to.clear();
        return this;
    }

    public List<Address> getCc() {

        return this.cc;
    }

    public Email addCc(List<Address> cc) {
        this.cc.addAll(cc);
        return this;
    }

    public Email addCc(String... cc) {
        try {
            for (String s : cc) {
                Address[] parse = InternetAddress.parse(s);
                this.cc.addAll(Arrays.asList(parse));
            }
            return this;
        }
        catch (Exception e) {
            throw new UncheckedException(e);
        }
    }

    public Email clearCc() {
        this.cc.clear();
        return this;
    }

    public List<Address> getBcc() {

        return this.bcc;
    }

    public Email addBcc(List<Address> bcc) {
        this.bcc.addAll(bcc);
        return this;
    }

    public Email addBcc(String... bcc) {
        try {
            for (String s : bcc) {
                Address[] parse = InternetAddress.parse(s);
                this.bcc.addAll(Arrays.asList(parse));
            }
            return this;
        }
        catch (Exception e) {
            throw new UncheckedException(e);
        }
    }

    public Email clearBcc() {
        this.bcc.clear();
        return this;
    }

    public Date getSentDate() {

        return this.sentDate;
    }

    public Email setSentDate(Date sentDate) {
        this.sentDate = sentDate;
        return this;
    }

    public String getSubject() {

        return this.subject;
    }

    public Email setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getTextContent() {

        return this.textContent;
    }

    public Email setTextContent(String textContent) {
        this.textContent = textContent;
        return this;
    }

    public String getHtmlContent() {

        return this.htmlContent;
    }

    public Email setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
        return this;
    }

    public Map<String, File> getFiles() {

        return this.files;
    }

    public Email addFiles(List<File> files) {
        for (File file : files) {
            this.files.put(file.getName(), file);
        }
        return this;
    }

    public Email addFiles(File... files) {
        this.addFiles(Arrays.asList(files));
        return this;
    }

    public Email addFile(String fName, File file) {
        fName = StringUtils.isBlank(fName) ? file.getName() : fName;
        this.files.put(fName, file);
        return this;
    }

    public Boolean getDebug() {

        return this.debug;
    }

    public Email setDebug(Boolean debug) {
        this.debug = debug;
        return this;
    }

    public String getMessageId() {

        return this.messageId;
    }

    public Email setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public String getPriority() {

        return this.priority;
    }

    public Email setPriority(String priority) {
        this.priority = priority;
        return this;
    }

    public Integer getSize() {

        return this.size;
    }

    public Email setSize(Integer size) {
        this.size = size;
        return this;
    }

    public Boolean getIsRead() {

        return this.isRead;
    }

    public Email setIsRead(Boolean isRead) {
        this.isRead = isRead;
        return this;
    }

    public Message getMessage() {

        return this.message;
    }

    public Email setMessage(Message message) {
        this.message = message;
        return this;
    }

    public boolean getIsTextEmail() {

        return this.isTextEmail;
    }

    public Email setIsTextEmail(boolean isTextEmail) {
        this.isTextEmail = isTextEmail;
        return this;
    }

    public boolean getHasAttach() {

        return this.hasAttach;
    }

    public Email setHasAttach(boolean hasAttach) {
        this.hasAttach = hasAttach;
        return this;
    }

    public Email saveAttach(File path) throws IOException, MessagingException {
        if (!path.exists() && !path.mkdirs()) {
            throw new IOException("Create directory \"" + path + "\" failure. ");
        }
        if (hasAttach && MapUtils.isEmpty(this.files)) {
            List<File> files = Email.saveAttach(message, path);
            if (CollectionUtils.isEmpty(files)) { return this; }
            this.addFiles(files);
        }
        return this;
    }

    public MimeMessage build(Session session) throws IOException, MessagingException {
        Assert.notNull(session, "Parameter \"session\" must not blank. ");
        if (debug != null) {
            session.setDebug(debug);
        }
        MimeMessage message = new MimeMessage(session);
        Assert.notEmpty(from, "Parameter \"from\" must not blank. ");
        Address[] tmp = new Address[from.size()];
        from.toArray(tmp);
        message.addFrom(tmp);
        Assert.notEmpty(to, "Parameter \"to\" must not blank. ");
        tmp = new Address[to.size()];
        to.toArray(tmp);
        message.addRecipients(Message.RecipientType.TO, tmp);
        if (CollectionUtils.isNotEmpty(cc)) {
            tmp = new Address[cc.size()];
            cc.toArray(tmp);
            message.addRecipients(Message.RecipientType.CC, tmp);
        }
        if (CollectionUtils.isNotEmpty(bcc)) {
            tmp = new Address[bcc.size()];
            bcc.toArray(tmp);
            message.addRecipients(Message.RecipientType.BCC, tmp);
        }
        if (sentDate != null) {
            message.setSentDate(sentDate);
        }
        if (StringUtils.isNotBlank(subject)) {
            message.setSubject(subject, charset);
        }
        if (StringUtils.isNotBlank(htmlContent) || MapUtils.isNotEmpty(files)) {
            MimeMultipart mimeMultipart = new MimeMultipart();
            if (StringUtils.isNotBlank(htmlContent)) {
                String contentType = String.format("text/html; charset=%s", charset.toLowerCase());
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent(htmlContent, contentType);
                mimeMultipart.addBodyPart(mimeBodyPart);
            }
            if (MapUtils.isNotEmpty(files)) {
                for (Map.Entry<String, File> entry : files.entrySet()) {
                    MimeBodyPart filePart = new MimeBodyPart();
                    filePart.setDataHandler(new DataHandler(new FileDataSource(entry.getValue())));
                    filePart.setFileName(MimeUtility.encodeWord(entry.getKey(), charset, "B"));
                    mimeMultipart.addBodyPart(filePart);
                }
            }
            message.setContent(mimeMultipart);
        }
        else if (StringUtils.isNotBlank(textContent)) {
            message.setText(textContent, charset);
        }
        return message;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MessageId   : ").append(messageId).append(NEWLINE);
        String fromStr = from.size() > 2 ? Email.serializeAddress(from.subList(0, 2)) + " ..." : Email.serializeAddress(from);
        builder.append("From        : ").append(fromStr).append(NEWLINE);
        String toStr = to.size() > 2 ? Email.serializeAddress(to.subList(0, 2)) + " ..." : Email.serializeAddress(to);
        builder.append("To          : ").append(toStr).append(NEWLINE);
        if (CollectionUtils.isNotEmpty(cc)) {
            String ccStr = cc.size() > 2 ? Email.serializeAddress(cc.subList(0, 2)) + " ..." : Email.serializeAddress(cc);
            builder.append("Cc          : ").append(ccStr).append(NEWLINE);
        }
        if (CollectionUtils.isNotEmpty(bcc)) {
            String bccStr = bcc.size() > 2 ? Email.serializeAddress(bcc.subList(0, 2)) + " ..." : Email.serializeAddress(bcc);
            builder.append("Bcc         : ").append(bccStr).append(NEWLINE);
        }
        builder.append("SentDate    : ").append(DateUtils.create(sentDate).toString()).append(NEWLINE);
        builder.append("Priority    : ").append(priority).append(NEWLINE);
        builder.append("Size        : ").append(size).append(" Byte").append(NEWLINE);
        builder.append("HasAttach   : ").append(hasAttach).append(NEWLINE);
        builder.append("IsRead      : ").append(isRead).append(NEWLINE);
        builder.append("IsTextEmail : ").append(isTextEmail).append(NEWLINE);
        String subject = this.subject.trim();
        subject = subject.length() > 36 ? subject.substring(0, 36) : subject;
        builder.append("Subject     : ").append(subject).append(NEWLINE);
        String content = isTextEmail ? textContent : htmlContent;
        content = content.trim();
        content = content.length() > 200 ? content.substring(0, 200) : content;
        builder.append("Summary     : ").append(content).append(NEWLINE);
        return builder.toString();
    }

    public static Email parse(Message message) throws IOException, MessagingException {
        Email email = new Email();
        email.setMessage(message);
        if (message instanceof MimeMessage) {
            email.setMessageId(((MimeMessage) message).getMessageID());
        }
        String[] xPrioHeaders = message.getHeader(X_PRIORITY);
        email.setPriority(ArrayUtils.isNotEmpty(xPrioHeaders) ? xPrioHeaders[0] : null);
        email.setSize(message.getSize());
        Flags flags = message.getFlags();
        email.setIsRead(flags.contains(SEEN));
        email.setSentDate(message.getSentDate());
        Address[] from = message.getFrom();
        email.addFrom(Arrays.asList(from));
        Address[] recipients = message.getRecipients(TO);
        email.addTo(Arrays.asList(recipients));
        Address[] tmp = message.getRecipients(CC);
        if (tmp != null) {
            email.addCc(Arrays.asList(tmp));
        }
        tmp = message.getRecipients(BCC);
        if (tmp != null) {
            email.addBcc(Arrays.asList(tmp));
        }
        String subject = message.getSubject();
        subject = MimeUtility.decodeText(subject);
        email.setSubject(subject);
        String contentType = message.getContentType();
        boolean contains = contentType.contains(STRING_NAME);
        if (message.isMimeType(TEXT_ALL) && !contains) {
            email.setIsTextEmail(true);
            email.setTextContent(message.getContent().toString());
        }
        else {
            email.setIsTextEmail(false);
            email.setHtmlContent(Email.takeContent(message));
        }
        email.setHasAttach(Email.containAttach(message));
        return email;
    }

    private static String serializeAddress(Address address) {
        try {
            if (address instanceof InternetAddress) {
                InternetAddress addr = (InternetAddress) address;
                String nickname = addr.getPersonal();
                nickname = StringUtils.isNotBlank(nickname)
                        ? MimeUtility.decodeText(nickname) : EMPTY_STRING;
                return nickname + "<" + addr.getAddress() + ">";
            }
            else {
                return address.toString();
            }
        }
        catch (Exception e) {
            throw new UncheckedException(e);
        }
    }

    private static String serializeAddress(List<Address> addresses) {
        StringBuilder builder = new StringBuilder();
        for (Address address : addresses) {
            String addressStr = Email.serializeAddress(address);
            builder.append(addressStr).append(", ");
        }
        builder.deleteCharAt(builder.length() - 2);
        return builder.toString();
    }

    private static String takeContent(Part part) throws IOException, MessagingException {
        StringBuilder builder = new StringBuilder();
        boolean containAttach = part.getContentType().contains(STRING_NAME);
        if (part.isMimeType(TEXT_ALL) && !containAttach) {
            builder.append(part.getContent().toString());
        }
        else if (part.isMimeType(MESSAGE_RFC822)) {
            builder.append(Email.takeContent((Part) part.getContent()));
        }
        else if (part.isMimeType(MULTIPART_ALL)) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                builder.append(Email.takeContent(bodyPart));
            }
        }
        return builder.toString();
    }

    private static boolean containAttach(Part part) throws IOException, MessagingException {
        boolean hasAttach = false;
        if (part.isMimeType(MULTIPART_ALL)) {
            // Handle multipart
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bPart = multipart.getBodyPart(i);
                String disp = bPart.getDisposition();
                boolean isAttach = disp != null, isInline = isAttach;
                isAttach = isAttach && disp.equalsIgnoreCase(Part.ATTACHMENT);
                isInline = isInline && disp.equalsIgnoreCase(Part.INLINE);
                if (isAttach || isInline ||
                        bPart.isMimeType(APPLICATION_ALL) ||
                        bPart.getContentType().contains(STRING_NAME)) {
                    hasAttach = true;
                }
                else if (bPart.isMimeType(MULTIPART_ALL)) {
                    hasAttach = Email.containAttach(bPart);
                }
                if (hasAttach) { break; }
            }
        }
        else if (part.isMimeType(MESSAGE_RFC822)) {
            hasAttach = Email.containAttach((Part) part.getContent());
        }
        return hasAttach;
    }

    private static List<File> saveAttach(Part part, File dir) throws IOException, MessagingException {
        List<File> files = new ArrayList<File>();
        if (part.isMimeType(MULTIPART_ALL)) {
            // Handle multipart
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bPart = multipart.getBodyPart(i);
                String disp = bPart.getDisposition();
                boolean isAttach = disp != null, isInline = isAttach;
                boolean hasName = isAttach, isApplication = isAttach;
                isAttach = isAttach && disp.equalsIgnoreCase(Part.ATTACHMENT);
                isInline = isInline && disp.equalsIgnoreCase(Part.INLINE);
                hasName = hasName && bPart.getContentType().contains(STRING_NAME);
                isApplication = isApplication && bPart.isMimeType(APPLICATION_ALL);
                if (isAttach || isInline || hasName || isApplication) {
                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        in = bPart.getInputStream();
                        String fName = bPart.getFileName();
                        fName = MimeUtility.decodeText(fName);
                        File dest = new File(dir, fName);
                        // Prevent dest file already exist.
                        if (!dest.createNewFile()) {
                            throw new IOException("Create file \"" + dest + "\" failure. ");
                        }
                        files.add(dest);
                        out = new FileOutputStream(dest);
                        IOUtils.copyLarge(in, out);
                        out.flush();
                    }
                    finally {
                        IOUtils.closeQuietly(in);
                        IOUtils.closeQuietly(out);
                    }
                }
                else if (bPart.isMimeType(MULTIPART_ALL)) {
                    files.addAll(Email.saveAttach(bPart, dir));
                }
            }
        }
        else if (part.isMimeType(MESSAGE_RFC822)) {
            files.addAll(Email.saveAttach((Part) part.getContent(), dir));
        }
        return files;
    }

}
