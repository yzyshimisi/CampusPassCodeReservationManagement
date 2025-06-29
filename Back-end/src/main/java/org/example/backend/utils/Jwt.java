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

    public String generateJwtToken(int id, String loginName, int adminRole) {
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("loginName", loginName);
        claims.put("adminRole", adminRole);

        claims.put("iss", jwt_iss);

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

    public Claims validateJwt(String jwtToken) {

        if (jwtToken.split("\\.").length != 3) {    // 验证基本结构
            System.out.println("Invalid JWT format");
            return null;
        }

        Claims claims = getClaimsFromJwt(jwtToken);
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