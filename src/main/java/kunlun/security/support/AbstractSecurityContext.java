package kunlun.security.support;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import kunlun.context.support.AbstractServletContext;
import kunlun.core.AccessController;
import kunlun.generator.id.IdUtils;
import kunlun.security.SecurityContext;
import kunlun.security.TokenManager;
import kunlun.security.UserManager;
import kunlun.util.Assert;
import kunlun.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import static kunlun.common.constant.Env.COMPUTER_NAME;
import static kunlun.common.constant.Env.HOST_NAME;
import static kunlun.security.TokenManager.Token;
import static kunlun.security.UserManager.UserDetail;

public abstract class AbstractSecurityContext extends AbstractServletContext implements SecurityContext {
    private static final Logger log = LoggerFactory.getLogger(AbstractSecurityContext.class);
    private static final String REQUEST_INFO_NAME = "requestInfo";
    protected static final String USER_DETAIL_NAME = "userDetail";
    public static final String TRACE_ID_NAME  = "tid";
    public static final String PLATFORM_NAME  = "p";
    public static final String TENANT_ID_NAME = "t";
    public static final String USER_ID_NAME   = "uid";
    public static final String USER_TYPE_NAME = "utp";
    private final ThreadLocal<Map<String, Object>> threadLocal;
    private final AccessController accessController;
    private final TokenManager tokenManager;
    private final UserManager userManager;
    private ServiceInfo serviceInfo;
    private boolean accessLog = true;

    public AbstractSecurityContext(TokenManager tokenManager,
                                   UserManager userManager,
                                   AccessController accessController,
                                   ThreadLocal<Map<String, Object>> threadLocal) {
        Assert.notNull(tokenManager, "Parameter \"tokenManager\" must not null. ");
        Assert.notNull(userManager, "Parameter \"userManager\" must not null. ");
        Assert.notNull(accessController, "Parameter \"accessController\" must not null. ");
        Assert.notNull(threadLocal, "Parameter \"threadLocal\" must not null. ");
        this.tokenManager = tokenManager;
        this.userManager = userManager;
        this.accessController = accessController;
        this.threadLocal = threadLocal;
    }

    @Override
    protected Map<String, Object> getBucket() {
        Map<String, Object> bucket = threadLocal.get();
        if (bucket == null) {
            threadLocal.set(bucket = new LinkedHashMap<String, Object>());
        }
        return bucket;
    }

    @Override
    public void init(HttpServletRequest request, HttpServletResponse response) {
        setRequest(request); setResponse(response);
        setRequestInfo(createRequestInfo(request));
        response.addHeader("rid", getRequestInfo().getTraceId());
        doSubmit();
        if (accessLog) {
            log.info("#Trace Start request: {} ,remoteAddr: {}"
                    , getRequestInfo().getServletPath(), getRequestInfo().getRemoteAddr());
        }
    }

    @Override
    public void destroy(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        if (accessLog) {
            Long accessTime = getRequestInfo().getAccessTime();
            Long responseTime = null;
            if (accessTime != null) {
                responseTime = System.currentTimeMillis() - accessTime;
            }
            log.info("#Trace End request: {}, remoteAddr: {}, response time: {}"
                    , getRequestInfo().getServletPath(), getRequestInfo().getRemoteAddr(), responseTime);
        }
        super.destroy(request, response, ex);
    }

    @Override
    public void resetThreadLocals() {

        threadLocal.remove();
    }

    @Override
    public String getTraceId() {

        return getRequestInfo() != null ? getRequestInfo().getTraceId() : null;
    }

    @Override
    public String getToken() {

        return getRequestInfo() != null ? getRequestInfo().getToken() : null;
    }

    @Override
    public Object getUserId() {

        return getRequestInfo() != null ? getRequestInfo().getUserId() : null;
    }

    @Override
    public Object getUserType() {

        return getRequestInfo() != null ? getRequestInfo().getUserType() : null;
    }

