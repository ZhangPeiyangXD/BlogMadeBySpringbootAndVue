package com.itzpy.blog.controller;

import com.itzpy.blog.service.LogoutService;
import com.itzpy.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
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
    public Result logout(@RequestHeader("Authorization") String token){
        log.info("正在登出...");

        return logoutService.logout(token);
    }
}
