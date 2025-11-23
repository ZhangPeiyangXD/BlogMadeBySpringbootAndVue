package com.itzpy.blog.service;

import com.itzpy.blog.dao.pojo.Result;
import org.springframework.stereotype.Service;

@Service
public interface LogoutService {
    /**
     * 登出
     * @param token 登录的token
     * @return result(登出成功)
     */
    Result logout(String token);
}
