# 博客项目开发笔记

> 🛠️ 开发时使用1-3的那个前端文件，不要用上线的，因为后面对项目，数据库的表都有优化。

## 1. 前置知识 ⚙️

- Spring Boot
- Spring MVC
- MyBatis Plus 或者 MyBatis，我个人用的是MyBatis实现对应接口

> 💡 这样基本能做到看了接口文档需求自己实现对应的功能。  
> 比一味的抄代码效率和体验好很多。

## 2. 项目配置相关 🔧

1. 使用老师的JDK版本和对应版本的依赖项
2. 包、类的路径注意和扫描的路径相同（mapper.xml映射的时候）
3. 确保MyBatis配置正确，mapper-locations路径与实际文件路径一致

## 3. JWT相关 🔐

1. 使用jwt工具类创建jwt的token令牌
2. ⚠️ 注意直接给数据库插入用户账密数据的时候，回车键也会被插入（找了半个小时，我还以为是加密出错了）

## 4. 拦截器配置相关 🚧

### 4.1 创建拦截器
创建拦截器类，实现HandlerInterceptor接口

### 4.2 注册拦截器
在WebMvcConfig中注册拦截器

### 4.3 实现拦截逻辑

#### 处理OPTIONS预检请求
```java
if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
    return true;
}
```

✅ 添加了必要的依赖注入：
- 注入了JWTUtils用于JWT token验证
- 注入了RedisTemplate用于检查Redis中的用户信息

✅ 完善了token验证逻辑：
- 不仅验证JWT token的有效性，还检查Redis中是否存在对应的用户信息
- 这样可以确保用户登录后、登出前才能访问受保护的资源
- 如果用户已登出（Redis中的token被删除），即使JWT token本身有效也无法访问

✅ 添加了@Component注解：
- 使拦截器能够通过Spring进行依赖注入

## 5. 登录相关 🔒

1. 登录时候获取用户信息，创建一个currentUser方法，接收token参数
2. 从客户端发送的HTTP请求头中获取名为Authorization的字段值，并将其作为token参数传入currentUser方法中，获取用户信息并返回给前端。注意WebMvcConfig配置中跨域配置，允许跨域请求
3. 创建LoginInterceptor拦截器并在WebMvcConfig注册
4. ✅ 注意放行以下请求路径：
   - `/login/**`
   - `/articles/**` 和 `/articles`
   - `/tags/hot`
   - `/users/currentUser`
   - `/register`

## 6. 跨域配置 🌐

在WebMvcConfig中配置CORS：
- ✅ 允许来自`http://localhost:8080`的请求
- ✅ 允许GET、POST、PUT、DELETE、OPTIONS方法
- ✅ 允许所有请求头
- ✅ 允许携带凭证

## 7. 错误处理 ⚠️

统一使用Result对象返回错误信息，避免直接返回null或简单字符串，确保前后端数据交互的一致性。


## 8. 账户注册时的动态插入sql 💾

```xml
<insert id="insertSelective">
    insert into 表名
    <trim prefix="(" suffix=")" suffixOverrides=",">
        <if test="account != null"> account, </if>
        ...
    </trim>
    <trim prefix="values (" suffix=")" suffixOverrides=",">
        <if test="account != null"> #{account}, </if>
        ...
    </trim>
</insert>
```

其中，`<trim>`标签用于去除多余的逗号，`<if>`标签用于判断字段是否为空，不为空则插入该字段的值。

### `<trim>` 标签属性详解：

| 属性 | 作用 | 示例 |
|------|------|------|
| `prefix` | 在 SQL 语句的开头添加指定的内容 | `prefix="("` 会在 SQL 开头添加左括号 |
| `suffix` | 在 SQL 语句的结尾添加指定的内容 | `suffix=")"` 会在 SQL 结尾添加右括号 |
| `prefixOverrides` | 去除 SQL 语句开头的指定字符 | `prefixOverrides="AND "` 会去除 SQL 开头的 "AND " 字符串 |
| `suffixOverrides` | 去除 SQL 语句结尾的指定字符 | `suffixOverrides=","` 会去除 SQL 结尾的逗号 |

> 注意：我在实现账户注册时，产生token和保存redis使用的方法是调用登录的login方法。  
> 这就导致我忘记了密码被加密的问题，导致在查表的时候一直找不到对应账户。