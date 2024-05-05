package kunlun.security.support;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;

import static kunlun.common.constant.Numbers.*;

public class JwtTokenManager extends AbstractTokenManager {

    public static final String UT = "ut";
    public static final String UID = "u";
    public static final String EXP = "e";

    public static final String HS256STR = "eyJhbGciOiJIUzI1NiJ9.";

    private final String signingKey;
    // second  30*24*60*60
    private final Long timeToLive;

    public JwtTokenManager(String signingKey, Long timeToLive) {
        if (StrUtil.isBlank(signingKey)) {
            signingKey = "kunlun-jwt-token";
        }
        this.signingKey = signingKey;
        this.timeToLive = timeToLive;
    }

    public JwtTokenManager() {

        this(null, null);
    }

    protected String cutOff(String longToken) {
        if (longToken.startsWith(HS256STR)) {
            return longToken.replaceFirst(HS256STR, "");
        }
        return longToken;
    }

    protected String recover(String shortToken) {
        if (!shortToken.startsWith(HS256STR)) {
            return HS256STR + shortToken;
        }
        return shortToken;
    }

    @Override
    public String buildToken(Token token) {
        Claims claims = new DefaultClaims();
        claims.put(UID, token.getUserId());
        if (timeToLive != null) {
            claims.put(EXP, System.currentTimeMillis() + timeToLive * 1000);
        }
        if (ObjUtil.isNotEmpty(token.getUserType())) {
            claims.put(UT, token.getUserType());
        }
        String compact = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signingKey)
                .compact();

        compact = cutOff(compact);

        if (token instanceof TokenImpl) {
            ((TokenImpl) token).setValue(compact);
        }
        return compact;
    }

    @Override
    public Token parseToken(String token) {
        token = recover(token);
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token)
                .getBody();
        if (claims == null) { return null; }
        // if uid is number,it is must long
        Object uid = claims.get(UID);
        if (uid instanceof Number) {
            uid = ((Number) uid).longValue();
        }
        return new TokenImpl(token, uid, claims.get(UT));
    }

    @Override
    public int verifyToken(String token) {
        if (StrUtil.isBlank(token)) {
            return MINUS_ONE;
        }
        token = recover(token);
        Jws<Claims> jws;
        try {
            jws = Jwts.parser()
                    .setSigningKey(signingKey)
                    .parseClaimsJws(token);
        } catch (Exception e) {
//            log.error
            return MINUS_TWO;
        }

        if (jws == null || MapUtil.isEmpty(jws.getBody())) {
            return MINUS_TWO;
        }

        Claims claims = jws.getBody();
        Long expiration = claims.get(EXP, Long.class);
        if (expiration != null && expiration < System.currentTimeMillis()) {
            return MINUS_THREE;
        }
        return ONE;
    }

    @Override
    public String refreshToken(String token) {
        Token tokenObj = parseToken(token);
        return buildToken(tokenObj);
    }

}
