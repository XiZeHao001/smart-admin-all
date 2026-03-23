# SmartAdmin 项目知识库

> 持续更新，记录项目分析、技术问答等内容。

---

## 一、项目概述

SmartAdmin 是一套**企业级后台管理系统基础框架**，定位是"开箱即用的中后台脚手架"。项目分两层：

- **sa-base**：纯基础设施层，不含业务逻辑，所有横切能力都在这里
- **sa-admin**：在 sa-base 之上构建的具体业务应用

---

## 二、完整技术栈

**核心框架**
- Java 17 + Spring Boot 3.5.4 + Spring Security
- MyBatis Plus 3.5.12 + Druid 连接池
- 双数据库支持：PostgreSQL（主）+ MySQL（可选）
- 多数据源：Dynamic DataSource（支持主从/分库）

**认证与权限**
- SA-Token 1.44.0 — 登录认证、权限控制
- 三层权限体系：数据权限 + 字段权限 + 表权限

**缓存**
- Redis 7 + Redisson（分布式锁、限流）
- Caffeine（本地二级缓存）

**实时通信**
- WebSocket + SSE 双通道（站内消息推送）

**文件处理**
- FastExcel + Apache POI — Excel 导入导出
- Apache Tika — 文件内容提取解析
- 存储：本地 / AWS S3 / 阿里云 OSS

**AI 能力**
- Spring AI 1.0.0-M4
- 支持 OpenAI、Azure OpenAI、Ollama（本地模型）
- 向量存储：PGVector（PostgreSQL）+ Redis

**安全加密**
- BouncyCastle — AES、SM2/SM4 国密算法
- API 请求/响应加密、XSS/SQL注入防护

**监控运维**
- Prometheus + Grafana + Loki + Promtail
- Micrometer 指标采集、P6Spy SQL 监控

**其他**
- Knife4j（Swagger 增强文档）
- Velocity + FreeMarker（代码生成模板）
- IP2Region（IP 归属地）
- Log4j2 日志

---

## 三、适合做什么业务

| 业务场景 | 原因 |
|---------|------|
| 企业内部管理系统 | 部门/员工/角色/菜单权限体系完整 |
| OA 办公系统 | 已内置公告、发票、企业信息等 OA 模块 |
| 电商后台管理 | 商品分类、商品管理、Excel 导入导出已有 |
| 数据中台/BI 后台 | 多数据源、数据权限、字段脱敏支持好 |
| AI 应用后台 | Spring AI 已集成，向量存储开箱即用 |
| 政务/金融系统 | 国密 SM2/SM4 加密、完整审计日志 |

---

## 四、缺少的主流技术

| 技术 | 影响 |
|------|------|
| 消息队列（RabbitMQ / Kafka / RocketMQ） | 无法支撑异步解耦、削峰填谷、分布式事件驱动 |
| 搜索引擎（Elasticsearch / OpenSearch） | 全文检索、复杂聚合查询能力弱 |
| 分布式微服务（Nacos / Sentinel / Seata） | 当前单体架构，扩展微服务需补充 |
| 短信 SDK（阿里云/腾讯云） | 无短信验证码、通知能力 |
| 第三方登录（微信/钉钉/企业微信） | 无 OAuth 社会化登录 |
| 支付（微信支付/支付宝） | 无支付能力 |
| 分布式任务调度（XXL-Job / PowerJob） | 当前自研 SmartJob，不支持分布式调度 |
| 私有化对象存储（MinIO） | 有 S3/OSS，缺私有化部署方案 |

> 总结：这是一套**单体架构的企业中后台框架**，基础设施完善，适合中小型项目快速落地。高并发、微服务场景下，消息队列和分布式组件是最需要补充的部分。

---

## 五、项目组织与 Monorepo 方案

### 现状盘点

| 项目 | 远程仓库 | 本地路径 |
|------|---------|---------|
| Java17 后端 | gitee.com/oldxi/smart-boot | F:\smart-boot |
| Java8 后端 | gitee.com/oldxi/smart-boot-java8 | 无本地 |
| 前端 JS | gitee.com/oldxi/smart-boot-web-java-script | F:\smart-boot-web-java-script |
| App | gitee.com/oldxi/smart-app | 无本地 |
| 前端 TS | 官方开源仓库 | 未 fork |

### 推荐目录结构

