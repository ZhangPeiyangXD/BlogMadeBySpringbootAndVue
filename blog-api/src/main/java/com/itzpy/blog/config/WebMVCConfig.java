package com.itzpy.blog.config;

import com.itzpy.blog.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 跨域配置
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }


    /**
    *   添加自定义拦截器，用于处理登录和JWT验证逻辑
    */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加自定义拦截器，用于处理登录和JWT验证逻辑
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns("/login**", "/articles", "/articles/**", "/tags","/tags/**", "/users/currentUser", "/logout");
    }
}