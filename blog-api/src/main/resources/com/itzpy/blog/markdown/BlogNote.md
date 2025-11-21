# 博客项目开发笔记

 开发时使用1-3的那个前端文件，不要用上线的，因为后面对项目，数据库的表都有优化。

## 1. 前置知识

- Spring Boot
- Spring MVC
- MyBatis Plus 或者 MyBatis，我个人用的是MyBatis实现对应接口

*这样基本能做到看了接口文档需求自己实现对应的功能。*    
*比一味的抄代码效率和体验好很多。*

## 2. 项目配置相关

1. 使用老师的JDK版本和对应版本的依赖项
2. 包、类的路径注意和扫描的路径相同（mapper.xml映射的时候）
3. 确保MyBatis配置正确，mapper-locations路径与实际文件路径一致


## 3.jwt相关：

1. 使用jwt工具类创建jwt的token令牌。
2. 注意直接给数据库插入用户账密数据的时候，回车键也会被插入（找了半个小时，我还以为是加密出错了）。


## 4. 登录相关
1. 登录时候获取用户信息，创建一个currentUser方法，接收token参数。
2. 从客户端发送的HTTP请求头中获取名为Authorization的字段值，并将其作为token参数传入currentUser方法中，    
获取用户信息并返回给前端。注意WebMvcConfig配置中跨域配置，允许跨域请求。
3. 创建LoginInterceptor拦截器并在WebMvcConfig注册。
4. 注意放行login/** 请求 和 articles/** 和 articles 请求 和 tags请求 和 tags/** 请求 和 /users/currentUser 请求。