```
smart-admin-all/
├── api-java17/          # Java17 后端
├── api-java8/           # Java8 后端
├── web-js/              # 前端 Vue JS版
├── web-ts/              # 前端 Vue TS版（待fork）
├── app/                 # App端
├── smart-starters/      # 插件化能力包（见第六节）
└── smart-admin-all.code-workspace
```

### 合并步骤（git subtree）

```bash
mkdir F:/smart-admin-all && cd F:/smart-admin-all
git init
git remote add origin https://gitee.com/oldxi/smart-admin-all.git

git remote add java17 https://gitee.com/oldxi/smart-boot.git
git fetch java17
git subtree add --prefix=api-java17 java17 master --squash

git remote add java8 https://gitee.com/oldxi/smart-boot-java8.git
git fetch java8
git subtree add --prefix=api-java8 java8 master --squash

git remote add web-js https://gitee.com/oldxi/smart-boot-web-java-script.git
git fetch web-js
git subtree add --prefix=web-js web-js master --squash

git remote add app https://gitee.com/oldxi/smart-app.git
git fetch app
git subtree add --prefix=app app master --squash

git push -u origin master
```

### IDE 使用方式

- **IDEA**：直接打开 `api-java17/` 或 `api-java8/` 目录，只看后端，不受其他目录干扰
- **VS Code**：打开 `web-js/` 或 `app/` 目录，专注前端
- 两个编辑器同时开，各司其职，这是企业最常见的开发方式
- 不需要从根目录打开，直接打开对应子目录即可

---

## 六、插件化模块设计（Starter 架构）

### 核心思想

把每个通用能力封装成独立的 Spring Boot Starter，哪个项目需要哪个能力就在 pom.xml 里加对应依赖，不引入就完全不生效，零侵入。这样同一套基础框架可以服务不同甲方的不同需求。

### 目录结构（已完成）

```
api-java17/smart-admin-api-java17-springboot3/   ← sa-parent 根目录
├── pom.xml                                       ← sa-parent（聚合 sa-base、sa-admin、smart-starters）
├── sa-base/
├── sa-admin/
└── smart-starters/                               ← 插件化能力包（嵌套模块）
    ├── pom.xml                                   ← smart-starters-parent（继承 sa-parent）
    ├── starter-sms/                              # 短信（阿里云/腾讯云）
    ├── starter-mq/                               # 消息队列（RabbitMQ/Kafka）
    ├── starter-saas-tenant/                      # SaaS 多租户
    ├── starter-pay/                              # 支付（微信/支付宝）
    ├── starter-oauth/                            # 第三方登录（微信/钉钉）
    ├── starter-workflow/                         # 工作流（Flowable）
    └── starter-search/                           # 搜索引擎（Elasticsearch）
```

**为什么用嵌套结构？**
- 目录整洁：7 个 starter 不与 sa-base、sa-admin 混在一起
- 独立管理：smart-starters 有自己的父 POM，starter 专属版本号不污染 sa-parent
- 可拆可合：以后独立发布到 Maven 私服时，直接拿走 smart-starters 目录即可

### 使用方式（在 sa-admin 中引入）

**第一步：sa-admin/pom.xml 加依赖**

```xml
<dependencies>
    <!-- 需要短信就加这一行 -->
    <dependency>
        <groupId>net.lab1024</groupId>
        <artifactId>starter-sms</artifactId>
    </dependency>
</dependencies>
```

**第二步：application.yaml 打开开关**

```yaml
smart:
  sms:
    enabled: true
    provider: aliyun
    aliyun:
      access-key-id: ${SMS_ACCESS_KEY_ID}
      access-key-secret: ${SMS_ACCESS_KEY_SECRET}
      sign-name: SmartAdmin
```

**第三步：业务代码直接注入使用**

```java
@Autowired
private SmsService smsService;

public void sendCode(String phone) {
    smsService.sendVerifyCode(phone, "1234", 5);
}
```

不引入依赖 = 不生效，零侵入。

### Starter 完成度总览

| 模块 | 触发配置 | 完成度 | 说明 |
|------|---------|--------|------|
| starter-sms | `smart.sms.enabled=true` | 骨架完成 | 需填入阿里云/腾讯云 SDK 调用 |
| starter-mq | `smart.mq.enabled=true` | ✅ 完整实现 | RabbitMQ/Kafka 已接入 |
| starter-saas-tenant | `smart.tenant.enabled=true` | ✅ 完整实现 | column 隔离 + MyBatis Plus 拦截器 |
| starter-pay | `smart.pay.enabled=true` | 骨架完成 | 需填入微信支付/支付宝 SDK 调用 |
| starter-oauth | `smart.oauth.enabled=true` | 部分完成 | URL 生成完成，getUserInfo 待实现 |
| starter-workflow | `smart.workflow.enabled=true` | ✅ 完整实现 | Flowable 完整封装 |
| starter-search | `smart.search.enabled=true` | ✅ 完整实现 | Elasticsearch 8.x 完整实现 |

