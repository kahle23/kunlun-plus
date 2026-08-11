package kunlun.action.mail;

import kunlun.action.ActionUtil;
import kunlun.common.constant.Nil;
import kunlun.core.Action;
import kunlun.core.function.Function;
import kunlun.exception.ExceptionUtil;
import kunlun.util.ArrayUtil;
import kunlun.util.Assert;
import kunlun.util.CollUtil;
import kunlun.util.StrUtil;

import javax.activation.DataSource;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static kunlun.common.constant.Numbers.ZERO;
import static kunlun.common.constant.Words.SEND;

/**
 * The abstract mail action.
 * @author Kahle
 */
public abstract class AbstractMailAction implements Action {
    private Function<String, MailConfig> configSupplier;

    public Function<String, MailConfig> getConfigSupplier() {

        return configSupplier;
    }

    public void setConfigSupplier(Function<String, MailConfig> configSupplier) {

        this.configSupplier = Assert.notNull(configSupplier);
    }

    /**
     * send
     * @param config config
     * @param message message
     * @return message id or null or others
     */
    public abstract Object send(MailConfig config, MailMessage message);

    @Override
    public Object execute(String strategy, Object input, Object[] arguments) {
        if (StrUtil.isBlank(strategy) || SEND.equals(strategy)) {
            MailMessage mailMsg = (MailMessage) input;
            return send(getConfigSupplier().apply(mailMsg.getConfigId()), mailMsg);
        } else {
            throw new UnsupportedOperationException("The strategy name is not supported! ");
        }
    }

    /**
     * The wrapped dataSource.
     * @author Kahle
     */
    public static class WrappedDataSource implements DataSource {
        private final DataSource dataSource;

        public WrappedDataSource(DataSource dataSource) {

            this.dataSource = Assert.notNull(dataSource);
        }

        @Override
        public InputStream getInputStream() throws IOException {

            return dataSource.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {

            return dataSource.getOutputStream();
        }

        @Override
        public String getContentType() {
            String execute = ActionUtil.execute("media-type", getName());
            if (StrUtil.isBlank(execute)) {
                return dataSource.getContentType();
            }
            return execute;
        }

        @Override
        public String getName() {

            return dataSource.getName();
        }
    }

    /**
     * MailUtil
     * @author Kahle
     */
    public static class MailUtil {

        public static List<DataSource> wrapAttachments(List<DataSource> attachments) {
            List<DataSource> result = new ArrayList<DataSource>();
            if (CollUtil.isEmpty(attachments)) { return result; }
            for (DataSource attachment : attachments) {
                if (attachment == null) { continue; }
                result.add(new WrappedDataSource(attachment));
            }
            return result;
        }

        public static InternetAddress[] parseAddress(List<String> addresses, Charset charset) {
            List<InternetAddress> resultList = new ArrayList<InternetAddress>(addresses.size());
            InternetAddress[] addrs;
            for (String address : addresses) {
                addrs = parseAddress(address, charset);
                if (ArrayUtil.isNotEmpty(addrs)) {
                    Collections.addAll(resultList, addrs);
                }
            }
            return resultList.toArray(new InternetAddress[ZERO]);
        }

        public static InternetAddress[] parseAddress(String address, Charset charset) {
            try {
                InternetAddress[] addresses = InternetAddress.parse(address);
                // Encoded the username.
                if (ArrayUtil.isNotEmpty(addresses)) {
                    String charsetStr = charset == null ? null : charset.name();
                    for (InternetAddress internetAddress : addresses) {
                        internetAddress.setPersonal(internetAddress.getPersonal(), charsetStr);
                    }
                }
                return addresses;
            } catch (Exception e) { throw ExceptionUtil.wrap(e); }
        }

        public static String encodeText(String text, String charset) {
            try {
                return MimeUtility.encodeText(text, charset, Nil.STR);
            } catch (UnsupportedEncodingException e) { /* ignore */ }
            return text;
        }
    }

}
