package com.itzpy.blog.service.impl;

import com.itzpy.blog.service.LogoutService;
import com.itzpy.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LogoutServiceImpl implements LogoutService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_" + token);

        return Result.success(null);
    }
}