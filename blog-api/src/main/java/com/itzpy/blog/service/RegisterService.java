package com.itzpy.blog.service;

import com.itzpy.blog.dao.pojo.Result;
import com.itzpy.blog.vo.params.LoginParam;
import org.springframework.stereotype.Service;

@Service
public interface RegisterService {
    /**
     * 注册
     * @param loginParam 用户信息 nickname account password
     * @return null 插入成功
     */
    Result register(LoginParam loginParam);
}
