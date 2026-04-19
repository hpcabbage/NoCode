# Site Template 第一阶段提交前现场体检清单

## 当前结论

`site_template` 第一阶段已经满足“收口提交，然后切下一轮”的条件。

当前真正需要处理的不是继续补功能，而是把工作区里的内容分成三类：

1. 应纳入本轮提交
2. 可纳入但不是必须
3. 明确不应纳入

---

## 1. 建议纳入本轮提交

### 1.1 功能代码（已在最近三次主线提交中体现）
这部分属于 `site_template` 第一阶段主成果，应视为本轮主提交内容：

- `src/main/java/com/yuaicodemother/controller/SiteTemplateController.java`
- `src/main/java/com/yuaicodemother/service/SiteTemplateService.java`
- `src/main/java/com/yuaicodemother/service/impl/SiteTemplateServiceImpl.java`
- `src/main/java/com/yuaicodemother/mapper/SiteTemplateMapper.java`
- `src/main/java/com/yuaicodemother/model/entity/SiteTemplate.java`
- `src/main/java/com/yuaicodemother/model/dto/template/` 相关文件
- `src/main/java/com/yuaicodemother/model/vo/SiteTemplateVO.java`
- 与模板能力直接相关的 `App` / `AppVO` / `AppServiceImpl` 变更
- `docs/README.md`

说明：
这些文件主体已经通过最近提交进入主线，这里主要是把它们作为当前阶段的正式提交边界再次确认。

### 1.2 当前新增的收口文档
建议与本轮一起提交：

- `docs/DOCS-SYSTEM.md`
- `docs/changelogs/2026-04-13.md`
- `docs/changelogs/2026-04-18.md`
- `docs/decisions/ADR-001-site-template-phase1-completion-boundary.md`
- `docs/plans/current-work.md`
- `docs/plans/site-template-phase1-commit-draft.md`
- `docs/runbooks/site-template-phase1-minimal-verification.md`
- `docs/specs/html-streaming-save-investigation-2026-04-13.md`
- `docs/specs/site-template-phase1-spec-2026-04-19.md`
- `docs/templates/decision-template.md`
- `docs/templates/investigation-template.md`
- `docs/templates/runbook-template.md`

说明：
虽然其中有部分文档属于“文档系统基础设施”或“旧主线历史归档”，但它们现在一起存在于新 docs 体系里，和当前收口动作是同一批上下文。为了避免后续还要再补一次 docs 骨架，我倾向于本轮一起带上。

---

## 2. 可纳入但建议单独判断

### `mvnw`
当前仅发现：
- 文件模式从 `100644` 变成了 `100755`

建议：
- 如果项目约定 `mvnw` 应该可执行，那可以保留；
- 如果这是本地环境顺手改出来的，就不要混进本轮。

更稳的做法：
- 先确认团队仓库是否通常保留可执行位；
- 不确定就先不带。

---

## 3. 明确不应纳入本轮提交

以下内容应在提交前排除：

- `.run.log`
- `tmp/code_deploy/l0CCNc/index.html`
- `tmp/code_output/html_4/index.html`
- `tmp/code_output/html_5/index.html`
- `tmp/code_output/html_6/index.html`
- `tmp/code_output/html_7/index.html`
- `tmp/code_output/html_8/index.html`

原因：
- 都是运行产物 / 调试现场文件；
- 与 `site_template` 第一阶段正式交付边界无关；
- 混入会让下一轮 git 很脏。

---

## 4. 建议提交标题

优先推荐：

- `收口站点模板第一阶段能力与文档`

备选：

- `完成站点模板第一阶段后端最小闭环`

如果这次你准备把文档系统骨架也一起带上，第一条更合适。

---

## 5. 建议提交说明

可直接参考：

本次提交收口 `site_template` 第一阶段当前主线，完成模板能力后端最小闭环，并同步整理文档入口、专题规格、完成边界和最小验证路径。

包含：
- 模板列表、详情、从应用创建模板、编辑模板、管理员公开状态控制等后端能力；
- 当前主线文档入口切换；
- 第一阶段 spec / decision / runbook / 提交草稿补齐。

当前阶段结论：
- `site_template` 第一阶段后端最小闭环已完成；
- 页面级最终人工验收待补。

---

## 6. 最后建议动作

如果你的目标是“现在就切下一轮 git”，那顺序建议是：

1. 把 `tmp/` 和 `.run.log` 这类运行产物排除；
2. 确认 `mvnw` 要不要保留可执行位；
3. 将 docs 收口文件与当前主线功能一起提交；
4. 用上面的推荐标题提交；
5. 提交后立即切下一轮，不再继续在这一轮叠改。

---

## 7. 一句话判断

**`site_template` 第一阶段已经可以收口提交，当前阻碍切下一轮的不是功能未完成，而是工作区还没清干净。**
