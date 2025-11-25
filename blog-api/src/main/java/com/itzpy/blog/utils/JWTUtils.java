package com.itzpy.blog.utils;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtils {
    // 从配置文件注入 JWT 密钥
    @Value("${jwt.secret}")
    private String jwtToken;

    // 从配置文件注入过期时间
    @Value("${jwt.token-expiration}")
    private Long tokenExpiration;

    private String staticJwtToken;
    private Long staticTokenExpiration;

    @PostConstruct
    public void init() {
        staticJwtToken = jwtToken;
        staticTokenExpiration = tokenExpiration;
        System.out.println("JWT Secret initialized: " + staticJwtToken);
    }


    public String createToken(Long userId){
        if (staticJwtToken == null || staticJwtToken.isEmpty()) {
            throw new IllegalStateException("JWT secret key is not initialized properly");
        }
        
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId",userId);
        JwtBuilder jwtBuilder = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, staticJwtToken) // 签发算法，秘钥为jwtToken
                .setClaims(claims) // body数据，要唯一，自行设置
                .setIssuedAt(new Date()) // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + staticTokenExpiration));// expireTime的有效时间
        String token = jwtBuilder.compact();
        return token;
    }

    public Map<String, Object> checkToken(String token){
        try {
            if (staticJwtToken == null || staticJwtToken.isEmpty()) {
                throw new IllegalStateException("JWT secret key is not initialized properly");
            }
            
            Jwt parse = Jwts.parser().setSigningKey(staticJwtToken).parse(token);
            return (Map<String, Object>) parse.getBody();
        }catch (Exception e){
            System.err.println("JWT解析失败，密钥：" + staticJwtToken);
            e.printStackTrace();
        }
        return null;
    }
}