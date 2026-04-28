# JWT 身份认证完整实现流程（教学版）

> 适用对象：已掌握 Spring Boot + Vue 3 基础，准备学习项目级身份认证的学生。
> 前置条件：项目已有登录接口（UUID 简单 token），数据库可正常运行。

---

## 一、为什么需要 JWT

### 1.1 现有方案的问题

之前我们的登录接口是这样的：

```java
// AuthController.java —— 旧版本
String token = UUID.randomUUID().toString();
```

UUID token 存在三个致命缺陷：

| 缺陷 | 说明 |
|------|------|
| **无用户信息** | 拿到 token 也不知道是谁、什么角色 |
| **无过期时间** | token 永远有效，泄露后无法自然失效 |
| **无签名校验** | 任何人都可以伪造一个 UUID，后端无法辨别真假 |

### 1.2 JWT 解决了什么

JWT（JSON Web Token）是一个开放标准（RFC 7519），它的核心优势：

- **自带用户信息**：token 内部嵌入了 userId、username、role 等数据
- **自带过期时间**：token 签发时就设好有效期，过期自动失效
- **自带数字签名**：用密钥签名，任何人篡改内容都会导致校验失败

### 1.3 JWT 的结构

一个 JWT token 长这样（三部分用 `.` 分隔）：

```
eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.xXxXxXxXxXxXxXxXxXx
|----- Header -----|----- Payload ----|----- Signature -----|
```

| 部分 | 内容 | 说明 |
|------|------|------|
| **Header** | `{"alg": "HS256"}` | 声明签名算法 |
| **Payload** | `{"userId": 1, "username": "admin", "role": "teacher", "exp": 1234567890}` | 存放用户数据（claims） |
| **Signature** | `HMAC-SHA256(Header.Payload, secret)` | 用密钥计算的签名，防篡改 |

> 重点理解：Payload 只是 Base64 编码，**不是加密**，所以不要在 JWT 里放密码等敏感信息。安全靠的是 **Signature 防篡改** + **HTTPS 防窃听**。

---

## 二、整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                        浏览器 (Frontend)                      │
│                                                             │
│   Login.vue          request.js            router/index.js  │
│   ① 登录获取JWT ──→ ② 每次请求自动带    ③ 路由守卫检查token  │
│   存入localStorage    Authorization头     无token→踢回登录页  │
└──────────────────────────┬──────────────────────────────────┘
                           │  HTTP Request
                           │  Authorization: Bearer eyJ...
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                       后端 (Spring Boot)                      │
│                                                             │
│   JwtInterceptor ──→ 校验签名 + 解析claims ──→ Controller    │
│   拦截每个请求         注入 userId/username/role   执行业务    │
│       │                                                     │
│       │ token无效/过期                                       │
│       ▼                                                     │
│   返回 401 "未登录或token已过期"                               │
└─────────────────────────────────────────────────────────────┘
```

核心流程一句话：**登录换 token，每次请求带 token，后端校验 token 才放行。**

---

## 三、后端实现

### 3.1 添加 JWT 依赖

**文件：`pom.xml`**

在 `<dependencies>` 末尾添加 jjwt 三件套：

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

说明：
- `jjwt-api`：编译时需要，提供 JWT 操作的接口
- `jjwt-impl`：运行时实现，提供具体的加密和解析逻辑
- `jjwt-jackson`：运行时 JSON 处理，负责 claims 的序列化和反序列化

> 为什么 impl 和 jackson 是 `runtime` 范围？因为你的代码只依赖 api 接口，具体实现由 Spring 在运行时注入，编译时不需要。

### 3.2 添加 JWT 配置

**文件：`application.yml`**

```yaml
jwt:
  secret: aWxvdmVwcm9ncmFtbWluZ2FuZHNlY3VyaXR5Zm9yZXZlcg==
  expiration: 86400000
