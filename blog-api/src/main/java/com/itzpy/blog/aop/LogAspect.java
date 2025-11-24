package com.itzpy.blog.aop;

import com.alibaba.fastjson.JSON;
import com.itzpy.blog.utils.HttpContextUtils;
import com.itzpy.blog.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class LogAspect {
    @Pointcut("@annotation(com.itzpy.blog.aop.LogAnnotation)")
    public void log() {}

    @Around("log()")
    public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable{
        long begin = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();
        long time = end - begin;

        //保存日志
        recordLog(joinPoint,time);

        return result;
    }

    private void recordLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);

        log.info("===============log start================");
        log.info("module:{}", logAnnotation.module());
        log.info("operator:{}", logAnnotation.operator());

        // 请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.info("request method:{}", className + "." + methodName + "()");

        // 请求的参数
        Object[] args = joinPoint.getArgs();
        if(args != null && args.length > 0) {
            String params = "";
            Object arg = args[0];
            // 特殊处理MultipartFile类型参数，避免序列化异常
            if (arg instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) arg;
                params = "{\"fileName\":\"" + file.getOriginalFilename() + "\", \"size\":\"" + file.getSize() + "\"}";
            } else {
                params = JSON.toJSONString(arg);
            }
            log.info("params:{}", params);
        }

        //获取request，设置ip地址
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        log.info("ip:{}", IpUtils.getIpAddr(request));

        log.info("execute time:{} ms", time);
        log.info("===============log end================");
    }
}
