package com.itzpy.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itzpy.blog.dao.pojo.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据账号和密码查询用户
     * @param account 账号
     * @param password 密码
     * @return 用户信息
     */
    @Select("select * from sys_user where account = #{account} and password = #{password} limit 1")
    SysUser selectByAccountAndPassword(String account, String password);
}
