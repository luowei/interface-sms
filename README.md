# interface-sms

## 项目简介

interface-sms 是一个基于 Spring MVC 框架的短信推送服务接口系统，采用自定义拦截器实现用户身份验证的 JSON RESTful API 示例项目。该系统主要为移动客户端（Android/iOS）提供短信通知推送服务，支持用户登录认证、消息推送、分组管理、iOS APNs 推送等功能。

**视频讲解**: [接口示例讲解](http://www.tudou.com/programs/view/GWKxgCWHlJQ)

## 技术栈

### 后端框架
- **Spring Framework 3.x** - 核心依赖注入和 AOP 框架
- **Spring MVC 3.x** - Web MVC 框架，处理 RESTful API 请求
- **Spring JDBC** - 数据库访问

### 数据库
- **Microsoft SQL Server** - 关系型数据库
- **Apache Commons DBCP** - 数据库连接池

### 缓存
- **EhCache 2.4.3** - 内存缓存，用于用户会话和验证码缓存

### 消息推送
- **JavaAPNs 2.2.1** - iOS APNs 推送服务
- **Bouncy Castle 1.46** - 加密库，用于 iOS 推送证书处理

### 工具库
- **Jackson 1.8.1** - JSON 序列化/反序列化
- **Apache Commons Lang3 3.1** - 通用工具类
- **Apache Commons IO 2.3** - IO 工具类
- **Apache Commons FileUpload 1.2.2** - 文件上传
- **Apache POI 3.9** - Excel 文件处理
- **Google Guava r08** - 通用工具库
- **Log4j 1.2.17** - 日志框架
- **JUnit 4.8** - 单元测试框架

### 其他
- **JSTL 1.2** - JSP 标准标签库
- **AspectJ 1.7.1** - AOP 切面编程
- **DOM4J 1.6.1** - XML 处理

## 项目结构

```
interface-sms/
├── src/                                    # 源代码目录
│   ├── com/rootls/                         # 主包路径
│   │   ├── common/                         # 公共模块
│   │   │   ├── bean/                       # 公共 Bean 类
│   │   │   │   ├── Config.java             # 配置类
│   │   │   │   ├── JsonRet.java            # JSON 响应封装
│   │   │   │   ├── NeedLogin.java          # 登录验证注解
│   │   │   │   └── InerCache.java          # 内部缓存
│   │   │   ├── utils/                      # 工具类
│   │   │   │   └── EHCacheUtil.java        # EhCache 缓存工具
│   │   │   ├── UserInterceptor.java        # 用户认证拦截器（核心）
│   │   │   ├── ArgumentFilter.java         # 参数过滤器
│   │   │   ├── ExceptionHandler.java       # 异常处理器
│   │   │   └── BaseController.java         # 控制器基类
│   │   ├── user/                           # 用户模块
│   │   │   ├── User.java                   # 用户实体
│   │   │   ├── UserController.java         # 用户控制器
│   │   │   └── UserRepository.java         # 用户数据访问层
│   │   ├── sms/                            # 短信模块
│   │   │   ├── Sms.java                    # 短信实体
│   │   │   ├── Group.java                  # 分组实体
│   │   │   ├── Reply.java                  # 回复实体
│   │   │   ├── SmsController.java          # 短信控制器
│   │   │   └── SmsRepository.java          # 短信数据访问层
│   │   ├── notification/                   # 通知推送模块
│   │   │   ├── IOSPush.java                # iOS 推送实体
│   │   │   ├── IOSPushTaskServlet.java     # iOS 推送任务服务
│   │   │   ├── NotificationRepository.java # 通知数据访问层
│   │   │   ├── NotificationResult.java     # 通知结果
│   │   │   └── NotificationSmsPool.java    # 通知短信池
│   │   └── suggestion/                     # 建议反馈模块
│   │       ├── Suggestion.java             # 建议实体
│   │       ├── SuggestionController.java   # 建议控制器
│   │       └── SuggestionRepository.java   # 建议数据访问层
│   ├── config.properties                   # 应用配置文件
│   ├── dataSource.properties               # 数据源配置
│   ├── applicationContext-root.xml         # Spring 根配置
│   ├── ehcache.xml                         # EhCache 配置
│   ├── log4j.properties                    # 日志配置
│   └── store.properties                    # 存储配置
├── WebRoot/                                # Web 根目录
│   ├── WEB-INF/                            # Web 配置目录
│   │   ├── web.xml                         # Web 应用配置
│   │   ├── mvc-servlet.xml                 # Spring MVC 配置
│   │   └── lib/                            # 第三方库
│   ├── api/                                # API 文档
│   │   ├── api.html                        # API 接口文档
│   │   └── sms-android.html                # Android 客户端文档
│   ├── images/                             # 图片资源
│   ├── index.jsp                           # 首页
│   ├── suggestion.jsp                      # 建议页面
│   ├── about.jsp                           # 关于页面
│   └── test.html                           # 测试页面
├── sql/                                    # SQL 脚本
│   └── sql.sql                             # 数据库建表脚本
├── doc/                                    # 文档目录
└── .gitignore                              # Git 忽略配置
```

## 主要功能

### 1. 用户认证与授权
- **用户登录**: 基于用户名、密码和设备 IMEI 的登录验证
- **AccessToken 机制**: 使用 UUID 生成访问令牌，支持令牌刷新和备份令牌机制
- **设备绑定**: 防止同一账号在多台设备同时登录
- **用户注册**: 支持手机号码注册，包含验证码验证
- **图形验证码**: 动态生成验证码图片，存储在缓存中
- **用户登出**: 清除 AccessToken 和缓存信息
- **自定义拦截器**: 通过 `@NeedLogin` 注解实现细粒度的登录控制

### 2. 短信推送服务
- **消息推送**: 获取待推送的短信消息
- **消息查询**: 按日期、分组等条件查询短信
- **消息试用**: 无需登录的试用消息接口
- **分组管理**: 支持短信分类和分组推送
- **回复功能**: 支持对短信进行回复和查看回复列表
- **推送状态管理**: 开启/关闭特定分组的推送

### 3. iOS APNs 推送
- **iOS 设备管理**: 注册和更新 DeviceToken
- **定时推送**: 通过 Servlet 实现定时推送任务
- **推送配置**: 支持生产环境和测试环境切换
- **证书管理**: P12 证书加密推送
- **推送队列**: 消息推送池管理

### 4. 系统配置
- **客户端配置**: 返回客户端所需的配置信息（客服电话、版本号等）
- **版本更新**: 检测客户端版本并提供下载地址
- **缓存管理**: 支持清除分组缓存
- **多数据源**: 支持多个数据库连接

### 5. 建议反馈
- **意见提交**: 用户可提交建议和反馈
- **关于页面**: 应用介绍页面

## 使用方法

### 环境要求
- JDK 1.6 或以上
- Apache Tomcat 6.x 或以上
- Microsoft SQL Server 2005 或以上

### 部署步骤

1. **配置数据库**
   ```bash
   # 修改 src/dataSource.properties 文件，配置数据库连接信息
   jdbc.url=jdbc:sqlserver://your-host:port;DatabaseName=your-database
   jdbc.username=your-username
   jdbc.password=your-password
   ```

2. **初始化数据库**
   ```bash
   # 执行 sql/sql.sql 脚本创建数据库表结构
   ```

3. **配置应用参数**
   ```bash
   # 修改 src/config.properties 文件
   # 配置客户服务信息、版本号、iOS 推送证书等
   ```

4. **配置 iOS 推送证书**
   ```bash
   # 将 iOS 推送证书（.p12 文件）放置到指定路径
   # 修改 config.properties 中的证书路径和密码
   certificatePath = certificate.p12
   certificatePassword = your-password
   ```

5. **构建部署**
   ```bash
   # 将项目部署到 Tomcat webapps 目录
   # 启动 Tomcat 服务器
   ```

6. **访问应用**
   ```
   http://localhost:8080/interface-sms/
   ```

### API 接口调用示例

#### 1. 用户登录
```http
POST /user/userLogin.do
Content-Type: application/x-www-form-urlencoded

username=your_username&password=your_password&imei=device_imei&applePush=1
```

响应：
```json
{
  "stat": "1",
  "error": "",
  "data": {
    "login": "1",
    "username": "用户姓名",
    "message": "登录成功",
    "accessToken": "generated-access-token"
  }
}
```

#### 2. 获取推送消息
```http
GET /sms/getPushSMS.do?accessToken=your-access-token&ts=timestamp
```

#### 3. 获取配置信息
```http
GET /sms/getConfig.do?accessToken=your-access-token
```

#### 4. 更新 DeviceToken（iOS）
```http
POST /user/updateDeviceToken.do
Content-Type: application/x-www-form-urlencoded

accessToken=your-access-token&deviceToken=ios-device-token&username=your_username
```

## 依赖说明

### Maven 依赖（参考）
由于本项目使用 lib 方式管理依赖，如需转换为 Maven 项目，可参考以下依赖配置：

```xml
<!-- Spring Framework -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>3.2.x</version>
</dependency>

<!-- Jackson JSON -->
<dependency>
    <groupId>org.codehaus.jackson</groupId>
    <artifactId>jackson-mapper-lgpl</artifactId>
    <version>1.8.1</version>
</dependency>

<!-- EhCache -->
<dependency>
    <groupId>net.sf.ehcache</groupId>
    <artifactId>ehcache-core</artifactId>
    <version>2.4.3</version>
</dependency>

<!-- JavaAPNs -->
<dependency>
    <groupId>com.github.fernandospr</groupId>
    <artifactId>javapns-jdk16</artifactId>
    <version>2.2.1</version>
</dependency>

<!-- SQL Server JDBC Driver -->
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>6.x.x</version>
</dependency>
```

## 核心特性

### 1. 自定义拦截器认证机制
- 通过 `UserInterceptor` 实现统一的用户认证
- 使用 `@NeedLogin` 注解灵活控制接口是否需要登录
- 支持方法级和类级的注解配置
- 双层 Token 验证机制（accessToken 和 accessTokenbak）

### 2. 缓存策略
- 使用 EhCache 缓存用户会话信息（smsUserCache）
- 缓存验证码信息（authCodeCache）
- 缓存用户分组信息（userGroups）
- 支持手动清除缓存

### 3. 安全性
- 字符编码过滤器（UTF-8）
- 参数过滤器防止恶意输入
- 设备 IMEI 绑定防止账号盗用
- AccessToken 定期刷新机制

### 4. 异常处理
- 统一的异常处理器（ExceptionHandler）
- 标准化的 JSON 错误响应格式

## 其他相关信息

### 项目背景
本项目是一个企业级短信通知系统的后端接口服务，主要用于为移动客户端提供实时的短信推送功能。系统采用典型的三层架构（Controller - Repository - Database），适合作为 Spring MVC RESTful API 的学习示例。

### 适用场景
- 企业内部消息通知系统
- 移动应用的推送服务后端
- RESTful API 接口设计参考
- Spring MVC 拦截器使用示例
- iOS APNs 推送集成示例

### 扩展建议
1. **升级框架版本**: Spring 3.x 较老，建议升级到 Spring Boot 2.x/3.x
2. **使用 Maven/Gradle**: 替代 lib 方式管理依赖
3. **数据库迁移**: 考虑使用 MySQL/PostgreSQL 替代 SQL Server
4. **安全增强**:
   - 使用 BCrypt 加密密码
   - 实现 JWT Token 认证
   - 添加 HTTPS 支持
5. **监控与日志**: 集成 SLF4J + Logback，添加 APM 监控
6. **容器化部署**: 使用 Docker 容器化部署

### 注意事项
- `dataSource.properties` 中包含敏感信息，请勿提交到代码仓库
- iOS 推送证书 `.p12` 文件需要妥善保管
- 生产环境部署前请修改所有默认密码和密钥
- 建议启用 HTTPS 保护 API 通信

### 开发者
- **作者**: luowei
- **创建时间**: 2014年1月

### 许可证
本项目仅供学习参考使用。

---

**欢迎提交 Issue 和 Pull Request！**
