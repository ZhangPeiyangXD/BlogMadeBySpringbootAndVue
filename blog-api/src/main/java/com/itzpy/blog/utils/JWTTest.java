package com.itzpy.blog.utils;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;

import java.util.Map;

public class JWTTest {
    public static void main(String[] args) {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NjQxMzUzOTQsInVzZXJJZCI6MiwiaWF0IjoxNzY0MDQ4OTk0fQ.yaG4ZgrScShjlQ5eNKoEV-uRkU-ufo2uD4trFCYAvZg";
        String secret = "zpy_blog";
        
        try {
            Jwt parse = Jwts.parser().setSigningKey(secret).parse(token);
            Map<String, Object> body = (Map<String, Object>) parse.getBody();
            System.out.println("Token body: " + body);
        } catch (Exception e) {
            System.out.println("Failed to parse token:");
            e.printStackTrace();
        }
    }
}