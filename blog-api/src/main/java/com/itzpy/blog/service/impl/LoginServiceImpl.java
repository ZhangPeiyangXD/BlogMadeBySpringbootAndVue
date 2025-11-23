package com.itzpy.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.itzpy.blog.dao.mapper.SysUserMapper;
import com.itzpy.blog.dao.pojo.SysUser;
import com.itzpy.blog.service.LoginService;
import com.itzpy.blog.utils.JWTUtils;
import com.itzpy.blog.dao.pojo.ErrorCode;
import com.itzpy.blog.dao.pojo.Result;
import com.itzpy.blog.vo.params.LoginParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {
    // 重新引入SysUserMapper，避免循环依赖
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private JWTUtils jwtUtils;

    @Value("${jwt.token-expiration}")
    private Long tokenExpiration;

    @Value("${jwt.salt}")
    private String salt;


    /**
     * 登录功能
     * @param loginParam 登录参数
     * @return result(用户信息)
     */
    @Override
    public Result login(LoginParam loginParam) {
        // 1.验证参数合法性
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        String nickname = loginParam.getNickname();
        if (account == null || account.isEmpty() || password == null || password.isEmpty()) {
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }

        // 2.根据account查password并比较
        // 密码加密
        password =  DigestUtils.md5Hex(password +  salt);

        // 直接使用Mapper而不是Service避免循环依赖
        SysUser sysUser = sysUserMapper.selectByAccountAndPassword(account, password);
        if (sysUser == null) {
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(), ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }

        // 3.如果存在，生成jwt的token
        String token = jwtUtils.createToken(sysUser.getId());

        // 4.存储token到redis中（token: user），expireTime天过期，重新登录.
        redisTemplate.opsForValue().set("TOKEN_" + token, JSON.toJSONString(sysUser), tokenExpiration);

        return Result.success(token);
    }


    /**
     * 验证token
     * @param token 用户的token
     * @return result(用户信息)
     */
    @Override
    public SysUser checkToken(String token) {
        if(StringUtils.isBlank( token)){
            return null;
        }

        Map<String, Object> stringObjectMap = jwtUtils.checkToken(token);
        if(stringObjectMap == null){
            return null;
        }

        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if(StringUtils.isBlank(userJson)){
            return null;
        }

        SysUser sysUser = JSON.parseObject(userJson, SysUser.class);
        return sysUser;
    }
}