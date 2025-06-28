package org.example.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Jwt {
    private final static SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    private final static String secret = "CampusPassTokenKey_MustBeLongEnoughToBeSecure123";
    private final static Long access_token_expiration = 7200L;
    private final static String jwt_iss = "spzhang";
    private final static String subject = "zhangsp";

    public String generateJwtToken(String id, String username, int role) {
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("username", username);
        claims.put("iss", jwt_iss);
        claims.put("admin_role", role); // 添加 admin_role 到 claims

        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .header().add(map).and()
                .claims(claims)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + access_token_expiration * 1000))
                .subject(subject)
                .signWith(secretKey)
                .compact();
    }

    private Claims getClaimsFromJwt(String jwt) {
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwt).getPayload();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Claims validateJwt(String Cookie) {
        String jwtToken = null;

        if (Cookie == null || !Cookie.contains("jwtToken=")) {
            System.out.println("Invalid or missing JWT token in Cookie");
            return null;
        }


        int start = Cookie.indexOf("jwtToken=") + "jwtToken=".length();
        int end = Cookie.indexOf(";", start);
        if (end == -1) {
            jwtToken = Cookie.substring(start); // 无分隔符，取到末尾
        } else {
            jwtToken = Cookie.substring(start, end); // 取到下一个分隔符
        }

        if (jwtToken.isEmpty() || jwtToken.split("\\.").length != 3) {    // 验证基本结构
            System.out.println("Invalid JWT format");
            return null;
        }

        Jwt jwtDemo = new Jwt();
        Claims claims = jwtDemo.getClaimsFromJwt(jwtToken);
        if (claims == null){    // 验证签名
            System.out.println("Invalid JWT");
            return null;
        }

        Date expiration = claims.getExpiration();
        if (expiration.before(new Date())) {
            System.out.println("JWT expired");
            return null;
        }

        return claims;
    }
}