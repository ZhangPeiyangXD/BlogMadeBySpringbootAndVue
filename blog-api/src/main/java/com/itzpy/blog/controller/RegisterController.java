package com.itzpy.blog.controller;


import com.itzpy.blog.service.LoginService;
import com.itzpy.blog.service.RegisterService;
import com.itzpy.blog.dao.pojo.Result;
import com.itzpy.blog.vo.params.LoginParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(("/register"))
public class RegisterController {
    @Autowired
    private RegisterService registerService;
    @Autowired
    private LoginService loginService;


    /**
     * 用户注册
     * @param loginParam 用户信息: 账号 密码 昵称
     * @return result(token)
     */
    @PostMapping
    public Result register(@RequestBody LoginParam  loginParam) {
      log.info("用户注册:{}",  loginParam);

      if(registerService.register(loginParam)!= null) {
          return registerService.register(loginParam);
      }

      return loginService.login(loginParam);
    }
}
