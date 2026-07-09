---
name: run-app
description: 启动记账APP前后端开发服务器（后端 :8080，前端 :5173）
argument-hint: [--backend-only | --frontend-only | --stop]
---

## 步骤

### 1. 解析参数

- `--backend-only`：仅启动后端
- `--frontend-only`：仅启动前端
- `--stop`：停止所有已启动的服务
- 无参数：同时启动前后端

### 2. 启动服务

#### 后端 `http://localhost:8080`

```bash
cd backend && java -Dmaven.multiModuleProjectDirectory="$PWD" -classpath ".mvn/wrapper/maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain spring-boot:run 2>&1
```

- 首次启动需下载依赖，约 15~30 秒
- 看到 `Started BookkeepingApplication` 即就绪
- 运行在**后台**（`run_in_background: true`）

#### 前端 `http://localhost:5173`

```bash
cd frontend && npm run dev 2>&1
```

- 看到 `Local: http://localhost:5173/` 即就绪
- 运行在**后台**（`run_in_background: true`）
- Vite 自动热更新

### 3. 反馈

启动后报告：

| 服务 | URL | 状态 |
|------|-----|------|
| 后端 | http://localhost:8080 | 🟢 运行中 |
| 前端 | http://localhost:5173 | 🟢 运行中 |
| API 代理 | /api → :8080 | Vite 自动代理 |

### 4. 验证

后端就绪后，执行快速冒烟测试：
```bash
curl -s http://localhost:8080/api/statistics/summary?startDate=2026-01-01\&endDate=2026-12-31
```
返回 `{"code":200,...}` 即正常。

## 停止服务

`/run-app --stop` 时执行：
```bash
# 停止后端（kill Java 进程）
taskkill //F //IM java.exe 2>/dev/null
# 停止前端（kill Node 进程）  
taskkill //F //IM node.exe 2>/dev/null
```

## 异常处理

- **端口冲突**：提示用户 "端口 8080/5173 已被占用，请先关闭占用进程或使用 --stop"
- **maven-wrapper.jar 损坏**：重新下载（同 tester 技能）
- **首次启动慢**：Maven 下载依赖可能需要几分钟，提醒用户耐心等待
