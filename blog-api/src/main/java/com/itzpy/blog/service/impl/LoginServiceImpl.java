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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

        // 4.存储精简的用户信息到redis中（token: user），expireTime天过期，重新登录.
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", sysUser.getId());
        userInfo.put("account", sysUser.getAccount());
        userInfo.put("nickname", sysUser.getNickname());
        userInfo.put("avatar", sysUser.getAvatar());
        
        try {
            // 存储到Redis并设置过期时间
            redisTemplate.opsForValue().set("TOKEN_" + token, JSON.toJSONString(userInfo), tokenExpiration, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // Redis存储失败
            e.printStackTrace();
            return Result.fail(ErrorCode.SYSTEM_ERROR.getCode(), "系统繁忙，请稍后重试");
        }

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

        // 检查jwtUtils是否为null
        if (jwtUtils == null) {
            return null;
        }

        Map<String, Object> stringObjectMap = jwtUtils.checkToken(token);
        if(stringObjectMap == null){
            return null;
        }

        try {
            String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
            if(StringUtils.isBlank(userJson)){
                return null;
            }

            // 检查JSON字符串长度
            if (userJson.length() > 10000) {
                // 删除这个可能有问题的key
                redisTemplate.delete("TOKEN_" + token);
                return null;
            }

            // 解析精简的用户信息
            Map<String, Object> userInfo = JSON.parseObject(userJson, Map.class);
            if (userInfo != null && userInfo.containsKey("id")) {
                SysUser sysUser = new SysUser();
                sysUser.setId(Long.valueOf(String.valueOf(userInfo.get("id"))));
                sysUser.setAccount((String) userInfo.get("account"));
                sysUser.setNickname((String) userInfo.get("nickname"));
                sysUser.setAvatar((String) userInfo.get("avatar"));
                return sysUser;
            }
        } catch (Exception e) {
            // Redis操作或JSON解析异常
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            // 特别处理内存溢出错误
            System.err.println("内存溢出错误: " + e.getMessage());
            // 清理可能的异常数据
            try {
                redisTemplate.delete("TOKEN_" + token);
            } catch (Exception ex) {
                // 忽略清理异常
            }
            return null;
        }
        return null;
    }
}