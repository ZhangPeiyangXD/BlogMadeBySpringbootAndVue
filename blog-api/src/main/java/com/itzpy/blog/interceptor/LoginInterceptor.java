package com.itzpy.blog.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // 如果是login开头的请求，直接放行
        if (uri.startsWith("/login")) {
            return true;
        }

        // 检查是否携带JWT token
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // 这里应该添加JWT验证逻辑
            // 如果JWT有效，返回true；否则返回false并设置相应状态码
            return validateJwtToken(authorizationHeader.substring(7));
        }

        // 如果既不是login请求又没有有效的JWT，则拒绝访问
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

    private boolean validateJwtToken(String token) {
        // 实现JWT验证逻辑
        // 返回true表示token有效，false表示无效
        return true; // 示例代码，实际应替换为真正的验证逻辑
    }
}
