---
name: security-audit
description: 安全审计：检查密码泄露、SQL注入、配置明文、CORS等安全隐患并输出报告
argument-hint: [文件路径 | 目录路径]
---

对指定代码执行多维度安全审计。逐文件阅读后按以下 7 个维度逐一检查。

## 检查维度

### 1. 🔑 硬编码敏感信息

扫描代码中是否出现：
- 密码明文：`password = "xxx"`、`passwd`、`pwd`、`secret`
- API 密钥：`apiKey = "xxx"`、`accessKey`、`secretKey`、`token = "xxx"`
- 数据库连接串中的明文密码：`jdbc:mysql://...password=xxx`
- JWT 密钥硬编码：`secret = "mySecretKey"`
- 私钥/PEM 内容直接写在代码中
- 邮箱密码、第三方服务密码

**不仅检查 Java/JS 文件，也检查 `.properties`、`.yml`、`.env`、`.xml`、`.json`**

### 2. 🏠 SQL 注入风险

检查 MyBatis XML 和 Java 代码中：
- MyBatis `${}`（字符串替换）是否可以用 `#{}`（参数化）替代
- 动态 SQL 拼接：`"SELECT * FROM " + tableName`、`String.format("SELECT ... %s", input)`
- `LIKE`、`IN`、`ORDER BY` 等子句是否对用户输入做了安全处理
- `Statement`（非 `PreparedStatement`）的使用
- MyBatis XML 中直接拼接变量到 SQL 关键字位置（如 `${orderBy}`）

### 3. 📄 配置文件敏感信息泄露

检查 `application.yml`、`application.properties`、`application-*.yml`、`.env`：
- 是否包含明文密码（应使用环境变量 `DB_PASSWORD` 而非 `password: 123456`）
- API 密钥是否直接写在配置文件中
- 是否包含生产环境 IP、内网地址等网络拓扑信息
- 数据库连接串是否暴露了生产凭证

### 4. 🌐 CORS 跨域配置

检查 CORS 配置：
- `allowedOrigins("*")` + `allowCredentials(true)` 同时存在 → 高危（允许任意来源携带凭证）
- `allowedOriginPatterns("*")` 是否在开发环境中过于宽松
- 生产环境是否仍然使用 `allowedOrigins: "*"`

### 5. 🔓 认证/授权风险

- Controller 方法是否缺少鉴权注解（如未加 `@PreAuthorize`）
- 敏感接口（删除、修改）是否仅通过前端路由守卫保护，后端无校验
- Session/Token 管理是否有超时和刷新机制
- 是否存在越权风险：通过修改 URL 的 `/{id}` 访问他人数据时是否校验归属

### 6. 🛡️ 输入校验与 XSS

- Controller 入参是否使用 `@Valid` / `@Validated` 校验
- 金额、日期等关键字段是否有 `@DecimalMin`、`@PastOrPresent` 等约束
- 前端是否对用户输入的 HTML/JS 做了转义（Vue 默认转义 `{{ }}`，但 `v-html` 有风险）
- 全局异常处理器是否暴露了详细的堆栈信息给前端

### 7. ⚙️ 其他安全风险

- **日志泄露**：异常日志中是否 `e.printStackTrace()` 直接输出到响应
- **文件操作**：是否有路径遍历风险（用户输入拼接到文件路径）
- **依赖版本**：检查 `pom.xml` / `package.json` 是否有已知 CVE 的版本
- **Debug 模式**：生产环境的 `spring.debug=true` 或 `logging.level: debug` 是否合理
- **CSRF**：前后端分离架构是否正确禁用了 CSRF 或做了防护

## 报告格式

```
# 🔒 安全审计报告

## 文件：<路径>

### 🔑 硬编码敏感信息（N 处）
| 行号 | 风险 | 代码 | 修复建议 |
|------|------|------|----------|
| L5 | 高 | `password = "admin123"` | 改为 `System.getenv("DB_PASSWORD")` |

### 🏠 SQL 注入风险（N 处）
| 行号 | 风险 | 代码 | 修复建议 |
|------|------|------|----------|
| L12 | 高 | `SELECT * FROM ${tableName}` | 改用 `#{}`，若必须动态表名则在 Java 层做白名单校验 |

### 📄 配置文件泄露（N 处）
| 行号/键 | 风险 | 内容 | 修复建议 |
|---------|------|------|----------|
| `spring.datasource.password` | 高 | `password: mypass123` | 改为 `${DB_PASSWORD}` 环境变量 |

### 🌐 CORS 风险（N 处）
| 行号 | 风险 | 配置 | 修复建议 |
|------|------|------|----------|

### 🔓 认证授权风险（N 处）
| 行号 | 风险 | 说明 | 修复建议 |
|------|------|------|----------|

### 🛡️ 输入校验风险（N 处）
| 行号 | 风险 | 说明 | 修复建议 |
|------|------|------|----------|

### ⚙️ 其他风险（N 处）
| 行号 | 风险 | 说明 | 修复建议 |
|------|------|------|----------|

### 📊 汇总

| 维度 | 高危 | 中危 | 低危 | 信息 |
|------|------|------|------|------|
| 硬编码密钥 | N | N | N | N |
| SQL 注入 | N | N | N | N |
| ... | | | | |

**总体评级：** 🔴 高危 / 🟡 中危 / 🟢 安全

**优先修复：** 列出 Top 3 需要立即修复的问题
```

## 风险等级说明

| 等级 | 说明 |
|------|------|
| 🔴 高危 | 直接可被利用造成数据泄露、权限绕过、代码执行 |
| 🟡 中危 | 在特定条件下可被利用 |
| 🟢 低危 | 不良实践，暂无直接利用方式 |
| ℹ️ 信息 | 值得关注但不构成漏洞 |

## 检查范围

- 用户指定文件 → 仅审计该文件
- 用户指定目录 → 递归审计目录下所有代码及配置文件
- 未指定 → 审计整个 `backend/` + `frontend/` 项目
- 优先检查 `application*.yml`、`pom.xml`、所有 `.java`、`.js`、`.vue`、`.xml`
