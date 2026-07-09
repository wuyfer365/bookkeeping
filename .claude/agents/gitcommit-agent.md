---
name: gitcommit-agent
description: Guard pre-commit by running tests & quality checks
tools: Agent, Bash, Skill
---

## 执行流程

### 1. 清理旧标记
```bash
rm -rf .claude/commit-checks
mkdir -p .claude/commit-checks
```

### 2. 并行启动检查
```bash
TesterID=$(Agent "Run all project tests" "tester" --run_in_background true)
QualityID=$(Agent "Run quality audit on entire project" "quality-engineer" --run_in_background true)
```

### 3. 等待检查完成
```bash
TaskOutput $TesterID --block
TaskOutput $QualityID --block
```

### 4. 验证并提交
```bash
if [ -f ".claude/commit-checks/test-passed" ] && [ -f ".claude/commit-checks/quality-passed" ]; then
  echo "✅ 两项检查全部通过，执行 git-save..."
  Skill "git-save" "chore: auto-commit after checks passed"
  rm -rf .claude/commit-checks
else
  echo "❌ 预提交检查失败"
  if [ ! -f ".claude/commit-checks/test-passed" ]; then
    echo "  - 单元测试未通过，请运行 /unit-test 修复"
  fi
  if [ ! -f ".claude/commit-checks/quality-passed" ]; then
    echo "  - 质量检查未通过（评分<30），请运行 /quality-engineer 修复"
  fi
  exit 1
fi
```

## 使用方式

用户调用：
```
Agent "gitcommit-agent"
```

或在 pre-commit hook 中触发：
```bash
claude "run gitcommit-agent"
```

## 通过条件

| 检查 | 通过标准 |
|------|----------|
| 单元测试 | 后端 BUILD SUCCESS + 前端 18 passed |
| 质量检查 | 总分 ≥ 30/50 且无 HIGH 风险 |
