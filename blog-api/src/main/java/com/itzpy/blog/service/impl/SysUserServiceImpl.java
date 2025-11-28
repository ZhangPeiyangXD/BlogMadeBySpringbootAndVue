package com.itzpy.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.itzpy.blog.dao.mapper.SysUserMapper;
import com.itzpy.blog.dao.pojo.SysUser;
import com.itzpy.blog.service.SysUserService;
import com.itzpy.blog.utils.JWTUtils;
import com.itzpy.blog.dao.pojo.ErrorCode;
import com.itzpy.blog.vo.LoginUserVo;
import com.itzpy.blog.dao.pojo.Result;
import com.itzpy.blog.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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
     * 根据id查询用户信息
     * @param id 用户id
     * @return 用户信息
     */
    @Override
    public UserVo findUserVoById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser == null){
            sysUser = new SysUser();
            sysUser.setId(1L);
            sysUser.setAvatar("/static/img/logo.b3a48c0.png");
            sysUser.setNickname("码神之路");
        }
        UserVo userVo  = new UserVo();
        BeanUtils.copyProperties(sysUser,userVo);
        userVo.setId(String.valueOf(sysUser.getId()));
        return userVo;
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
         *      是否为空，redis是否存在
         *  2.如果校验失败，返回错误
         *  3.如果成功，返回LoginUserVo
         */
        // 手动解析token
        if(StringUtils.isBlank(token)){
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
        }

        try {
            // 检查redisTemplate是否为null
            if (redisTemplate == null) {
                return Result.fail(ErrorCode.SYSTEM_ERROR.getCode(), "Redis模板初始化失败");
            }

            // 先检查key是否存在
            String key = "TOKEN_" + token;
            if (!redisTemplate.hasKey(key)) {
                return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
            }

            // 获取value长度，防止过大的value导致OOM
            Long valueLength = redisTemplate.boundValueOps(key).size();
            if (valueLength == null || valueLength > 10000) { // 限制10KB
                // 删除这个可能有问题的key
                redisTemplate.delete(key);
                return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), "Token数据异常");
            }

            String userJson = redisTemplate.opsForValue().get(key);
            if(StringUtils.isBlank(userJson)){
                return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
            }

            // 检查JSON字符串长度
            if (userJson.length() > 10000) {
                // 删除这个可能有问题的key
                redisTemplate.delete(key);
                return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), "Token数据过大");
            }

            // 只解析需要的字段，而不是整个SysUser对象
            Map<String, Object> userMap = JSON.parseObject(userJson, Map.class);
            
            if (userMap != null && userMap.containsKey("id")) {
                LoginUserVo loginUserVo = new LoginUserVo();
                loginUserVo.setId(String.valueOf(userMap.get("id")));
                loginUserVo.setNickname((String) userMap.get("nickname"));
                loginUserVo.setAvatar((String) userMap.get("avatar"));
                loginUserVo.setAccount((String) userMap.get("account"));

                return Result.success(loginUserVo);
            } else {
                return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), "Token数据格式错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), "Token解析失败: " + e.getMessage());
        } catch (OutOfMemoryError e) {
            // 特别处理内存溢出错误
            System.err.println("内存溢出错误: " + e.getMessage());
            e.printStackTrace();
            // 清理可能的异常数据
            try {
                redisTemplate.delete("TOKEN_" + token);
            } catch (Exception ex) {
                // 忽略清理异常
            }
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), "Token数据异常，请重新登录");
        }
    }

}