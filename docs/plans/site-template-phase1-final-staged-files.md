# Site Template 第一阶段最终待提交文件清单

## 结论

如果目标是：
**现在把 `site_template` 第一阶段收口，然后直接切下一轮 git**，
那么本轮建议按下面清单处理。

---

## 1. 建议直接纳入提交的文件

### 1.1 已修改文件

- `docs/changelogs/2026-04-18.md`
- `docs/plans/current-work.md`

### 1.2 建议新增并纳入提交的文件

- `docs/DOCS-SYSTEM.md`
- `docs/changelogs/2026-04-13.md`
- `docs/decisions/ADR-001-site-template-phase1-completion-boundary.md`
- `docs/plans/site-template-phase1-commit-draft.md`
- `docs/plans/site-template-phase1-staging-checklist.md`
- `docs/runbooks/site-template-phase1-minimal-verification.md`
- `docs/specs/html-streaming-save-investigation-2026-04-13.md`
- `docs/specs/site-template-phase1-spec-2026-04-19.md`
- `docs/templates/decision-template.md`
- `docs/templates/investigation-template.md`
- `docs/templates/runbook-template.md`

说明：
这些文件组成了当前 docs 体系和 `site_template` 第一阶段收口包，可以一起带上，避免下一轮还要回头补文档骨架。

---

## 2. 建议不要纳入提交的文件

### 2.1 运行产物 / 调试文件

- `.run.log`
- `tmp/code_deploy/l0CCNc/index.html`
- `tmp/code_output/html_4/index.html`
- `tmp/code_output/html_5/index.html`
- `tmp/code_output/html_6/index.html`
- `tmp/code_output/html_7/index.html`
- `tmp/code_output/html_8/index.html`

这些文件不属于 `site_template` 第一阶段正式交付内容。

### 2.2 暂缓判断文件

- `mvnw`

当前只发现执行权限变化：
- `100644 -> 100755`

建议：
- 如果你们仓库本来就要求 `mvnw` 可执行，就带上；
- 如果不确定，先不要带，避免把环境噪音混进功能提交。

---

## 3. 可直接参考的 git add 名单

如果本轮只收 docs 与当前收口内容，可按这个思路准备：

```bash
git add \
  docs/DOCS-SYSTEM.md \
  docs/changelogs/2026-04-13.md \
  docs/changelogs/2026-04-18.md \
  docs/decisions/ADR-001-site-template-phase1-completion-boundary.md \
  docs/plans/current-work.md \
  docs/plans/site-template-phase1-commit-draft.md \
  docs/plans/site-template-phase1-staging-checklist.md \
  docs/runbooks/site-template-phase1-minimal-verification.md \
  docs/specs/html-streaming-save-investigation-2026-04-13.md \
  docs/specs/site-template-phase1-spec-2026-04-19.md \
  docs/templates/decision-template.md \
  docs/templates/investigation-template.md \
  docs/templates/runbook-template.md
```

如果还要把 `mvnw` 一起带上，再额外加：

```bash
git add mvnw
```

---

## 4. 提交标题建议

优先推荐：

- `收口站点模板第一阶段能力与文档`

如果你只想强调第一阶段闭环，也可以用：

- `完成站点模板第一阶段后端最小闭环`

---

## 5. 提交前最后检查

提交前只看 3 件事：

1. `git status` 里是否还残留 `.run.log` 和 `tmp/` 产物；
2. `mvnw` 是否要保留可执行位；
3. 最终 staged 文件是否都属于当前 docs 收口和 `site_template` 第一阶段。

---

## 6. 一句话执行建议

**先排除 `.run.log` 和 `tmp/`，确认 `mvnw`，然后按上面清单提交，提交后直接切下一轮。**
