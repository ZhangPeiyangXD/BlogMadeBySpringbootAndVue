package com.itzpy.blog.handler;

import com.itzpy.blog.vo.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

// 统一异常处理(对所有的Controller进行AOP加强)
@ControllerAdvice
public class AllExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody//响应给前端json数据
    public Result doException(Exception ex) {
        ex.printStackTrace();

        return Result.fail(-999, "服务器异常，请联系管理员");
    }
}
