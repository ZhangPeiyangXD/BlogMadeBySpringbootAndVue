package com.itzpy.blog.admin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源配置
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/com/itzpy/blog/static/css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/com/itzpy/blog/static/js/");
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/com/itzpy/blog/static/img/");
        registry.addResourceHandler("/plugins/**")
                .addResourceLocations("classpath:/com/itzpy/blog/static/plugins/");
        registry.addResourceHandler("/pages/**")
                .addResourceLocations("classpath:/com/itzpy/blog/static/pages/");
        registry.addResourceHandler("/*.html")
                .addResourceLocations("classpath:/com/itzpy/blog/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 添加视图控制器
        registry.addViewController("/").setViewName("redirect:/login.html");
        registry.addViewController("/login").setViewName("redirect:/login.html");
    }
}