    @Override
    public String getPlatform() {

        return getRequestInfo() != null ? getRequestInfo().getPlatform() : null;
    }

    @Override
    public String getTenantId() {

        return getRequestInfo() != null ? getRequestInfo().getTenantId() : null;
    }

    @Override
    public void putBaseData(Object userId, Object userType, String platform, String tenantId) {
        RequestInfo requestInfo = getRequestInfo();
        if (requestInfo == null) {
            setRequestInfo(requestInfo = new RequestInfo());
        }
        if (ObjUtil.isNotEmpty(userId)) {
            requestInfo.setUserId(userId);
        }
        if (ObjUtil.isNotEmpty(userType)) {
            requestInfo.setUserType(userType);
        }
        if (StrUtil.isNotBlank(platform)) {
            requestInfo.setPlatform(platform);
        }
        if (StrUtil.isNotBlank(tenantId)) {
            requestInfo.setTenantId(tenantId);
        }
        doSubmit();
    }

    @Override
    public UserDetail getUserDetail() {
        UserDetail userDetail = getProperty(USER_DETAIL_NAME, UserDetail.class);
        if (userDetail != null) { return userDetail; }
        if (getUserId() == null) { return null; }
        userDetail = getUserManager().getUserDetail(getUserId(), getUserType());
        if (userDetail == null) { return null; }
        setProperty(USER_DETAIL_NAME, userDetail);
        return userDetail;
    }

    @Override
    public AccessController getAccessController() {

        return accessController;
    }

    @Override
    public TokenManager getTokenManager() {

        return tokenManager;
    }

    @Override
    public UserManager getUserManager() {

        return userManager;
    }

    public boolean getAccessLog() {

        return accessLog;
    }

    public void setAccessLog(boolean accessLog) {

        this.accessLog = accessLog;
    }

    protected abstract void doSubmit();

    public ServiceInfo getServiceInfo() {

        return serviceInfo;
    }

    public void setServiceInfo(ServiceInfo serviceInfo) {
        Assert.notNull(serviceInfo, "Parameter \"serviceInfo\" must not null. ");
        this.serviceInfo = serviceInfo;
    }

    public ServiceInfo createServiceInfo() {
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setServerName(StringUtils.isNotBlank(HOST_NAME) ? HOST_NAME : COMPUTER_NAME);
        serviceInfo.setServerAddr(getIpAddress());
        //serviceInfo.setServerPort()
        //serviceInfo.setAppName()
        //serviceInfo.setEnvName()
        return serviceInfo;
    }

