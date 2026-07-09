---
name: tester
description: 运行后端 Maven 测试和前端测试，汇总报告结果
argument-hint: [--backend-only | --frontend-only]
---

当用户调用此技能时，按以下步骤执行：

## 步骤

### 1. 解析参数

- `--backend-only`：仅运行后端测试
- `--frontend-only`：仅运行前端测试
- 无参数：同时运行前后端测试

### 2. 运行测试

#### 后端测试

```bash
cd backend && java -Dmaven.multiModuleProjectDirectory="$PWD" -classpath ".mvn/wrapper/maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain test 2>&1
```

判断标准：
- 退出码为 0 且输出含 `BUILD SUCCESS` → 通过
- 其他 → 失败

#### 前端测试

先检查 `frontend/package.json` 中是否有 `test` 脚本：
```bash
cd frontend && node -e "const p=require('./package.json'); if(p.scripts?.test) process.exit(0); else process.exit(1)"
```

- 有 test 脚本 → `npx vitest run`
- 无 test 脚本 → 输出"前端未配置测试脚本（package.json 中无 test 命令）"，跳过

### 3. 汇总报告

用表格列出：

| 测试端 | 状态 | 详情 |
|--------|------|------|
| 后端   | ✅ 通过 / ❌ 失败 / ⏭️ 跳过 | 测试数 X, 通过 X, 失败 X, 耗时 Xs |
| 前端   | ✅ 通过 / ❌ 失败 / ⏭️ 跳过 | 有输出时同理 |

若全部通过则强调"🟢 全部测试通过"；若有失败则用"🔴 N 项测试失败"突出显示。

## 异常处理

- Maven wrapper jar 损坏 → 重新下载 `curl -sL "https://repo1.maven.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar" -o ".mvn/wrapper/maven-wrapper.jar"`
- Maven 编译失败 → 报告错误行并建议 `mvnw compile` 单独检查
