package artoria.user;

import artoria.exception.VerifyUtils;
import artoria.servlet.RequestUtils;
import artoria.util.ThreadLocalUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static artoria.common.DefaultErrorCode.NO_LOGIN;
import static artoria.common.DefaultErrorCode.PARAMETER_IS_REQUIRED;
import static artoria.user.UserUtils.*;

public class UserInterceptor extends HandlerInterceptorAdapter {
    private static final String OPTIONS_METHOD = "OPTIONS";
    private TokenManager tokenManager;
    private UserManager userManager;
    private UserProperties userProperties;

    public UserInterceptor(TokenManager tokenManager, UserManager userManager, UserProperties userProperties) {
        this.tokenManager = tokenManager;
        this.userManager = userManager;
        String tokenName = userProperties.getTokenName();
        VerifyUtils.notBlank(tokenName, PARAMETER_IS_REQUIRED);
        this.userProperties = userProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        if (OPTIONS_METHOD.equals(method)) { return true; }
        String tokenName = userProperties.getTokenName();
        String tokenId = request.getHeader(tokenName);
        VerifyUtils.notBlank(tokenId, NO_LOGIN);
        UserUtils.setTokenId(tokenId);
        Token token = UserUtils.getToken();
        token.setLastAccessedTime(System.currentTimeMillis());
        token.setLastAccessedAddress(RequestUtils.getRemoteAddr(request));
        tokenManager.save(token);
        userManager.refresh(token.getUserId());
        tokenManager.refresh(tokenId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtils.remove(TOKEN_ID_THREAD_LOCAL_KEY);
        ThreadLocalUtils.remove(TOKEN_THREAD_LOCAL_KEY);
        ThreadLocalUtils.remove(USER_INFO_THREAD_LOCAL_KEY);
    }

}
