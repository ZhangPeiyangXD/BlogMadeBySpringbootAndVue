package com.itzpy.blog.controller;

import com.itzpy.blog.service.SysUserService;
import com.itzpy.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private SysUserService sysUserService;

    /**
     * 获取当前登录用户信息
     * @param token 请求头中的Authorization的token字段
     * @return result 当前登录用户信息
     */
    // @RequestHeader("Authorization") 作用：获取请求头中的Authorization字段的值
    @GetMapping("/currentUser")
    public Result currentUser(@RequestHeader("Authorization") String token){
        log.info("正在登陆的用户头部信息：{}", token);

        return sysUserService.findUserByToken(token);
    }
}
