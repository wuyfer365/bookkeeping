---
name: unit-test
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

### 4. Guard file

如果全部测试通过，生成提交标记文件：
```bash
mkdir -p .claude/commit-checks
echo "PASSED at $(date)" > .claude/commit-checks/test-passed
```

如果任一测试失败，删除标记文件：
```bash
rm -f .claude/commit-checks/test-passed
```

## 异常处理

- Maven wrapper jar 损坏 → 重新下载 `curl -sL "https://repo1.maven.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar" -o ".mvn/wrapper/maven-wrapper.jar"`
- Maven 编译失败 → 报告错误行并建议 `mvnw compile` 单独检查
