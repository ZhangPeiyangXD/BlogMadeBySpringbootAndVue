package com.itzpy.blog.controller;

import com.itzpy.blog.service.LoginService;
import com.itzpy.blog.vo.Result;
import com.itzpy.blog.vo.params.LoginParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private LoginService loginService;


    /**
     * 登录
     * @return result(登录成功)
     */
    @PostMapping
    public Result login(@RequestBody LoginParam loginParam){
        log.info("用户登录");

        return loginService.login(loginParam);
    }

}
