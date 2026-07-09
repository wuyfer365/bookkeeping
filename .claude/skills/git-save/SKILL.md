---
name: git-save
description: 暂存所有变更、提交并推送到 GitHub
argument-hint: <commit message>
---

当用户调用此技能时，执行以下步骤完成存档与推送：

## 步骤

1. **检查状态**：`git status`，确认有未提交的变更。无变更则告知用户并结束。

2. **暂存**：`git add -A`

3. **提交**：
   ```
   git commit -m "<用户提供的 message>

   Co-Authored-By: Claude <noreply@anthropic.com>"
   ```

4. **推送**：
   ```
   git push origin $(git rev-parse --abbrev-ref HEAD)
   ```

5. **反馈**：报告 commit hash（短格式）、分支名、变更文件数。

## 异常处理

- push 被拒绝（远程更新）→ 先 `git pull --rebase origin <branch>` 再重试 push
- 冲突 → 提示用户手动解决后重新 `/git-save`
- 无变更 → 输出"工作区干净，无需存档"
