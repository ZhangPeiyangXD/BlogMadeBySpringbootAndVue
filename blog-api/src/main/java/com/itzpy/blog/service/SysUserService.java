package com.itzpy.blog.service;

import com.itzpy.blog.dao.pojo.SysUser;
import org.springframework.stereotype.Service;

@Service
public interface SysUserService {
    SysUser findUserById(Long id);
}