### 开发新 Starter 流程

**1. 在 smart-starters 下创建新模块**

```bash
cd smart-admin-api-java17-springboot3/smart-starters
mkdir starter-xxx
cd starter-xxx
```

**2. 创建 pom.xml**

```xml
<project>
    <parent>
        <groupId>net.lab1024</groupId>
        <artifactId>smart-starters-parent</artifactId>
        <version>3.0.0</version>
    </parent>
    <artifactId>starter-xxx</artifactId>
    <dependencies>
        <!-- 按需引入第三方 SDK，设为 optional -->
    </dependencies>
</project>
```

**3. 写配置类 XxxProperties.java**

```java
@Data
@ConfigurationProperties(prefix = "smart.xxx")
public class XxxProperties {
    private boolean enabled = false;
    // 其他配置项
}
```

**4. 写核心接口 XxxService.java**

```java
public interface XxxService {
    void doSomething();
}
```

**5. 写实现类 XxxServiceImpl.java**

```java
@Slf4j
public class XxxServiceImpl implements XxxService {
    @Override
    public void doSomething() {
        // 实现逻辑
    }
}
```

**6. 写自动配置 XxxAutoConfiguration.java**

```java
@Configuration
@EnableConfigurationProperties(XxxProperties.class)
@ConditionalOnProperty(prefix = "smart.xxx", name = "enabled", havingValue = "true")
public class XxxAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public XxxService xxxService(XxxProperties properties) {
        return new XxxServiceImpl(properties);
    }
}
```

**7. 注册自动配置（Spring Boot 3 方式）**

创建 `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`：

```
net.lab1024.sa.starter.xxx.XxxAutoConfiguration
```

**8. 在 smart-starters/pom.xml 的 `<modules>` 加入**

```xml
<module>starter-xxx</module>
```

**9. 验证（三种方式）**

方式1：在 starter 内写单元测试

```java
@SpringBootTest(classes = XxxTestApplication.class)
class XxxAutoConfigurationTest {
    @Autowired
    private XxxService xxxService;

    @Test
    void contextLoads() {
        assertNotNull(xxxService);
    }
}
```

方式2：在 sa-admin 中引入并启动验证

```xml
<!-- sa-admin/pom.xml -->
<dependency>
    <groupId>net.lab1024</groupId>
    <artifactId>starter-xxx</artifactId>
</dependency>
```

```yaml
# application.yaml
smart:
  xxx:
    enabled: true
```

启动 `AdminApplication`，访问测试接口验证。

方式3：写集成测试 Controller（推荐）

参考 `sa-admin/module/system/support/sms/SmsTestController.java`。

### 验证 Demo（以 starter-sms 为例）

**sa-admin/pom.xml 引入依赖：**

```xml
<dependency>
    <groupId>net.lab1024</groupId>
    <artifactId>starter-sms</artifactId>
</dependency>
```

**application.yaml 配置：**

```yaml
smart:
  sms:
    enabled: true
    provider: aliyun
    aliyun:
      access-key-id: test-key
      access-key-secret: test-secret
      sign-name: SmartAdmin
```

**测试 Controller（已创建）：**

`sa-admin/module/system/support/sms/SmsTestController.java`

```java
@RestController
@RequestMapping("/support/sms/test")
@ConditionalOnBean(SmsService.class)
public class SmsTestController {

    @Autowired
    private SmsService smsService;

    @GetMapping("/send")
    public String testSend() {
        smsService.sendVerifyCode("13800138000", "1234", 5);
        return "短信发送成功";
    }
}
```

**启动验证：**

```bash
# 启动 AdminApplication
# 访问 http://localhost:10010/support/sms/test/send
# 返回提示说明 starter 已装配，但需填入真实 SDK 调用
```

### 包结构规范

