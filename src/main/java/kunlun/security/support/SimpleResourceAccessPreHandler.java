package kunlun.security.support;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import kunlun.common.Result;
import kunlun.core.handler.ResourceAccessPreHandler;
import kunlun.data.CodeDefinition;
import kunlun.data.json.JsonUtils;
import kunlun.exception.ExceptionUtils;
import kunlun.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static kunlun.common.constant.Numbers.*;

public class SimpleResourceAccessPreHandler implements ResourceAccessPreHandler {
    private static final Logger log = LoggerFactory.getLogger(SimpleResourceAccessPreHandler.class);

    private final List<String> ignoredUrls;
    private final boolean showLog;

    public SimpleResourceAccessPreHandler(boolean showLog, List<String> ignoredUrls) {
        this.ignoredUrls = ignoredUrls;
        this.showLog = showLog;
    }

    protected boolean isIgnoredUrl(String requestUrl) {
        // 过滤掉资源相关请求
        if (requestUrl.endsWith(".js")
                || requestUrl.endsWith(".css")
                || requestUrl.endsWith(".html")
                || requestUrl.endsWith(".ico")
                || requestUrl.contains("swagger-")
                || requestUrl.endsWith("api-docs")
                || requestUrl.contains("error")) {
            return true;
        }
        // 白名单过滤
        if (CollUtil.isEmpty(ignoredUrls)) { return false; }
        for (String url : ignoredUrls) {
            if (requestUrl.equalsIgnoreCase(url.trim())) {
                return true;
            }
        }
        return false;
    }


    public enum Errors implements CodeDefinition {
        /**
         *
         */
        /**
         * 操作失败.
         */
        FAILED(500, "操作失败"),
        UNAUTHORIZED_ERROR(401, "Unauthorized"),
        FORBIDDEN(403, "没有相关权限"),
        NOT_LOGIN_ERROR(403, "未登录或登录已过期，请重新登录！"),
        NOT_FOUND_ERROR(404,"request url not found"),
        ;

        private String message;
        private long code;

        Errors(long code, String message) {
            this.message = message;
            this.code = code;
        }

        @Override
        public Long getCode() {

            return code;
        }

        @Override
        public String getDescription() {

            return message;
        }
    }

    protected void write(HttpServletResponse response, Result<Object> result) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(JsonUtils.toJsonString("jackson", result));
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    protected boolean doHandle(HttpServletRequest request,
                               HttpServletResponse response,
                               String requestUrl,
                               String token) {
        if (StrUtil.isBlank(requestUrl)) {
            write(response, Result.failure(Errors.NOT_FOUND_ERROR));
            return false;
        }
        requestUrl = requestUrl.trim();

        if (showLog) {
            log.info(">>>> resource access -> request url: {}, ignored urls: {}"
                    , requestUrl, JSON.toJSONString(ignoredUrls));
        }

        // 判断是否是可以忽略的 URL
        if (isIgnoredUrl(requestUrl)) { return true; }

        int verifyToken = SecurityUtils.getTokenManager().verifyToken(token);
        if (verifyToken != ONE) {
            // not token
            if (verifyToken == MINUS_ONE) {
                write(response, Result.failure(Errors.NOT_LOGIN_ERROR));
            } else if (verifyToken == MINUS_TWO || verifyToken == MINUS_THREE) {
                write(response, Result.failure(Errors.NOT_LOGIN_ERROR));
            } else {
                write(response, Result.failure(Errors.UNAUTHORIZED_ERROR));
            }
            return false;
        }

        //
        if (SecurityUtils.hasPermissionAnd(SecurityUtils.getUserId(), SecurityUtils.getUserType(), "api", requestUrl)) {
            return true;
        }
        else {
            write(response, Result.failure(Errors.FORBIDDEN));
            return false;
        }
    }

    @Override
    public Object handle(Object type, Object resource, String token, Object... arguments) {
        log.debug("Resource access pre handler: type={}, token={}, url={}", type, token, resource);
        String requestUrl = resource != null ? String.valueOf(resource) : null;
        HttpServletResponse response = (HttpServletResponse) arguments[1];
        HttpServletRequest request = (HttpServletRequest) arguments[0];
        try {
            return doHandle(request, response, requestUrl, token);
        }
        catch (Exception e) {
            log.error("Resource access pre handler error", e);
            write(response, Result.failure(Errors.FAILED));
            return false;
        }
    }

}
