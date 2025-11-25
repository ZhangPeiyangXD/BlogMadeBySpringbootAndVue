package com.itzpy.blog.admin.handler;

import com.itzpy.blog.admin.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(HttpServletRequest request, Exception ex) {
        log.error("请求地址: {}, 发生异常: ", request.getRequestURL(), ex);
        
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/error.html");
        return modelAndView;
    }
    
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException ex) {
        log.error("系统运行时异常: ", ex);
        return Result.fail(500, "系统运行时异常: " + ex.getMessage());
    }
}