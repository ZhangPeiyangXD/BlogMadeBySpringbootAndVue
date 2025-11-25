package com.itzpy.blog.interceptor;

import com.alibaba.fastjson.JSON;
import com.itzpy.blog.dao.pojo.SysUser;
import com.itzpy.blog.service.LoginService;
import com.itzpy.blog.utils.JWTUtils;
import com.itzpy.blog.utils.UserThreadLocal;
import com.itzpy.blog.dao.pojo.Result;
import com.itzpy.blog.dao.pojo.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JWTUtils jwtUtils;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private LoginService loginService;

    /**
     * 请求处理之前执行
     * @param request 请求对象
     * @param response 响应对象
     * @param handler 处理器对象
     * @return true表示继续处理请求，false表示拒绝处理请求
     * @throws Exception 抛出的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        
        // 处理OPTIONS预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 处理一些公开接口，不需要token验证
        if (uri.equals("/register") ||
            uri.equals("/login")||
            uri.equals("/comments/create/change")
            ) {
            return true;
        }

        // 检查是否携带JWT token
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // 验证JWT token并检查Redis中是否存在对应的用户信息
            String token = authorizationHeader.substring(7);
            // 检查token是否为空或无效
            if (StringUtils.isBlank(token)) {
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().print(JSON.toJSONString(Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg())));
                return false;
            }
            
            Result result = validateJwtTokenAndCheckRedis(token);
            if (result != null) {
                // token验证失败，返回错误信息
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().print(JSON.toJSONString(result));
                return false;
            }
            
            // 登录成功时，从Redis中获取用户信息并存储到UserThreadLocal中
            String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
            if (!StringUtils.isBlank(userJson)) {
                try {
                    SysUser sysUser = JSON.parseObject(userJson, SysUser.class);
                    if (sysUser != null) {
                        UserThreadLocal.put(sysUser);
                    }
                } catch (Exception e) {
                    // 解析用户信息失败，继续执行但不存储用户信息
                }
            }

            // 放行
            return true;
        }

        // 如果既不是登录/注册请求又没有有效的JWT，则拒绝访问
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().print(JSON.toJSONString(Result.fail(ErrorCode.NO_LOGIN.getCode(), ErrorCode.NO_LOGIN.getMsg())));
        return false;
    }

    /**
     * 验证JWT token并检查Redis中是否存在对应的用户信息
     * @param token JWT token
     * @return 如果验证失败返回Result对象，否则返回null表示验证成功
     */
    private Result validateJwtTokenAndCheckRedis(String token) {
        // 检查token是否为空
        if (StringUtils.isBlank(token)) {
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
        }

        // 检查jwtUtils是否为null
        if (jwtUtils == null) {
            return Result.fail(ErrorCode.SYSTEM_ERROR.getCode(), "JWT工具初始化失败");
        }

        // 验证JWT token
        Map<String, Object> tokenMap = jwtUtils.checkToken(token);
        if (tokenMap == null) {
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
        }

        // 检查redisTemplate是否为null
        if (redisTemplate == null) {
            return Result.fail(ErrorCode.SYSTEM_ERROR.getCode(), "Redis模板初始化失败");
        }

        // 检查Redis中是否存在对应的用户信息（验证用户是否已登出）
        try {
            String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
            if (StringUtils.isBlank(userJson)) {
                return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
            }

            // 尝试解析用户信息
            SysUser sysUser = JSON.parseObject(userJson, SysUser.class);
            if (sysUser == null) {
                return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
            }
        } catch (Exception e) {
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
        }

        // 验证成功
        return null;
    }


    /**
     * 请求处理完成后执行（删除用户线程，防止内存泄漏）
     * @param request 请求对象
     * @param response 响应对象
     * @param handler 处理器对象
     * @param ex 异常对象
     * @throws Exception 抛出的异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求处理完成后，清空当前线程中的用户信息
        UserThreadLocal.remove();
    }
}