package com.itzpy.blog.service.impl;

import com.itzpy.blog.dao.mapper.SysUserMapper;
import com.itzpy.blog.dao.pojo.SysUser;
import com.itzpy.blog.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    SysUserMapper sysUserMapper;

    @Override
    public SysUser findUserById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);

        if (sysUser == null) {
            sysUser = new SysUser();
            sysUser.setNickname("站主");
        }

        return sysUser;
    }
}
