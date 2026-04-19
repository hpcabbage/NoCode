# Site Template 第一阶段可提交边界与提交说明草稿

## 1. 目标

用于在 NoCode 当前 `site_template` 第一阶段收口过程中，明确：

1. 这轮哪些内容建议纳入同一次提交；
2. 哪些内容应视为当前阶段说明性文档；
3. 提交说明应该如何描述，避免把完成度说大或把旧问题混进来。

---

## 2. 建议纳入本轮提交的核心边界

### 2.1 后端能力变更
建议纳入：

- `src/main/java/com/yuaicodemother/controller/SiteTemplateController.java`
- `src/main/java/com/yuaicodemother/service/SiteTemplateService.java`
- `src/main/java/com/yuaicodemother/service/impl/SiteTemplateServiceImpl.java`
- `src/main/java/com/yuaicodemother/mapper/SiteTemplateMapper.java`
- `src/main/java/com/yuaicodemother/model/entity/SiteTemplate.java`
- `src/main/java/com/yuaicodemother/model/dto/template/` 下相关请求对象
- `src/main/java/com/yuaicodemother/model/vo/SiteTemplateVO.java`
- 与模板建站链路直接相关的 `App` / `AppVO` / `AppServiceImpl` 变更

### 2.2 与当前阶段直接相关的文档
建议纳入：

- `docs/README.md`
- `docs/plans/current-work.md`
- `docs/changelogs/2026-04-18.md`
- `docs/specs/site-template-phase1-spec-2026-04-19.md`
- `docs/decisions/ADR-001-site-template-phase1-completion-boundary.md`
- `docs/runbooks/site-template-phase1-minimal-verification.md`

这些文档的作用不是“额外加戏”，而是把当前阶段真实主线、完成边界和验证路径一起收口，避免后续上下文再次漂移。

---

## 3. 当前不建议混入本次提交的内容

### 3.1 与旧 HTML streaming 问题直接相关的临时产物
不建议混入：

- `tmp/code_output/html_*`
- `tmp/code_deploy/`
- `.run.log`

原因：
- 这些属于调试或运行产物；
- 与 `site_template` 第一阶段能力提交边界不一致；
- 混进来会让提交意图变脏。

### 3.2 非当前主线的环境噪音
需要额外确认是否纳入：

- `mvnw`

如果只是权限位、换行或本地环境副作用，建议不要和本轮功能提交混在一起。

### 3.3 旧主线历史文档
以下内容可以保留在仓库中，但不应在本次提交说明里当成主角：

- `docs/changelogs/2026-04-13.md`
- `docs/specs/html-streaming-save-investigation-2026-04-13.md`

原因：
- 它们是历史排查背景，不是这轮 `site_template` 第一阶段收口的核心交付物。

---

## 4. 建议提交口径

### 推荐标题（中文 commit 标题）
可优先使用：

- `收口站点模板第一阶段能力与文档`

如果希望更强调后端闭环，也可以用：

- `完成站点模板第一阶段后端最小闭环`

更稳一点的推荐仍然是第一条，因为它同时覆盖代码与文档收口。

---

## 5. 提交说明草稿

### 版本 A，偏稳妥
本次提交收口 `site_template` 第一阶段当前主线，完成模板能力后端最小闭环，并同步整理文档入口、专题规格、完成边界和最小验证路径。

包含：
- 模板列表、详情、从应用创建模板、编辑模板、管理员公开状态控制等后端能力；
- 当前主线文档入口切换；
- 第一阶段 spec / decision / runbook 补齐。

当前阶段结论：
- `site_template` 第一阶段后端最小闭环已完成；
- 页面级最终人工验收待补。

### 版本 B，偏 PR 描述
## 本次变更
- 补齐 `site_template` 第一阶段模板能力后端闭环；
- 收紧模板列表查询权限与可见性；
- 新增模板编辑接口；
- 将 NoCode 文档主入口切换到当前真实主线；
- 补充第一阶段专题 spec、完成边界 ADR 与最小验证 runbook。

## 当前完成度
- 后端最小闭环：已完成
- 页面级最终人工验收：待补

## 暂不包含
- 模板运营能力深化
- 页面级完整验收结论
- 与旧 HTML streaming 问题相关的后续处理

---

## 6. 提交前检查建议

提交前至少再看一遍：

1. `git diff --name-only` 是否仍聚焦 `site_template` 与当前文档收口；
2. `tmp/`、日志文件、运行产物是否已排除；
3. `mvnw` 是否真有必要纳入；
4. 文档中的完成度表述是否仍遵守 ADR-001；
5. 如要正式提交，最好按 runbook 再过一轮最小验证。

---

## 7. 当前结论

这轮最适合被定义为：

**`site_template` 第一阶段后端最小闭环 + 文档收口提交`**

不要把它包装成：
- 模板系统完整上线
- 页面验收全部完成
- 与更早 HTML streaming 历史问题一起打包解决