```

| 配置项 | 值 | 说明 |
|--------|-----|------|
| `jwt.secret` | Base64 字符串 | 签名密钥，至少 256 位（32 字节）。生产环境必须保密 |
| `jwt.expiration` | `86400000` | token 有效期，单位毫秒。86400000ms = 24 小时 |

### 3.3 创建 JwtUtil 工具类

**新建文件：`utils/JwtUtil.java`**

这是 JWT 的核心工具，负责三件事：**生成 token**、**解析 token**、**校验 token**。

```java
package com.lrm.aiplatform.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component  // 注册为 Spring Bean，让 Controller 可以注入使用
public class JwtUtil {

    private final SecretKey key;       // 签名密钥
    private final long expiration;     // 过期时间（毫秒）

    // 构造方法：从 application.yml 读取配置
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        // 将字符串密钥转为 HMAC-SHA256 算法所需的 SecretKey 对象
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /**
     * 生成 JWT token
     * @param userId   用户ID
     * @param username 用户名
     * @param role     角色（teacher / student）
     * @return JWT 字符串
     */
    public String generateToken(Long userId, String username, String role) {
        // 第一步：将用户信息放入 claims（Payload 部分）
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        Date now = new Date();
        // 第二步：构建 JWT
        return Jwts.builder()
                .claims(claims)                                      // 自定义数据
                .subject(username)                                   // 主题（标准字段）
                .issuedAt(now)                                       // 签发时间
                .expiration(new Date(now.getTime() + expiration))    // 过期时间
                .signWith(key)                                       // 用密钥签名
                .compact();                                          // 输出字符串
    }

