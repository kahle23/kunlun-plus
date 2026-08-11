package baibao.action.sms.support.aliyun;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import kunlun.action.sms.AbstractSmsAction;
import kunlun.action.sms.SmsMessage;
import kunlun.action.sms.SmsSendResult;
import kunlun.data.Dict;
import kunlun.data.json.JsonUtil;
import kunlun.exception.ExceptionUtil;
import kunlun.util.Assert;
import kunlun.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static kunlun.common.constant.Numbers.ONE;
import static kunlun.common.constant.Symbols.COMMA;

/**
 * 阿里云短信处理器
 * @see <a href="https://help.aliyun.com/document_detail/419273.html">SendSms - 发送短信</>
 * @see <a href="https://help.aliyun.com/document_detail/419277.html">QuerySendDetails - 查询短信发送详情</>
 */
public class AliYunZhSmsAction extends AbstractSmsAction {
    private static final Logger log = LoggerFactory.getLogger(AliYunZhSmsAction.class);
    protected static final String QUERY_SEND_DETAILS = "QuerySendDetails";
    protected static final String SEND_BATCH_SMS = "SendBatchSms";
    protected static final String SEND_SMS = "SendSms";

    protected IAcsClient getAcsClient(AliYunSmsConfig config) {
        IClientProfile clientProfile = DefaultProfile.getProfile(
                config.getRegion(), config.getAccessKeyId(), config.getAccessKeySecret());
        return new DefaultAcsClient(clientProfile);
    }

    protected CommonRequest buildRequest(AliYunSmsConfig config, String sysAction) {
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(config.getSysDomain());
        request.setSysVersion(config.getSysVersion());
        request.setSysAction(sysAction);
        return request;
    }

    protected void validateSmsMsg(SmsMessage smsMsg) {
        Assert.notNull(smsMsg, "Parameter \"smsMsg\" must not null. ");
        Assert.notEmpty(smsMsg.getPhoneNumbers(), "Parameter \"phoneNumbers\" must not empty. ");
        Assert.notBlank(smsMsg.getTemplateCode(), "Parameter \"templateCode\" must not blank. ");
    }

    protected SmsSendResult execute(AliYunSmsConfig config, CommonRequest request) {
        boolean debug = config.getDebug() != null && config.getDebug();
        if (debug) {
            log.info("The aliyun sms input: {}", JsonUtil.toJsonString(request));
        }
        CommonResponse response;
        try {
            response = getAcsClient(config).getCommonResponse(request);
        } catch (Exception e) { throw ExceptionUtil.wrap(e); }
        if (debug) {
            log.info("The aliyun sms output: {}", JsonUtil.toJsonString(response));
        }
        Assert.notNull(response, "The aliyun sms output is null. ");
        Assert.notNull(response.getData(), "The aliyun sms output is null. ");
        Dict data = JsonUtil.parseObject(response.getData(), Dict.class);
        String code = data.getString("Code");
        String bizId = data.getString("BizId");
        String message = data.getString("Message");
        Assert.isTrue("OK".equals(code), message);
        //
        SmsSendResult result = new SmsSendResult();
        result.setCode(code);
        result.setDescription(message);
        result.setMessageIds(Collections.singletonList(bizId));
        return result;
    }

    @Override
    public Object send(SmsConfig config, SmsMessage smsMsg) {
        validateSmsMsg(smsMsg);
        Collection<Map<String, Object>> parameters = smsMsg.getParameters();
        Collection<String> phoneNumbers = smsMsg.getPhoneNumbers();
        Collection<String> senderNames = smsMsg.getSenderNames();
        Assert.isTrue(senderNames != null && senderNames.size() == ONE
                , "Parameter \"senderNames\" must not null and size is one. ");
        Assert.isTrue(parameters != null && parameters.size() == ONE
                , "Parameter \"parameters\" must not null and size is one. ");
        //
        String templateParam = JsonUtil.toJsonString(parameters.iterator().next());
        CommonRequest request = buildRequest((AliYunSmsConfig) config, SEND_SMS);
        request.putQueryParameter("PhoneNumbers",  StrUtil.join(COMMA, phoneNumbers));
        request.putQueryParameter("SignName",      senderNames.iterator().next());
        request.putQueryParameter("TemplateCode",  smsMsg.getTemplateCode());
        request.putQueryParameter("TemplateParam", templateParam);
        return execute((AliYunSmsConfig) config, request);
    }

    @Override
    public Object batchSend(SmsConfig config, SmsMessage smsMsg) {
        validateSmsMsg(smsMsg);
        Collection<Map<String, Object>> parameters = smsMsg.getParameters();
        Collection<String> phoneNumbers = smsMsg.getPhoneNumbers();
        Collection<String> senderNames = smsMsg.getSenderNames();
        Assert.isTrue(senderNames != null && senderNames.size() == phoneNumbers.size()
                , "Parameter \"senderNames\" must not null and size equal \"phoneNumbers\" size. ");
        Assert.isTrue(parameters != null && parameters.size() == ONE
                , "Parameter \"parameters\" must not null and size equal \"phoneNumbers\" size. ");
        //
        CommonRequest request = buildRequest((AliYunSmsConfig) config, SEND_BATCH_SMS);
        request.putQueryParameter("PhoneNumberJson",   JsonUtil.toJsonString(phoneNumbers));
        request.putQueryParameter("SignNameJson",      JsonUtil.toJsonString(senderNames));
        request.putQueryParameter("TemplateCode",      smsMsg.getTemplateCode());
        request.putQueryParameter("TemplateParamJson", JsonUtil.toJsonString(parameters));
        return execute((AliYunSmsConfig) config, request);
    }

    public static class AliYunSmsConfig extends SmsConfig {
        /**
         * accessKeyId
         */
        private String accessKeyId;
        /**
         * accessKeySecret
         */
        private String accessKeySecret;
        /**
         * endpoint
         */
        private String endpoint;
        /**
         * region (cn-hangzhou)
         */
        private String region;
        /**
         * sysDomain
         */
        private String sysDomain;
        /**
         * sysVersion
         */
        private String sysVersion;

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getSysDomain() {
            return sysDomain;
        }

        public void setSysDomain(String sysDomain) {
            this.sysDomain = sysDomain;
        }

        public String getSysVersion() {
            return sysVersion;
        }

        public void setSysVersion(String sysVersion) {
            this.sysVersion = sysVersion;
        }
    }

}
