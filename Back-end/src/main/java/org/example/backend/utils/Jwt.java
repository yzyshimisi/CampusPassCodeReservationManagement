package org.example.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Jwt {
    private final static SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

//    private final static String secret = "secretKey";
    private final static String secret = "CampusPassTokenKey_MustBeLongEnoughToBeSecure123";

    // 过期时间（单位秒）/ 2小时
    private final static Long access_token_expiration = 7200L;

    // jwt签发者
    private final static String jwt_iss = "spzhang";

    // jwt所有人
    private final static String subject = "zhangsp";

    public String generateJwtToken(String id, String username){

        // 头部 map / Jwt的头部承载，第一部分
        // 可不设置 默认格式是{"alg":"HS256"}
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        // 载荷 map / Jwt的载荷，第二部分
        Map<String,Object> claims = new HashMap<String,Object>();
        claims.put("id", id);   // 私有声明 / 自定义数据，根据业务需要添加
        claims.put("username", username);

        // 标准中注册的声明(建议但不强制使用)
        claims.put("iss", jwt_iss);

        Key secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        // 下面就是在为payload添加各种标准声明和私有声明了
        return Jwts.builder()
                .header().add(map).and()    // 头部信息
                .claims(claims)    // 载荷信息
                .id(UUID.randomUUID().toString())    // 设置jti(JWT ID)：是JWT的唯一标识，从而回避重放攻击。
                .issuedAt(new Date())    // 设置iat: jwt的签发时间
                .expiration(new Date(System.currentTimeMillis() + access_token_expiration * 1000))  // 设置exp：jwt过期时间
                .subject(subject)       // 设置sub：代表这个jwt所面向的用户，所有人
                .signWith(secretKey)    // 设置签名：通过签名算法和秘钥生成签名
                .compact();    // 开始压缩
    }

    private Claims getClaimsFromJwt(String jwt) {
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        try {
            Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwt).getPayload();
            return claims;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Claims validateJwt(String Cookie) {

        String jwtToken = null;

        if (Cookie == null || !Cookie.contains("jwtToken=")) {
            System.out.println("Invalid or missing JWT token in Cookie");
            return null;
        }

        jwtToken = Cookie.substring(Cookie.indexOf("jwtToken=") + "jwtToken=".length());

        if (jwtToken.isEmpty() || jwtToken.split("\\.").length != 3) {    // 验证基本结构
            System.out.println("Invalid JWT format");
            return null;
        }

        Jwt jwtDemo = new Jwt();

        Claims claims = jwtDemo.getClaimsFromJwt(jwtToken);
        if(claims == null){    // 验证签名
            System.out.println("Invalid JWT");
            return null;
        }

        Date expiration = claims.getExpiration();
        if(expiration.before(new Date())){    // 验证有效期
            System.out.println("JWT expired");
            return null;
        }

//        Date notBefore = claims.getNotBefore();
//        out.println(notBefore);
//        if (notBefore != null && notBefore.after(new Date())) {    // 验证生效时间
//            throw new SecurityException("JWT not yet valid");
//            return null;
//        }
        return claims;
    }
}
