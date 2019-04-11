package artoria.user;

import artoria.exception.VerifyUtils;
import artoria.spring.ApplicationContextUtils;
import artoria.util.ThreadLocalUtils;

import static artoria.exception.InternalErrorCode.*;

public class UserUtils {
    /**
     * ThreadLocal token key
     */
    private static final String TOKEN_THREAD_LOCAL_KEY = "TOKEN";
    /**
     * ThreadLocal tokenId key
     */
    private static final String TOKEN_ID_THREAD_LOCAL_KEY = "TOKEN_ID";
    /**
     * ThreadLocal userInfo key
     */
    private static final String USER_INFO_THREAD_LOCAL_KEY = "USER_INFO";

    private static class Holder {
        static TokenManager tokenManager = ApplicationContextUtils.getBean(TokenManager.class);
        static UserManager userManager = ApplicationContextUtils.getBean(UserManager.class);
    }

    public static String getTokenId() {
        String tokenId = (String) ThreadLocalUtils.getValue(TOKEN_ID_THREAD_LOCAL_KEY);
        VerifyUtils.notBlank(tokenId, NO_LOGIN);
        return tokenId;
    }

    public static void setTokenId(String tokenId) {
        VerifyUtils.notBlank(tokenId, PARAMETER_REQUIRED);
        ThreadLocalUtils.setValue(TOKEN_ID_THREAD_LOCAL_KEY, tokenId);
    }

    public static Token getToken() {
        Token token = (Token) ThreadLocalUtils.getValue(TOKEN_THREAD_LOCAL_KEY);
        if (token != null) { return token; }
        String tokenId = UserUtils.getTokenId();
        token = Holder.tokenManager.find(tokenId);
        VerifyUtils.notNull(token, INVALID_TOKEN);
        ThreadLocalUtils.setValue(TOKEN_THREAD_LOCAL_KEY, token);
        return token;
    }

    public static UserInfo getUserInfo() {
        UserInfo userInfo = (UserInfo) ThreadLocalUtils.getValue(USER_INFO_THREAD_LOCAL_KEY);
        if (userInfo != null) { return userInfo; }
        Token token = UserUtils.getToken();
        String userId = token.getUserId();
        userInfo = Holder.userManager.find(userId);
        VerifyUtils.notNull(userInfo, INVALID_USER);
        ThreadLocalUtils.setValue(USER_INFO_THREAD_LOCAL_KEY, userInfo);
        return userInfo;
    }

}
