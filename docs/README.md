# NoCode Docs

这是 NoCode 项目的内部文档目录。

## 导航

- `DOCS-SYSTEM.md`：文档系统规则，说明每类文档该写什么
- `plans/current-work.md`：当前正在推进的事情、阻塞、Next Action
- `investigations/`：问题排查记录
- `decisions/`：技术决策记录（ADR）
- `runbooks/`：操作手册 / 回归步骤 / 启动手册
- `changelogs/`：每日变更记录
- `specs/`：专题说明 / 长文档
- `templates/`：写文档时复用的模板

## 当前重点文档

- `plans/current-work.md`
- `changelogs/2026-04-13.md`（历史排查留档，保留参考）

## 当前工作面

当前主线已从更早的 “HTML 流式生成有内容但未落盘” 排查，切换到 `site_template` 第一阶段收口：

- 已完成模板后端最小闭环；
- 已完成模板前端最小闭环入口；
- 当前重点是整理文档入口、阶段结论和后续非人工依赖的推进方向；
- 页面级人工最终验收后续再补，不作为当前唯一阻塞。

## 使用约定

1. 排查问题 → 先写 `investigations/`
2. 明确下一步 → 更新 `plans/current-work.md`
3. 拍板方案 → 记录到 `decisions/`
4. 形成稳定操作步骤 → 沉淀到 `runbooks/`
5. 当天做了什么 → 写进 `changelogs/YYYY-MM-DD.md`
6. 如果项目主线切换了，先同步 `plans/current-work.md` 与本 README，避免后续会话走错入口
