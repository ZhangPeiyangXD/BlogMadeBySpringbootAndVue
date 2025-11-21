package com.itzpy.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.itzpy.blog.dao.mapper.SysUserMapper;
import com.itzpy.blog.dao.pojo.SysUser;
import com.itzpy.blog.service.SysUserService;
import com.itzpy.blog.utils.JWTUtils;
import com.itzpy.blog.vo.ErrorCode;
import com.itzpy.blog.vo.LoginUserVo;
import com.itzpy.blog.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    SysUserMapper sysUserMapper;
    
    // 添加JWTUtils和RedisTemplate依赖
    @Autowired
    private JWTUtils jwtUtils;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 根据id查询用户
     * @param id 用户id
     * @return 用户信息
     */
    @Override
    public SysUser findUserById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);

        if (sysUser == null) {
            sysUser = new SysUser();
            sysUser.setNickname("站主");
        }

        return sysUser;
    }

    /**
     * 根据账号和密码查询用户
     * @param account 账号
     * @param password 密码
     * @return 用户信息
     */
    @Override
    public SysUser findUserByCountAndPassword(String account, String password) {
        SysUser sysUser = sysUserMapper.selectByAccountAndPassword(account, password);

        return sysUser;
    }


    /**
     * 根据token查询用户
     * @param token 用户的token
     * @return result(用户信息)
     */
    @Override
    public Result findUserByToken(String token) {
        /**
         *  1.校验 token合法性：
         *      是否为空，解析是否成功，redis是否存在
         *  2.如果校验失败，返回错误
         *  3.如果成功，返回LoginUserVo
         */
        // 手动解析token
        if(StringUtils.isBlank(token)){
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
        }

        Map<String, Object> stringObjectMap = jwtUtils.checkToken(token);
        if(stringObjectMap == null){
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
        }

        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if(StringUtils.isBlank(userJson)){
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
        }

        SysUser sysUser = JSON.parseObject(userJson, SysUser.class);
        
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setId(sysUser.getId().toString());
        loginUserVo.setNickname(sysUser.getNickname());
        loginUserVo.setAvatar(sysUser.getAvatar());
        loginUserVo.setAccount(sysUser.getAccount());

        return Result.success(loginUserVo);
    }
}