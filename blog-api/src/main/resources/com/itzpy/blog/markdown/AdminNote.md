# Blog Admin 管理模块笔记

## 1. 模块概述

blog-admin 是博客系统的后台管理模块，主要负责权限管理和后台操作。该模块使用了 Spring Security 进行安全控制，并集成了 JWT 认证机制。

## 2. 权限表增删改查

### 2.1 数据模型

权限实体类：[Permission](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/pojo/Permission.java)

```java
@Data
public class Permission {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String path;
    private String description;
}
```

### 2.2 控制层接口

控制器：[AdminController](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/controller/AdminController.java)

1. 查询权限列表：
   - 接口：`POST /admin/permission/permissionList`
   - 参数：[PageParam](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/model/params/PageParam.java) 分页参数
   - 返回：[Result](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/vo/Result.java)<[PageResult](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/vo/PageResult.java)<[Permission](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/pojo/Permission.java)>>

2. 添加权限：
   - 接口：`POST /admin/permission/add`
   - 参数：[Permission](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/pojo/Permission.java) 对象
   - 返回：[Result](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/vo/Result.java)

3. 更新权限：
   - 接口：`POST /admin/permission/update`
   - 参数：[Permission](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/pojo/Permission.java) 对象（需包含ID）
   - 返回：[Result](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/vo/Result.java)

4. 删除权限：
   - 接口：`GET /admin/permission/delete/{id}`
   - 参数：权限ID
   - 返回：[Result](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/vo/Result.java)

### 2.3 服务层实现

服务类：[PermissionService](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/service/PermissionService.java)

实现了权限的增删改查功能，使用 MyBatis Plus 进行数据库操作：

- 列表查询：支持分页和关键字搜索
- 添加权限：直接插入数据库
- 更新权限：根据ID更新记录
- 删除权限：根据ID删除记录

### 2.4 数据访问层

Mapper接口：[PermissionMapper](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/mapper/PermissionMapper.java)

继承自 BaseMapper，拥有基本的 CRUD 操作能力。

## 3. Security 安全配置

安全配置类：[SecurityConfig](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/config/SecurityConfig.java)

### 3.1 密码加密

使用 BCryptPasswordEncoder 进行密码加密：

```java
@Bean
public BCryptPasswordEncoder bCryptPasswordEncoder(){
    return new BCryptPasswordEncoder();
}
```

### 3.2 Web 安全配置

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/css/**").permitAll()
        .antMatchers("/img/**").permitAll()
        .antMatchers("/js/**").permitAll()
        .antMatchers("/plugins/**").permitAll()
        .antMatchers("/admin/**").access("@authService.auth(request,authentication)")
        .antMatchers("/pages/**").authenticated()
        .and().formLogin()
        .loginPage("/login.html")
        .loginProcessingUrl("/login")
        .usernameParameter("username")
        .passwordParameter("password")
        .defaultSuccessUrl("/pages/main.html")
        .failureUrl("/login.html")
        .permitAll()
        .and().logout()
        .logoutUrl("/logout")
        .logoutSuccessUrl("/login.html")
        .permitAll()
        .and()
        .httpBasic()
        .and()
        .csrf().disable()
        .headers().frameOptions().sameOrigin();
}
```

关键配置点：
1. 静态资源（CSS、图片、JS等）允许所有访问
2. `/admin/**` 路径使用自定义权限认证服务 [@authService.auth](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/service/AuthService.java#L24-L50)
3. `/pages/**` 路径需要认证后才能访问
4. 自定义登录页面和登录处理接口
5. 自定义退出登录配置
6. 关闭 CSRF 防护（适用于 REST API）
7. 允许 iframe 嵌套（同源策略）

## 4. 登录认证

### 4.1 用户实体

管理员实体类：[Admin](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/pojo/Admin.java)

```java
@Data
public class Admin {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
}
```

### 4.2 登录流程

登录过程由 Spring Security 处理：

1. 用户访问登录页面 `/login.html`
2. 提交登录表单到 `/login` 接口
3. 使用配置的 usernameParameter 和 passwordParameter 获取用户名和密码
4. 成功登录后跳转到 `/pages/main.html`
5. 登录失败返回 `/login.html`

### 4.3 用户详情服务

用户详情服务：[SecurityUserService](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/service/SecurityUserService.java)

负责加载用户详细信息供 Spring Security 使用。

## 5. 权限认证

### 5.1 权限认证服务

权限认证服务：[AuthService](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/service/AuthService.java)

自定义权限认证逻辑：

```java
public boolean auth(HttpServletRequest request, Authentication authentication){
    //获取请求路径
    String requestURI = request.getRequestURI();
    
    //获取当前认证用户信息
    Object principal = authentication.getPrincipal();
    if (principal == null || "anonymousUser".equals(principal)){
        //未登录用户无权限
        return false;
    }
    
    UserDetails userDetails = (UserDetails) principal;
    String username = userDetails.getUsername();
    
    //查找管理员信息
    Admin admin = adminService.findAdminByUsername(username);
    if (admin == null){
        return false;
    }
    
    //超级管理员（ID为1）拥有所有权限
    if (1 == admin.getId()){
        return true;
    }
    
    //获取管理员拥有的权限列表
    Long id = admin.getId();
    List<Permission> permissionList = this.adminService.findPermissionByAdminId(id);
    
    //检查请求路径是否在权限列表中
    requestURI = StringUtils.split(requestURI,'?')[0];
    for (Permission permission : permissionList) {
        if (requestURI.equals(permission.getPath())){
            return true;
        }
    }
    return false;
}
```

### 5.2 权限查询

管理员服务：[AdminService](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/service/AdminService.java)

```java
public List<Permission> findPermissionByAdminId(Long adminId) {
    //SQL查询：SELECT * FROM permission where id in (select permission_id from admin_permission where admin_id=?)
    return adminMapper.findPermissionByAdminId(adminId);
}
```

权限 Mapper：[AdminMapper](file:///D:/blog_learn/myBlog/blog-parent/blog-admin/src/main/java/com/itzpy/blog/admin/mapper/AdminMapper.java)

```java
@Select("SELECT * FROM permission where id in (select permission_id from admin_permission where admin_id=#{adminId})")
List<Permission> findPermissionByAdminId(Long adminId);
```

## 6. 总结

blog-admin 模块采用 Spring Security 实现了完整的后台权限管理系统：

1. 通过自定义配置实现了灵活的安全控制策略
2. 使用 BCryptPasswordEncoder 保证密码安全
3. 实现了基于角色和路径的权限控制
4. 提供了权限的完整 CRUD 操作接口
5. 支持超级管理员拥有全部权限
6. 通过自定义认证服务实现了细粒度的权限判断