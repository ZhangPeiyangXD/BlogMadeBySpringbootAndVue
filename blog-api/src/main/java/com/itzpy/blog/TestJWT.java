package com.itzpy.blog;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestJWT {
    public static void main(String[] args) {
        // 测试提供的token
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NjQxMzUzOTQsInVzZXJJZCI6MiwiaWF0IjoxNzY0MDQ4OTk0fQ.yaG4ZgrScShjlQ5eNKoEV-uRkU-ufo2uD4trFCYAvZg";
        String secret = "zpy_blog";
        
        System.out.println("当前时间戳: " + System.currentTimeMillis());
        System.out.println("Token过期时间戳: " + 1764135394);
        System.out.println("Token签发时间戳: " + 1764048994);
        System.out.println("Token是否过期: " + (System.currentTimeMillis()/1000 > 1764135394));
        
        try {
            // 尝试解析token
            Object parsed = Jwts.parser().setSigningKey(secret).parse(token);
            System.out.println("解析结果: " + parsed);
        } catch (Exception e) {
            System.out.println("解析失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}