package com.itzpy.blog.service.impl;

import com.itzpy.blog.service.LogoutService;
import com.itzpy.blog.dao.pojo.Result;
import com.itzpy.blog.dao.pojo.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LogoutServiceImpl implements LogoutService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Override
    public Result logout(String token) {
        // 检查token是否为空
        if (StringUtils.isBlank(token)) {
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        
        try {
            // 从Redis中删除token
            redisTemplate.delete("TOKEN_" + token);
            
            return Result.success(null);
        } catch (Exception e) {
            return Result.fail(ErrorCode.SYSTEM_ERROR.getCode(), "登出失败: " + e.getMessage());
        }
    }
}