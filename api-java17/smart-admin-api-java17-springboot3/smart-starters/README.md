# smart-starters

SmartAdmin 插件化能力包，每个 starter 独立，按需引入。

## 使用方式

在 `sa-parent` 中加入 smart-starters-parent 作为依赖管理来源，或直接在各服务 pom 中声明版本：

```xml
<!-- 引入短信能力 -->
<dependency>
    <groupId>net.lab1024</groupId>
    <artifactId>starter-sms</artifactId>
    <version>1.0.0</version>
</dependency>
```

然后在 `application.yaml` 中打开开关：

```yaml
smart:
  sms:
    enabled: true
    provider: aliyun
    aliyun:
      access-key-id: ${SMS_AK}
      access-key-secret: ${SMS_SK}
      sign-name: SmartAdmin
```

不引入依赖 = 不生效，无任何副作用。

---

## 模块总览

| 模块 | 触发配置 | 核心 Bean | 完成度 |
|------|---------|----------|--------|
| starter-sms | `smart.sms.enabled=true` | `SmsService` | 骨架完成，需填入 SDK 调用 |
| starter-mq | `smart.mq.enabled=true` | `MqSendService` | RabbitMQ/Kafka 已接入 |
| starter-saas-tenant | `smart.tenant.enabled=true` | `TenantContextService` | column 隔离完整实现 |
| starter-pay | `smart.pay.enabled=true` | `wechatPayService` / `alipayService` | 骨架完成，需填入 SDK 调用 |
| starter-oauth | `smart.oauth.enabled=true` | `OauthService` | URL 生成完成，getUserInfo 需实现 |
| starter-workflow | `smart.workflow.enabled=true` | `WorkflowService` | Flowable 完整实现 |
| starter-search | `smart.search.enabled=true` | `SearchService` | ES 8.x 完整实现 |

---

## 目录结构

```
smart-starters/
├── pom.xml                   # 父 POM（版本管理 + 模块聚合）
├── starter-sms/
│   └── src/main/java/net/lab1024/sa/starter/sms/
│       ├── SmsAutoConfiguration.java
│       ├── SmsProperties.java
│       └── service/
│           ├── SmsService.java          # 统一接口
│           └── impl/
│               ├── AliyunSmsServiceImpl.java
│               └── TencentSmsServiceImpl.java
├── starter-mq/
│   └── .../mq/
│       ├── MqAutoConfiguration.java
│       ├── MqProperties.java
│       └── service/
│           ├── MqSendService.java
│           └── impl/
│               ├── RabbitMqSendServiceImpl.java
│               └── KafkaMqSendServiceImpl.java
├── starter-saas-tenant/
│   └── .../tenant/
│       ├── TenantAutoConfiguration.java
│       ├── TenantProperties.java
│       ├── interceptor/TenantInterceptor.java
│       ├── mybatis/TenantLineHandlerImpl.java
│       └── service/
│           ├── TenantContextService.java
│           └── impl/TenantContextServiceImpl.java
├── starter-pay/
│   └── .../pay/
│       ├── PayAutoConfiguration.java
│       ├── PayProperties.java
│       ├── domain/{PayOrderRequest,PayOrderResult,PayRefundRequest,PayRefundResult}.java
│       └── service/
│           ├── PayService.java
│           └── impl/{WechatPayServiceImpl,AlipayServiceImpl}.java
├── starter-oauth/
│   └── .../oauth/
│       ├── OauthAutoConfiguration.java
│       ├── OauthProperties.java
│       ├── domain/OauthUserInfo.java
│       └── service/
│           ├── OauthService.java
│           └── impl/DefaultOauthServiceImpl.java
├── starter-workflow/
│   └── .../workflow/
│       ├── WorkflowAutoConfiguration.java
│       ├── WorkflowProperties.java
│       └── service/
│           ├── WorkflowService.java
│           └── impl/FlowableWorkflowServiceImpl.java
└── starter-search/
    └── .../search/
        ├── SearchAutoConfiguration.java
        ├── SearchProperties.java
        └── service/
            ├── SearchService.java
            └── impl/ElasticsearchSearchServiceImpl.java
```

---

## 开发新 Starter 步骤

1. 在父 POM 的 `<modules>` 加入新模块
2. 新建子目录，创建 `pom.xml`（继承 smart-starters-parent）
3. 写 `XxxProperties.java`（`@ConfigurationProperties`）
4. 写核心接口 `XxxService.java`
5. 写实现类 `XxxServiceImpl.java`
6. 写 `XxxAutoConfiguration.java`（`@ConditionalOnProperty` + `@ConditionalOnMissingBean`）
7. 在 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册

---

## 与 sa-parent 关系

smart-starters 是**独立 Maven 项目**，不依赖 sa-parent，可发布为私有 Maven 包供多个项目共用。
引用方在自己的 pom 中加入 `<dependency>` 即可，无需源码。
