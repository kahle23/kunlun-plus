package kunlun.action.mail;

import cn.hutool.core.util.ArrayUtil;
import kunlun.util.Assert;
import kunlun.util.CollUtil;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * The mail message.
 * @author Kahle
 */
public class MailMessage implements Serializable {
    /**
     * The "To" (primary) recipients.
     */
    private List<String> to;
    /**
     * The "Cc" (carbon copy) recipients.
     */
    private List<String> cc;
    /**
     * The "Bcc" (blind carbon copy) recipients.
     */
    private List<String> bcc;
    /**
     * The addresses to which replies should be directed (the RFC 822 "Reply-To" header field).
     */
    private List<String> reply;
    /**
     * The subject.
     */
    private String title;
    /**
     * The content.
     * For example, "&lt;img src="cid:test.png"&gt;"
     *      only allows for images, and it must be added as an attachment.
     *      And the "cid" is the file name of the attachment.
     */
    private String content;
    /**
     * Whether the content is html.
     */
    private Boolean html;
    /**
     * The attachments, similar to files, pictures, etc.
     * @see ByteArrayDataSource
     * @see FileDataSource
     */
    private List<DataSource> attachments;
    /**
     * Send time.
     */
    private Date sendTime;
    /**
     * The config id.
     */
    private String configId;

    public List<String> getTo() {

        return to;
    }

    public void setTo(List<String> to) {

        this.to = to;
    }

    public List<String> getCc() {

        return cc;
    }

    public void setCc(List<String> cc) {

        this.cc = cc;
    }

    public List<String> getBcc() {

        return bcc;
    }

    public void setBcc(List<String> bcc) {

        this.bcc = bcc;
    }

    public List<String> getReply() {

        return reply;
    }

    public void setReply(List<String> reply) {

        this.reply = reply;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getContent() {

        return content;
    }

    public void setContent(String content) {

        this.content = content;
    }

    public Boolean getHtml() {

        return html;
    }

    public void setHtml(Boolean html) {

        this.html = html;
    }

    public List<DataSource> getAttachments() {

        return attachments;
    }

    public void setAttachments(List<DataSource> attachments) {

        this.attachments = attachments;
    }

    public Date getSendTime() {

        return sendTime;
    }

    public void setSendTime(Date sendTime) {

        this.sendTime = sendTime;
    }

    public String getConfigId() {

        return configId;
    }

    public void setConfigId(String configId) {

        this.configId = configId;
    }

    /**
     * The mail builder.
     * @author Kahle
     */
    public static class Builder implements kunlun.core.Builder {

        public static Builder of(String configId) {

            return of().setConfigId(configId);
        }

        public static Builder of() {

            return new Builder();
        }

        private List<String> to = new ArrayList<String>();
        private List<String> cc = new ArrayList<String>();
        private List<String> bcc = new ArrayList<String>();
        private List<String> reply = new ArrayList<String>();
        private String title;
        private String content;
        private boolean html = false;
        private List<DataSource> attachments = new ArrayList<DataSource>();
        private Date sendTime = new Date();
        private String configId;

        public List<String> getTo() {

            return to;
        }

        public Builder setTo(List<String> to) {
            if (to == null) { return this; }
            this.to = to;
            return this;
        }

        public Builder addTo(String... to) {
            List<String> list = Arrays.asList(to);
            if (!list.isEmpty()) {
                this.to.addAll(list);
            }
            return this;
        }

        public List<String> getCc() {

            return cc;
        }

        public Builder setCc(List<String> cc) {
            if (cc == null) { return this; }
            this.cc = cc;
            return this;
        }

        public Builder addCc(String... cc) {
            List<String> list = Arrays.asList(cc);
            if (!list.isEmpty()) {
                this.cc.addAll(list);
            }
            return this;
        }

        public List<String> getBcc() {

            return bcc;
        }

        public Builder setBcc(List<String> bcc) {
            if (bcc == null) { return this; }
            this.bcc = bcc;
            return this;
        }

        public Builder addBcc(String... bcc) {
            List<String> list = Arrays.asList(bcc);
            if (!list.isEmpty()) {
                this.bcc.addAll(list);
            }
            return this;
        }

        public List<String> getReply() {

            return reply;
        }

        public Builder setReply(List<String> reply) {
            if (reply == null) { return this; }
            this.reply = reply;
            return this;
        }

        public Builder addReply(String... reply) {
            List<String> list = Arrays.asList(reply);
            if (!list.isEmpty()) {
                this.reply.addAll(list);
            }
            return this;
        }

        public String getTitle() {

            return title;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getContent() {

            return content;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setContent(String content, boolean html) {
            setContent(content);
            setHtml(html);
            return this;
        }

        public boolean getHtml() {

            return html;
        }

        public Builder setHtml(boolean html) {
            this.html = html;
            return this;
        }

        public List<DataSource> getAttachments() {

            return attachments;
        }

        public Builder setAttachments(List<DataSource> attachments) {
            this.attachments = Assert.notNull(attachments);
            return this;
        }

        public Builder addAttachments(List<DataSource> attachments) {
            if (CollUtil.isEmpty(attachments)) { return this; }
            this.attachments.addAll(attachments);
            return this;
        }

        public Builder addAttachment(DataSource... attachments) {
            if (ArrayUtil.isEmpty(attachments)) { return this; }
            this.attachments.addAll(Arrays.asList(attachments));
            return this;
        }

        public Builder addFiles(List<File> files) {
            if (CollUtil.isEmpty(files)) { return this; }
            for (File file : files) {
                if (file == null) { continue; }
                this.attachments.add(new FileDataSource(file));
            }
            return this;
        }

        public Builder addFile(File... files) {
            if (ArrayUtil.isEmpty(files)) { return this; }
            addFiles(Arrays.asList(files));
            return this;
        }

        public Date getSendTime() {

            return sendTime;
        }

        public Builder setSendTime(Date sendTime) {
            if (sendTime == null) { return this; }
            this.sendTime = sendTime;
            return this;
        }

        public String getConfigId() {

            return configId;
        }

        public Builder setConfigId(String configId) {
            this.configId = configId;
            return this;
        }

        @Override
        public MailMessage build() {
            MailMessage mailMessage = new MailMessage();
            mailMessage.setTo(to);
            mailMessage.setCc(cc);
            mailMessage.setBcc(bcc);
            mailMessage.setReply(reply);
            mailMessage.setTitle(title);
            mailMessage.setContent(content);
            mailMessage.setHtml(html);
            mailMessage.setAttachments(attachments);
            mailMessage.setSendTime(sendTime);
            mailMessage.setConfigId(configId);
            return mailMessage;
        }
    }

}
