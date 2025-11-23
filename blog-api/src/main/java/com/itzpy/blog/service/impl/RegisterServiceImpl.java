package com.itzpy.blog.service.impl;

import com.itzpy.blog.dao.mapper.SysUserMapper;
import com.itzpy.blog.dao.pojo.SysUser;
import com.itzpy.blog.service.RegisterService;
import com.itzpy.blog.utils.JWTUtils;
import com.itzpy.blog.dao.pojo.ErrorCode;
import com.itzpy.blog.dao.pojo.Result;
import com.itzpy.blog.vo.params.LoginParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Value("${jwt.token-expiration}")
    long tokenExpiration;
    @Value("${jwt.salt}")
    String salt;

    /**
     * 用户注册
     * @param loginParam 用户信息: 账号 密码 昵称
     * @return null 插入成功
     */
    @Override
    @Transactional
    public Result register(LoginParam loginParam) {
        //1.判断参数是否合法
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        String nickname = loginParam.getNickname();

        if(account == null || account.trim().isEmpty()|| password == null || password.trim().isEmpty()){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }

        //2.判断账户是否存在，存在则返回账户已存在
        SysUser sysUser = sysUserMapper.selectByAccount(account);
        if (sysUser != null) {
            return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(), ErrorCode.ACCOUNT_EXIST.getMsg());
        }

        //3.生成用户和token
        password = DigestUtils.md5Hex(password +  salt);

        SysUser newUser = new SysUser();
        BeanUtils.copyProperties(loginParam, newUser);
        newUser.setCreateDate(System.currentTimeMillis());
        newUser.setLastLogin(System.currentTimeMillis());
        newUser.setPassword(password);

        sysUserMapper.insertSelective(newUser);

        return null;
    }
}