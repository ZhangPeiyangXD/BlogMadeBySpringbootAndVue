package com.itzpy.blog.service;

import com.itzpy.blog.dao.pojo.SysUser;
import com.itzpy.blog.vo.Result;
import com.itzpy.blog.vo.params.LoginParam;
import org.springframework.stereotype.Service;

@Service
public interface LoginService {

    /**
     * 登录功能
     * @param loginParam
     * @return token, 用户信息
     */
    Result login(LoginParam loginParam);


    /**
     * 验证token
     * @param token 用户的token
     * @return result(用户信息)
     */
    SysUser checkToken(String token);
}