    /**
     * 解析 JWT token，提取其中的 claims
     * @param token JWT 字符串
     * @return Claims 对象，可从中取出 userId、username、role 等
     * @throws io.jsonwebtoken.JwtException 如果 token 无效或过期
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)        // 用同一个密钥验证签名
                .build()
                .parseSignedClaims(token)
                .getPayload();           // 获取 Payload 部分
    }

    /**
     * 校验 token 是否有效
     * @return true=有效, false=无效或过期
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            // 签名不对、过期、格式错误等所有异常统一返回 false
            return false;
        }
    }
}
```

**逐段讲解：**

| 代码行 | 讲什么 |
|--------|--------|
| `@Component` | Spring 自动扫描并创建单例 Bean，哪里需要就在哪里 `@Autowired` 注入 |
| `@Value("${jwt.secret}")` | 从 `application.yml` 读取配置值，注入到构造方法参数 |
| `Keys.hmacShaKeyFor(...)` | 把字符串密钥转成 Java 加密库能用的 Key 对象，算法是 HMAC-SHA256 |
| `Jwts.builder()` | 建造者模式，链式调用设置各种属性 |
| `.signWith(key)` | 最关键的一步：用密钥对 Header + Payload 做 HMAC-SHA256 签名 |
| `.compact()` | 把所有部分拼接成最终的 `xxx.yyy.zzz` 字符串 |
| `.verifyWith(key)` | 解析时用同一个密钥验签，签名不对会抛异常 |

### 3.4 创建 JwtInterceptor 拦截器

**新建文件：`utils/JwtInterceptor.java`**

拦截器的作用：**在每个请求到达 Controller 之前，检查是否带了有效 token**。

```java
package com.lrm.aiplatform.utils;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 在 Controller 方法执行之前调用
     * @return true=放行, false=拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 第一步：从请求头中提取 token
        String token = extractToken(request);

        // 第二步：校验 token
        if (token == null || !jwtUtil.validateToken(token)) {
            // 返回 JSON 格式的 401 错误
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write(
                "{\"code\":401,\"message\":\"未登录或token已过期\",\"data\":null}"
            );
            return false;  // 拦截请求，不往后传
        }

        // 第三步：解析 token 中的用户信息，注入到 request 属性中
        Claims claims = jwtUtil.parseToken(token);
        request.setAttribute("userId", claims.get("userId", Long.class));
        request.setAttribute("username", claims.getSubject());
        request.setAttribute("role", claims.get("role", String.class));

        return true;  // 放行，交给 Controller 处理
    }

    /**
     * 从请求中提取 token，支持两种方式：
     * 1. 标准方式：Authorization: Bearer <token>
     * 2. 兼容旧版：自定义 token 头
     */
    private String extractToken(HttpServletRequest request) {
        // 优先取标准 Authorization 头
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);  // 去掉 "Bearer " 前缀
        }
        // 兼容旧的 token 头
        String customToken = request.getHeader("token");
        if (customToken != null && !customToken.isEmpty()) {
            return customToken;
        }
        return null;
    }
}
```

**关键点讲解：**

| 概念 | 说明 |
|------|------|
| `HandlerInterceptor` | Spring MVC 提供的拦截器接口，比 Filter 更简单 |
| `preHandle` | 在 Controller 方法执行前调用，返回 false 则请求被拦截 |
| `request.setAttribute(...)` | 把解析出的用户信息存入 request，后续 Controller 可以 `request.getAttribute("userId")` 获取当前登录用户 |
| `response.setStatus(401)` | HTTP 401 Unauthorized，标准的"未认证"状态码 |
| `Bearer ` 前缀 | HTTP 认证规范写法，`Bearer` 意为"持票人"，即持有这个 token 的人就是合法用户 |

### 3.5 注册拦截器

**修改文件：`config/ZhipuAiConfig.java`**

拦截器写好了，还需要"安装"到 Spring MVC 的拦截器链中，并指定哪些路径需要拦截、哪些放行。

```java
// 新增的 import
import com.lrm.aiplatform.utils.JwtInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Configuration
public class ZhipuAiConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    // 构造方法注入 JwtInterceptor
    public ZhipuAiConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    // ... 其他配置不变 ...

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")           // 拦截所有请求
                .excludePathPatterns(             // 除了以下路径：
                        "/login",                 //   登录接口
                        "/user/login",            //   用户登录
                        "/user/register",         //   用户注册
                        "/user/add"               //   添加用户
                );
    }
}
```

**为什么这些路径要放行？**
- `/login` 和 `/user/login`：用户还没登录，当然不能要求带 token
- `/user/register` 和 `/user/add`：注册新用户也不需要登录

> 教学提示：`addPathPatterns("/**")` 中的 `/**` 表示匹配所有层级的路径，如 `/user/query/1`、`/ai/chat` 等。

### 3.6 修改登录接口，返回 JWT

**修改文件：`controller/AuthController.java`**

之前的登录返回的是 `UUID.randomUUID().toString()`，现在改为真正的 JWT。

```java
// 新增依赖注入
private final JwtUtil jwtUtil;

public AuthController(IUserService userService, JwtUtil jwtUtil) {
    this.userService = userService;
    this.jwtUtil = jwtUtil;
}

@PostMapping("/login")
public Result<LoginDTO> login(@RequestParam String username,
                               @RequestParam String password) {
    User user = userService.login(username, password);
    if (user == null) {
        return new Result<>(401, "用户名或密码错误", null);
    }

    // 改动点：用 JwtUtil 生成真正的 JWT token
    String token = jwtUtil.generateToken(
        user.getId(),        // 存入 userId
        user.getUsername(),  // 存入 username
        user.getRole()       // 存入 role（teacher / student）
    );

    LoginDTO loginDTO = new LoginDTO(user.getId(), user.getUsername(), token);
    return Result.success("登录成功", loginDTO);
}
```

`UserController` 中的 `/user/login` 也做同样修改，返回 `LoginDTO` 而非 `User` 对象。

**LoginDTO 的数据结构：**

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    private Long id;        // 用户ID
    private String username;// 用户名
    private String token;   // JWT token（核心！）
}
```

---

## 四、前端实现

### 4.1 修改请求拦截器：发送 JWT

**修改文件：`utils/request.js`**

之前是把 token 放在自定义头 `config.headers.token = token`，现在改为标准的 Bearer 格式。

```javascript
import axios from 'axios'
import router from '@/router'

const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000
})

// ========== 请求拦截器 ==========
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    // 标准 HTTP 认证格式：Authorization: Bearer <token>
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => {
  return Promise.reject(error)
})

// ========== 响应拦截器 ==========
request.interceptors.response.use(response => {
  const body = response.data

  // 后端返回的 code 是 401，说明 token 过期或无效
  if (body.code === 401) {
    localStorage.removeItem('token')
    router.push('/login')
    return Promise.reject(new Error(body.message || '未登录'))
  }

  // 正常情况：直接返回 data 字段，组件不用关心 code 和 message
  return body.data
}, error => {
  // HTTP 状态码就是 401（请求根本没到 Controller，被拦截器挡了）
  if (error.response && error.response.status === 401) {
    localStorage.removeItem('token')
    router.push('/login')
  }
  return Promise.reject(error)
})

export default request
```

**讲解要点：**

| 代码 | 问题引导学生 |
|------|-------------|
| `` `Bearer ${token}` `` | 为什么要加 "Bearer " 前缀？—— HTTP 标准规范，后端 `extractToken` 根据这个前缀来提取 |
| `body.code === 401` | 为什么有两种 401 判断？—— 一种是后端业务返回 code:401（密码错误），一种是 HTTP 层面 401（被拦截器挡了） |
| `return body.data` | 为什么返回 data 而不是整个 response？—— 组件只需要业务数据，code 和 message 由拦截器统一处理 |
| `router.push('/login')` | 注意这里 import router，普通的 js 文件也可以使用路由跳转 |

### 4.2 添加路由守卫

**修改文件：`router/index.js`**

路由守卫的作用：**在页面跳转之前检查是否有 token，没有就踢回登录页**。

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Home from '@/views/Home.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'          // 根路径自动跳到登录页
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true } // 标记：此页面需要登录
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// ========== 全局前置守卫 ==========
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 情况1：要访问需要登录的页面，但没有 token → 踢回登录页
  if (to.meta.requiresAuth && !token) {
    next('/login')
  }
  // 情况2：已登录，却想去登录页 → 直接跳到首页
  else if (to.path === '/login' && token) {
    next('/home')
  }
  // 情况3：其他情况正常放行
  else {
    next()
  }
})

export default router
```

**三个参数的含义：**

| 参数 | 含义 | 举例 |
|------|------|------|
| `to` | 要去的目标路由 | `{ path: '/home', meta: { requiresAuth: true } }` |
| `from` | 从哪个路由来 | `{ path: '/login' }` |
| `next()` | 放行函数 | `next()` 正常跳转；`next('/login')` 重定向到登录页 |

**三种情况图解：**

```
用户访问 /home
    │
    ├── 有 token ──→ next() → 进入 Home 页面
    │
    └── 无 token ──→ next('/login') → 重定向到登录页

用户访问 /login
    │
    ├── 有 token ──→ next('/home') → 已经登录了，去首页
    │
    └── 无 token ──→ next() → 正常进入登录页
```

### 4.3 修改登录页：存储 token

**修改文件：`views/Login.vue`**

之前有一行 bug：`localStorage.setItem('token', user.id)` 存的是用户 ID 而非 token。

```javascript
async login() {
  // 前端校验：用户名和密码不能为空
  if (!this.username.trim()) {
    alert('请输入用户名')
    return
  }
  if (!this.password.trim()) {
    alert('请输入密码')
    return
  }

  try {
    // 调用登录接口
    const data = await loginApi(this.username, this.password)

    // 后端返回的 data 结构：{ id, username, token }
    if (!data || !data.token) {
      alert('用户名或密码错误')
      return
    }

    // 关键修改：存真正的 JWT token，不是 user.id
    localStorage.setItem('token', data.token)
    localStorage.setItem('username', data.username)

    // 跳转到首页
    this.$router.push('/home')
  } catch (error) {
    alert('登录失败，请检查服务器')
  }
}
```

**修改前后对比：**

```javascript
// ❌ 旧代码 —— BUG：存的是用户 ID，不是 token
localStorage.setItem('token', user.id)

// ✅ 新代码 —— 存的是真正的 JWT
localStorage.setItem('token', data.token)
```

### 4.4 修改登录 API

**修改文件：`api/login.js`**

改用 AuthController 的 `/login` 接口（查询参数方式传参）。

```javascript
import request from '@/utils/request'

export function loginApi(username, password) {
  return request({
    url: '/login',
    method: 'post',
    params: { username, password }  // Query String: /login?username=xxx&password=xxx
  })
}
```

---

## 五、完整数据流（时序图）

```
 浏览器                    前端代码                    后端                   数据库
   │                         │                         │                      │
   │  1. 用户输入用户名密码    │                         │                      │
   │────────────────────────→│                         │                      │
   │                         │                         │                      │
   │                         │  2. POST /login          │                      │
   │                         │     ?username=&password= │                      │
   │                         │────────────────────────→│                      │
   │                         │                         │  3. 查询用户          │
   │                         │                         │─────────────────────→│
   │                         │                         │←─────────────────────│
   │                         │                         │  4. 验证密码(MD5)     │
   │                         │                         │                      │
   │                         │                         │  5. JwtUtil.generate │
   │                         │                         │     Token(userId,    │
   │                         │                         │     username, role)  │
   │                         │                         │                      │
   │                         │  6. { code:200,         │                      │
   │                         │      data:{id,username, │                      │
   │                         │      token:"eyJ..." } } │                      │
   │                         │←────────────────────────│                      │
   │                         │                         │                      │
   │  7. localStorage.set    │                         │                      │
   │     ('token', 'eyJ...') │                         │                      │
   │     router.push('/home')│                         │                      │
   │                         │                         │                      │
   │  8. 访问 /home          │                         │                      │
   │     router.beforeEach   │                         │                      │
   │     检查 token 存在 ✓    │                         │                      │
   │                         │                         │                      │
   │  9. 页面加载，调API      │                         │                      │
   │                         │  10. GET /user/list      │                      │
   │                         │      Authorization:      │                      │
   │                         │      Bearer eyJ...       │                      │
   │                         │────────────────────────→│                      │
   │                         │                         │  JwtInterceptor      │
   │                         │                         │  提取 Authorization  │
   │                         │                         │  parseToken 验签     │
   │                         │                         │  注入 userId 等属性   │
   │                         │                         │  放行 → Controller   │
   │                         │                         │                      │
   │                         │  11. { code:200,        │                      │
   │                         │       data:[...] }      │                      │
   │                         │←────────────────────────│                      │
   │                         │                         │                      │
   │  12. 页面渲染数据        │                         │                      │
   │                         │                         │                      │
```

---

## 六、文件变更清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `pom.xml` | 修改 | 添加 jjwt-api/impl/jackson 三个依赖 |
| `application.yml` | 修改 | 添加 jwt.secret 和 jwt.expiration 配置 |
| `utils/JwtUtil.java` | **新建** | JWT 生成 / 解析 / 校验核心工具类 |
| `utils/JwtInterceptor.java` | **新建** | 请求拦截器，校验 token 并注入用户信息 |
| `config/ZhipuAiConfig.java` | 修改 | 注册拦截器，配置放行路径 |
| `controller/AuthController.java` | 修改 | 登录返回 JWT token（替代 UUID） |
| `controller/UserController.java` | 修改 | `/user/login` 同步返回 JWT token |
| `dto/LoginDTO.java` | 已有 | 无需修改，token 字段直接承载 JWT |
| `utils/request.js` | 修改 | Authorization: Bearer 头 + 401 处理 |
| `router/index.js` | 修改 | 添加 beforeEach 路由守卫 |
| `views/Login.vue` | 修改 | 存储 data.token 而非 user.id |
| `api/login.js` | 修改 | 改用 /login 端点 |

---

## 七、测试与验证

### 7.1 单元测试（后端）

**文件：`test/.../utils/JwtUtilTest.java`**

```java
@Test
void generateToken_shouldReturnValidJwt() {
    String token = jwtUtil.generateToken(1L, "admin", "teacher");
    assertNotNull(token);
    // JWT 必须由三段组成，用 . 分隔
    assertTrue(token.split("\\.").length == 3);
}

@Test
void parseToken_shouldExtractClaims() {
    String token = jwtUtil.generateToken(1L, "admin", "teacher");
    Claims claims = jwtUtil.parseToken(token);
    assertEquals(1L, claims.get("userId", Long.class));
    assertEquals("admin", claims.getSubject());
    assertEquals("teacher", claims.get("role", String.class));
}

@Test
void validateToken_shouldReturnFalseForExpiredToken() {
    JwtUtil shortLived = new JwtUtil("...secret...", 1); // 1ms过期
    String token = shortLived.generateToken(1L, "admin", "teacher");
    Thread.sleep(10);
    assertFalse(shortLived.validateToken(token));
}
```

运行：
```bash
mvn test -Dtest=com.lrm.aiplatform.utils.JwtUtilTest
```

### 7.2 手动测试（curl）

```bash
# 1. 启动后端: mvn spring-boot:run

# 2. 登录获取 token
curl -X POST "http://localhost:8080/login?username=admin&password=123456"

# 返回示例:
# {"code":200,"message":"登录成功","data":{"id":1,"username":"admin","token":"eyJ..."}}

# 3. 不带 token 访问受保护接口 → 应返回 401
curl http://localhost:8080/user/list
# {"code":401,"message":"未登录或token已过期","data":null}

# 4. 带 token 访问（复制上一步返回的 token）
curl -H "Authorization: Bearer eyJ..." http://localhost:8080/user/list
# {"code":200,"message":"获取成功","data":[...]}

# 5. 带错误 token 访问 → 应返回 401
curl -H "Authorization: Bearer invalidtoken" http://localhost:8080/user/list
# {"code":401,"message":"未登录或token已过期","data":null}
```

### 7.3 前端功能测试清单

启动前端 `npm run dev`，按顺序验证：

- [ ] 访问 `http://localhost:5173` → 自动跳转到 `/login`
- [ ] 不输入用户名密码直接点登录 → 提示"请输入用户名"
- [ ] 输入错误密码点登录 → 提示"用户名或密码错误"
- [ ] 输入正确用户名密码 → 跳转到 `/home`
- [ ] F12 → Application → Local Storage → 确认 `token` 是 `eyJ...` 开头的 JWT
- [ ] F12 → Network → 确认 `/user/list` 请求头包含 `Authorization: Bearer eyJ...`
- [ ] 手动清除 LocalStorage 中的 token → 刷新页面 → 被踢回 `/login`
- [ ] 已登录状态手动访问 `/login` → 自动跳回 `/home`
- [ ] 等待 24 小时后（或修改后端 expiration 为 5000 测试）→ token 过期 → 访问接口返回 401 → 自动跳登录页

---

## 八、常见问题

### Q1: 为什么不直接用 Spring Security？
Spring Security 功能强大但学习曲线陡。对于教学项目，**手写拦截器 + JWT** 让学生能看到每一行代码的作用，理解认证的本质，而不是被框架的"魔法"遮蔽。实际生产项目中可以迁移到 Spring Security + JWT 的组合。

### Q2: token 存在 localStorage 安全吗？
不是最安全的方案（存在 XSS 攻击窃取风险），但对教学项目足够。生产环境的更优方案是 **httpOnly Cookie** 存储，JavaScript 无法读取，天然防 XSS。

### Q3: 为什么不用 BCrypt 替代 MD5？
MD5 已不安全，但本项目定位为教学，代码简单直观。实际项目应使用 `BCryptPasswordEncoder` 或 `argon2`。要替换只需改 `UserServiceImpl` 中密码加密和比对的两行代码。

### Q4: 用户登出怎么实现？
JWT 是无状态的，服务端不存储 token，所以"登出"只需：**前端删除 localStorage 中的 token 即可**。如果担心 token 被窃取后登出也无法失效，可以引入 Redis 黑名单机制，但教学阶段不需要。

### Q5: token 快过期了怎么办？
生产环境会引入 **refresh token** 机制：登录时同时返回短生命周期的 access token（如 30 分钟）和长生命周期的 refresh token（如 7 天）。access token 过期后用 refresh token 换新的。教学阶段直接用 24 小时过期即可。
