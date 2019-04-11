package artoria.user;

import artoria.exception.VerifyUtils;
import artoria.util.ThreadLocalUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static artoria.exception.InternalErrorCode.NO_LOGIN;
import static artoria.exception.InternalErrorCode.PARAMETER_REQUIRED;

public class UserInterceptor extends HandlerInterceptorAdapter {
    private static final String OPTIONS_METHOD = "OPTIONS";
    private TokenManager tokenManager;
    private UserManager userManager;
    private UserProperties userProperties;

    public UserInterceptor(TokenManager tokenManager, UserManager userManager, UserProperties userProperties) {
        this.tokenManager = tokenManager;
        this.userManager = userManager;
        String tokenName = userProperties.getTokenName();
        VerifyUtils.notBlank(tokenName, PARAMETER_REQUIRED);
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
        userManager.refresh(token.getUserId());
        tokenManager.refresh(tokenId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        ThreadLocalUtils.clear();
    }

}
