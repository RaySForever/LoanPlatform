package xyz.raysmen.lp.base.util;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;
import xyz.raysmen.lp.common.exception.BusinessException;
import xyz.raysmen.lp.common.result.ResponseEnum;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * JwtUtils
 * JWT工具类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.base.util
 * @date 2022/06/08 22:02
 */
public class JwtUtils {
    private final static long TOKEN_EXPIRATION = 24*60*60*1000;
    private final static String TOKEN_SIGN_KEY = "R1R2B3loan1p0l1a2t3f4o5r6m7";

    private static Key getKeyInstance() {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] bytes = DatatypeConverter.parseBase64Binary(TOKEN_SIGN_KEY);
        return new SecretKeySpec(bytes, signatureAlgorithm.getJcaName());
    }

    public static String createToken(Long userId, String userName) {
        return Jwts.builder()
                .setSubject("LOANPLATFORM-USER")
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(getKeyInstance(), SignatureAlgorithm.ES512)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
    }

    /**
     * 判断token是否有效
     * @param token token
     * @return      有效与否
     */
    public static boolean checkToken(String token) {
        if(StringUtils.hasText(token)) {
            return false;
        }
        try {
            Jwts.parserBuilder().setSigningKey(getKeyInstance())
                    .build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static Long getUserId(String token) {
        Claims claims = getClaims(token);
        Integer userId = (Integer) claims.get("userId");
        return userId.longValue();
    }

    public static String getUserName(String token) {
        Claims claims = getClaims(token);
        return (String) claims.get("userName");
    }

    /**
     * 校验token并返回Claims
     * @param token token
     * @return      Claims，自定义键值对
     */
    private static Claims getClaims(String token) {
        if(StringUtils.hasText(token)) {
            // LOGIN_AUTH_ERROR(-213, "未登录"),
            throw new BusinessException(ResponseEnum.LOGIN_AUTH_ERROR);
        }
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(getKeyInstance()).build().parseClaimsJws(token);
            return claimsJws.getBody();
        } catch (Exception e) {
            // LOGIN_AUTH_ERROR(-213, "未登录"),
            throw new BusinessException(ResponseEnum.LOGIN_AUTH_ERROR);
        }
    }
}
