package kunlun.spring.security.support;

import com.alibaba.ttl.TransmittableThreadLocal;
import kunlun.core.AccessController;
import kunlun.core.DataController;
import kunlun.security.TokenManager;
import kunlun.security.UserService;
import kunlun.security.support.AbstractSecurityContext;
import kunlun.spring.context.SpringContext;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Map;

import static cn.hutool.core.util.StrUtil.isNotBlank;
import static kunlun.common.constant.Symbols.COLON;
import static kunlun.common.constant.Symbols.EMPTY_STRING;

/**
 * The spring security context.
 * @author Kahle
 */
public class SpringSecurityContext extends AbstractSecurityContext implements SpringContext {
    private final ApplicationContext appContext;

    public SpringSecurityContext(ApplicationContext appContext,
                                 TokenManager tokenManager,
                                 UserService userService,
                                 AccessController accessController,
                                 DataController dataController) {
        super(tokenManager, userService, accessController, dataController, new TransmittableThreadLocal<Map<String, Object>>());
        this.appContext = appContext;
        setServiceInfo(createServiceInfo());
    }

    @Override
    public ApplicationContext getSpringContext() {

        return appContext;
    }

    @Override
    public Environment getEnvironment() {

        return appContext.getEnvironment();
    }

    @Override
    protected void afterInit() {
        RequestInfo requestInfo = getRequestInfo();
        MDC.put(TRACE_ID_NAME, requestInfo.getTraceId());
        MDC.put(TENANT_ID_NAME, requestInfo.getTenantId());
        MDC.put(PLATFORM_NAME, requestInfo.getPlatform());
        MDC.put(USER_ID_NAME,  requestInfo.getUserId() != null ? String.valueOf(requestInfo.getUserId()) : null);
        MDC.put(USER_TYPE_NAME,  requestInfo.getUserType() != null ? String.valueOf(requestInfo.getUserType()) : null);
        MDC.put("APP", getServiceInfo().getAppName());
        MDC.put("ENV", getServiceInfo().getEnvName());
        boolean rNotBlank = isNotBlank(getRequestInfo().getRealAddr());
        String rAddr = rNotBlank ? getRequestInfo().getRealAddr() : getRequestInfo().getRemoteAddr();
        String rPort = rNotBlank ? getRequestInfo().getRealPort() : getRequestInfo().getRemotePort();
        MDC.put("CLI", String.format("[%s%s]", rAddr, isNotBlank(rPort) ? COLON + rPort : EMPTY_STRING));
        String sAddr = getServiceInfo().getServerAddr();
        String sPort = getServiceInfo().getServerPort();
        MDC.put("SERV", String.format("[%s%s]", sAddr, isNotBlank(sPort) ? COLON + sPort : EMPTY_STRING));
    }

    @Override
    public ServiceInfo createServiceInfo() {
        ServiceInfo serviceInfo = super.createServiceInfo();
        serviceInfo.setServerPort(getEnvironment().getProperty("server.port"));
        serviceInfo.setAppName(getEnvironment().getProperty("spring.application.name"));
        serviceInfo.setEnvName(getEnvironment().getProperty("spring.profiles.active"));
        return serviceInfo;
    }

}
