package com.itzpy.blog.service;

import com.itzpy.blog.dao.pojo.SysUser;
import com.itzpy.blog.vo.Result;
import org.springframework.stereotype.Service;

@Service
public interface SysUserService {
    /**
     * 根据id查询用户
     * @param id 用户id
     * @return 用户信息
     */
    SysUser findUserById(Long id);

    /**
     * 根据账号和密码查询用户
     * @param account 账号
     * @param password 密码
     * @return 用户信息
     */
    SysUser findUserByCountAndPassword(String account, String password);

    /**
     * 根据token查询用户
     * @param token 用户的token
     * @return result(用户信息)
     */
    Result findUserByToken(String token);
}
