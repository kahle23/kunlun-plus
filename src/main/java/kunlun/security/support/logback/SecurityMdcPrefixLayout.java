package kunlun.security.support.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Map;

import static kunlun.security.support.AbstractSecurityContext.*;

/**
 * @see org.slf4j.TtlLogbackMdcAdapter
 */
public class SecurityMdcPrefixLayout extends PatternLayout {

    protected String createPrefix(Map<String, String> mdc) {
        String result = "";
        if (MapUtil.isEmpty(mdc)) { return result; }
        String appKey = "APP", envKey = "ENV", cliKey = "CLI", servKey = "SERV";
        // 服务器IP 不为空，说明服务信息进入 MDC 了
        if (mdc.containsKey(servKey)) {
            result = result + String.format("CLI:%s SERV:%s ",
                    mdc.get(cliKey),
                    mdc.get(servKey)
            );
        }
        // 请求ID不为空，说明请求信息进入 MDC 了
        result += "TID:" + mdc.get(TRACE_ID_NAME) + " ";
        result += "P:" + mdc.get(PLATFORM_NAME) + " ";
        if (StrUtil.isNotBlank(mdc.get(TENANT_ID_NAME))) {
            result += "T:" + mdc.get(TENANT_ID_NAME) + " ";
        }
        result += "UID:" + mdc.get(USER_ID_NAME) + " ";
        if (StrUtil.isNotBlank(mdc.get(USER_TYPE_NAME))) {
            result += "UTP:" + mdc.get(USER_TYPE_NAME) + " ";
        }

        return result;
    }

    @Override
    public String doLayout(ILoggingEvent event) {

        return createPrefix(event.getMDCPropertyMap()) + super.doLayout(event);
    }

}
