package com.itzpy.blog.controller;

import com.itzpy.blog.aop.LogAnnotation;
import com.itzpy.blog.service.LogoutService;
import com.itzpy.blog.dao.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logout")
@Slf4j
public class LogoutController {
    @Autowired
    LogoutService logoutService;


    /**
     * 登出
     * @param token 登录的token
     * @return result(登出成功)
     */
    @GetMapping
    @LogAnnotation(module = "登出", operator = "登出")
    public Result logout(@RequestHeader("Authorization") String token){
        // 检查token是否以"Bearer "开头，如果是则提取真正的token
        if (StringUtils.isNotBlank(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return logoutService.logout(token);
    }
}