```
net.lab1024.sa
├── base/          # 基础框架（sa-base）
├── admin/         # 管理端业务（sa-admin）
│   ├── system/    # 系统模块（部门/员工/权限）
│   └── business/  # 业务模块（按甲方业务划分）
└── starter/       # 各能力插件包（smart-starters）
    ├── sms/
    ├── mq/
    ├── tenant/
    └── ...
```

包结构清晰后，未来拆微服务时每个 `business/` 下的模块可以直接独立成一个服务。

---

## 七、Spring Boot → Spring Cloud 迁移路径

当前单体架构完全够用，按需渐进式迁移：

```
阶段1（现在）：Spring Boot 单体，Docker Compose 部署
阶段2（业务增长）：引入 Nacos 做配置中心，服务还是单体
阶段3（需要拆分）：按业务模块拆成多个 Spring Boot 服务 + Gateway
阶段4（高可用）：加 Sentinel 限流、Seata 分布式事务
```

不需要一步到位，边做业务边拆，风险最低。

---

## 八、部署方案（Docker + Podman 双套）

### 为什么要两套

- Docker：开发环境、个人服务器、大多数云主机
- Podman：无守护进程、rootless 运行，部分企业/政务环境要求无 Docker

### 目录规划

```
api-java17/deploy/
├── docker/
│   ├── middleware.yml       # 中间件（PostgreSQL、Redis）
│   ├── app.yml              # 应用服务
│   └── README.md            # Docker 部署文档
└── podman/
    ├── middleware.yml        # 同结构，podman-compose 兼容格式
    ├── app.yml
    └── README.md            # Podman 部署文档
```

Java8 版本保持同步相同结构，前端同理。

### 关键差异点（Docker vs Podman）

| 项目 | Docker | Podman |
|------|--------|--------|
| 启动命令 | `docker compose up -d` | `podman-compose up -d` |
| 守护进程 | 需要 dockerd | 无守护进程 |
| 权限 | 默认 root | 支持 rootless |
| 镜像兼容 | OCI 标准 | OCI 标准，完全兼容 |

---

## 九、Monorepo 日常 Git 操作

### 推送代码

```bash
# 日常推送（同时推送到 Gitee 和 GitHub）
git push origin master
```

### 在 Monorepo 中修改某个子项目

```bash
# 直接在对应子目录下修改文件，然后正常提交
git add api-java17/xxx
git commit -m "feat: xxx"
git push origin master
```

### 从原子仓库同步最新代码到 Monorepo

```bash
# 同步 java17
git subtree pull --prefix=api-java17 java17 dev-mydev --squash

# 同步 java8
git subtree pull --prefix=api-java8 java8 dev-mydev --squash

# 同步前端 JS
git subtree pull --prefix=web-js web-js dev-mydev --squash

# 同步 App
git subtree pull --prefix=app app dev-mydev --squash

# 同步 web-ts（添加后）
git subtree pull --prefix=web-ts web-ts dev-mydev --squash

# 同步完推送
git push origin master
```

### 把 Monorepo 的修改推回原子仓库（如果还需要维护原仓库）

```bash
git subtree push --prefix=api-java17 java17 dev-mydev
```

### 新成员克隆 Monorepo

```bash
git clone https://gitee.com/oldxi/smart-admin-all.git
# 或
git clone https://github.com/XiZeHao001/smart-admin-all.git
```

克隆后不需要额外操作，所有子项目代码都在对应目录下。

### 添加 web-ts 到 Monorepo（待执行）

```bash
cd /d/smart-admin-all
git remote add web-ts https://github.com/XiZeHao001/smart-admin.git
git fetch web-ts
# 只取 smart-admin-web-typescript 子目录
git subtree add --prefix=web-ts web-ts master --squash
git push origin master
```

> 注意：fork 的仓库里 ts 前端在 `smart-admin-web-typescript/` 子目录下，subtree 合并后需要确认目录层级是否需要调整。

### 远程仓库地址汇总

| 仓库 | Gitee | GitHub |
|------|-------|--------|
| Monorepo 主仓库 | gitee.com/oldxi/smart-admin-all | github.com/XiZeHao001/smart-admin-all |
| Java17 后端 | gitee.com/oldxi/smart-boot | — |
| Java8 后端 | gitee.com/oldxi/smart-boot-java8 | — |
| 前端 JS | gitee.com/oldxi/smart-boot-web-java-script | — |
| App | gitee.com/oldxi/smart-app | — |
| 前端 TS（fork） | — | github.com/XiZeHao001/smart-admin |

---

*最后更新：2026-03-22*
