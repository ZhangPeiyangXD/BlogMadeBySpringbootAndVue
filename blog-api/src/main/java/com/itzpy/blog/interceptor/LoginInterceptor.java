package com.itzpy.blog.interceptor;

import com.alibaba.fastjson.JSON;
import com.itzpy.blog.dao.pojo.SysUser;
import com.itzpy.blog.utils.JWTUtils;
import com.itzpy.blog.vo.Result;
import com.itzpy.blog.vo.ErrorCode;
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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        
        // 处理OPTIONS预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 如果是login开头的请求，直接放行
        if (uri.startsWith("/login")) {
            return true;
        }

        // 检查是否携带JWT token
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // 验证JWT token并检查Redis中是否存在对应的用户信息
            String token = authorizationHeader.substring(7);
            Result result = validateJwtTokenAndCheckRedis(token);
            if (result != null) {
                // token验证失败，返回错误信息
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().print(JSON.toJSONString(result));
                return false;
            }
            // token验证成功，放行请求
            return true;
        }

        // 如果既不是login请求又没有有效的JWT，则拒绝访问
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

        // 验证JWT token
        Map<String, Object> tokenMap = jwtUtils.checkToken(token);
        if (tokenMap == null) {
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
        }

        // 检查Redis中是否存在对应的用户信息（验证用户是否已登出）
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if (StringUtils.isBlank(userJson)) {
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
        }

        // 尝试解析用户信息
        try {
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
}