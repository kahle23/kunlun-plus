package kunlun.security.support;

import cn.hutool.json.JSONUtil;
import kunlun.logging.Logger;
import kunlun.logging.LoggerFactory;
import kunlun.security.TokenManager;
import org.junit.Test;

import static kunlun.security.support.AbstractTokenManager.TokenImpl;

public class JwtTokenManagerTest {
    private static final JwtTokenManager tokenManager = new JwtTokenManager(null, 30*24*60*60L);
    private static final Logger log = LoggerFactory.getLogger(JwtTokenManagerTest.class);

    @Test
    public void testBuildToken() {
        // eyJ1IjoxMjY0NjIyMDM5NjQyMjU2MjExLCJlIjoxNzM2NDk4OTQ4OTA2LCJ1dCI6NX0.mrgqSlaGIzO_gLkByV9E2YO4EXlH34KoMgCiTXaaLso
        TokenImpl token = new TokenImpl(null, 1264622039642256211L, 5);
        String buildToken = tokenManager.buildToken(token);
        log.info("Build token: {}", buildToken);
    }

    @Test
    public void testObtainToken() {
        String tokenStr = "eyJ1IjoxMjY0NjIyMDM5NjQyMjU2MjExLCJlIjoxNzM2NDk4OTQ4OTA2LCJ1dCI6NX0.mrgqSlaGIzO_gLkByV9E2YO4EXlH34KoMgCiTXaaLso";
        TokenManager.Token token = tokenManager.parseToken(tokenStr);
        log.info("Obtain token: {}", JSONUtil.toJsonPrettyStr(token));
    }

    @Test
    public void testVerifyToken() {
        String tokenStr = "eyJ1IjoxMjY0NjIyMDM5NjQyMjU2MjExLCJlIjoxNzM2NDk4OTQ4OTA2LCJ1dCI6NX0.mrgqSlaGIzO_gLkByV9E2YO4EXlH34KoMgCiTXaaLso";
        int verifyToken = tokenManager.verifyToken(tokenStr);
        log.info("Verify token: {}", verifyToken);
    }

}