    @Deprecated
    protected String getIpAddress() {
        // todo
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public RequestInfo getRequestInfo() {

        return getProperty(REQUEST_INFO_NAME, RequestInfo.class);
    }

    public void setRequestInfo(RequestInfo requestInfo) {

        setProperty(REQUEST_INFO_NAME, requestInfo);
    }

    public RequestInfo createRequestInfo(HttpServletRequest request) {
        RequestInfo requestInfo = new RequestInfo();

        requestInfo.setRemoteAddr(request.getRemoteAddr());
        requestInfo.setRemotePort(String.valueOf(request.getRemotePort()));
        requestInfo.setLocalAddr(request.getLocalAddr());
        requestInfo.setLocalPort(String.valueOf(request.getLocalPort()));
        requestInfo.setRealAddr(request.getHeader("X-Real-IP"));
        requestInfo.setRealPort(request.getHeader("X-Real-PORT"));

        requestInfo.setDeviceName(request.getHeader("deviceName"));
        requestInfo.setDeviceType(request.getHeader("deviceType"));
        requestInfo.setDeviceId(request.getHeader("deviceId"));

        requestInfo.setServletPath(request.getServletPath());
        requestInfo.setTraceId(request.getHeader(TRACE_ID_NAME));
        requestInfo.setPlatform(request.getHeader(PLATFORM_NAME));
        requestInfo.setTenantId(request.getHeader(TENANT_ID_NAME));
        requestInfo.setAccessTime(System.currentTimeMillis());
        if (StrUtil.isBlank(requestInfo.getTraceId())) {
            requestInfo.setTraceId(IdUtils.nextString("uuid"));
        }

        requestInfo.setToken(request.getHeader("authorization"));
        requestInfo.setUserId(request.getHeader(USER_ID_NAME));
        requestInfo.setUserType(request.getHeader(USER_TYPE_NAME));
        if (StrUtil.isBlank((String) requestInfo.getUserId()) &&
                StrUtil.isNotBlank(requestInfo.getToken())) {
            Token token = getTokenManager().parseToken(requestInfo.getToken());
            if (token != null && token.getUserId() != null) {
                requestInfo.setUserId(token.getUserId());
                requestInfo.setUserType(token.getUserType());
            }
        }

        return requestInfo;
    }

    public static class ServiceInfo implements Serializable {
        private String serverName;
        private String serverAddr;
        private String serverPort;
        private String appName;
        private String envName;

        public String getServerName() {

            return serverName;
        }

        public void setServerName(String serverName) {

            this.serverName = serverName;
        }

        public String getServerAddr() {

            return serverAddr;
        }

        public void setServerAddr(String serverAddr) {

            this.serverAddr = serverAddr;
        }

        public String getServerPort() {

            return serverPort;
        }

        public void setServerPort(String serverPort) {

            this.serverPort = serverPort;
        }

        public String getAppName() {

            return appName;
        }

        public void setAppName(String appName) {

            this.appName = appName;
        }

        public String getEnvName() {

            return envName;
        }

        public void setEnvName(String envName) {

            this.envName = envName;
        }
    }

    public static class RequestInfo implements Serializable {
        /* -------- 地址和端口相关 -------- */
        private String remoteAddr;
        private String remotePort;
        private String localAddr;
        private String localPort;
        private String realAddr;
        private String realPort;
        /* -------- 地址和端口相关 -------- */

        /* -------- 设备信息相关 -------- */
        private String deviceName;
        private String deviceType;
        private String deviceId;
        /* -------- 设备信息相关 -------- */

        /* -------- 访问信息相关 -------- */
        private String servletPath;
        private String traceId;
        private String platform;
        private String tenantId;
        private Long   accessTime;
        /* -------- 访问信息相关 -------- */

        /* -------- 用户信息相关 -------- */
        private String token;
        private Object userId;
        private Object userType;
        /* -------- 用户信息相关 -------- */

        public String getRemoteAddr() {
            return remoteAddr;
        }

        public void setRemoteAddr(String remoteAddr) {
            this.remoteAddr = remoteAddr;
        }

        public String getRemotePort() {
            return remotePort;
        }

        public void setRemotePort(String remotePort) {
            this.remotePort = remotePort;
        }

        public String getLocalAddr() {
            return localAddr;
        }

        public void setLocalAddr(String localAddr) {
            this.localAddr = localAddr;
        }

        public String getLocalPort() {
            return localPort;
        }

        public void setLocalPort(String localPort) {
            this.localPort = localPort;
        }

        public String getRealAddr() {
            return realAddr;
        }

        public void setRealAddr(String realAddr) {
            this.realAddr = realAddr;
        }

        public String getRealPort() {
            return realPort;
        }

        public void setRealPort(String realPort) {
            this.realPort = realPort;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getServletPath() {
            return servletPath;
        }

        public void setServletPath(String servletPath) {
            this.servletPath = servletPath;
        }

        public String getTraceId() {
            return traceId;
        }

        public void setTraceId(String traceId) {
            this.traceId = traceId;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public Long getAccessTime() {
            return accessTime;
        }

        public void setAccessTime(Long accessTime) {
            this.accessTime = accessTime;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Object getUserId() {
            return userId;
        }

        public void setUserId(Object userId) {
            this.userId = userId;
        }

        public Object getUserType() {
            return userType;
        }

        public void setUserType(Object userType) {
            this.userType = userType;
        }
    }

}
