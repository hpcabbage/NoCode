# NoCode 文档系统规则

> 目标：不要再把所有信息堆到一个文档里。
> 这套规则把“历史、计划、决策、运行手册、调查记录”分开，方便持续推进、回看和交接。

---

## 1. 文档分层

推荐目录结构：

```text
docs/
  DOCS-SYSTEM.md                 # 本规则：整个文档系统怎么用
  README.md                      # docs 目录导航
  plans/                         # 计划类：接下来要做什么
  investigations/                # 调查类：问题怎么查、查到了什么
  decisions/                     # 决策类：为什么这样定
  runbooks/                      # 操作手册：怎么启动、怎么验证、怎么恢复
  changelogs/                    # 变更记录：做了什么
  specs/                         # 规格/专题说明：某个主题的完整说明
  templates/                     # 模板：写文档时复用
```

---

## 2. 每类文档的职责

### 2.1 `plans/`
**作用：记录接下来要做什么。**

适合写：
- 当前阶段目标
- 当前阻塞
- 下一步动作
- 验证方式

不适合写：
- 长篇历史过程
- 已经结束的调查细节

建议命名：
- `current-work.md`
- `html-streaming-repair-plan.md`

---

### 2.2 `investigations/`
**作用：记录问题排查过程和证据。**

适合写：
- 现象
- 已验证链路
- 日志证据
- 已证伪假设
- 当前最可信结论

不适合写：
- 最终实施方案的完整规格
- 长期运行手册

建议命名：
- `html-streaming-save-investigation-2026-04-13.md`

---

### 2.3 `decisions/`
**作用：记录已经拍板的技术决策。**

适合写：
- 为什么不用某方案
- 为什么采用某实现
- 取舍依据
- 影响范围

建议使用 ADR（Architecture Decision Record）风格。

建议命名：
- `ADR-001-html-streaming-save-strategy.md`

---

### 2.4 `runbooks/`
**作用：记录可执行的操作手册。**

适合写：
- 如何启动后端
- 如何启动前端
- 如何回归测试
- 如何恢复故障
- 如何检查依赖服务

特点：
- 命令明确
- 步骤线性
- 可直接照着执行

建议命名：
- `backend-local-start.md`
- `html-generation-regression-checklist.md`

---

### 2.5 `changelogs/`
**作用：记录做了什么改动。**

适合写：
- 今天改了哪些文件
- 修了什么问题
- 验证过什么
- 还有什么风险

建议按日期或阶段记录。

建议命名：
- `2026-04-13.md`

---

### 2.6 `specs/`
**作用：记录某个专题的完整规格/问题说明。**

适合写：
- 某个复杂问题的专题说明
- 一个模块的结构化描述
- 兼顾背景、现状、证据、候选方案、下一步

它比 `investigations/` 更完整，
但不等于“全部东西都往 specs 塞”。

适合场景：
- 一类持续几天甚至更久的问题
- 会反复回看的主题

---

## 3. 什么时候更新哪个文档

### 情况 A：今天刚排查了一个问题
更新：
- `investigations/xxx.md`
- 如有阶段变化，再补 `plans/current-work.md`

### 情况 B：今天已经决定采用某个修法
更新：
- `decisions/ADR-xxx.md`
- `plans/current-work.md`

### 情况 C：今天完成了一轮改动
更新：
- `changelogs/YYYY-MM-DD.md`
- 必要时更新 `runbooks/` 或 `plans/`

### 情况 D：形成长期可复用的结论
更新：
- `runbooks/` 或 `specs/`
- 不要只留在日记式 changelog 里

---

## 4. 推荐的最小规则

### Rule 1
**一个文档只承担一种职责。**
- 计划不要混调查
- 调查不要混操作手册
- 决策不要混流水账

### Rule 2
**优先维护一个“当前计划文档”。**
推荐：`docs/plans/current-work.md`

它始终回答 4 个问题：
- 现在在干什么
- 为什么做这件事
- 当前阻塞是什么
- Next Action 是什么

### Rule 3
**每个复杂问题至少要有一份 investigation。**
否则后面很容易重复走弯路。

### Rule 4
**真正拍板的事情要写 decision，不要只写在聊天里。**

### Rule 5
**能复用的操作步骤进入 runbook，不要埋在 investigation。**

---

## 5. 当前建议落地方式（适用于 NoCode）

建议从下面 5 个文件开始：

1. `docs/README.md`
   - docs 导航入口
2. `docs/plans/current-work.md`
   - 当前计划 / Next Action
3. `docs/investigations/html-streaming-save-investigation-2026-04-13.md`
   - 今天的排查记录
4. `docs/runbooks/backend-local-start.md`
   - 后端本地启动说明
5. `docs/changelogs/2026-04-13.md`
   - 今天做过的实际动作

---

## 6. 文档维护原则

- 新问题先建 `investigation`
- 形成结论后补 `decision`
- 要执行的步骤沉淀进 `runbook`
- 当前推进始终收敛到 `plans/current-work.md`
- 每日动作简要写进 `changelog`

---

## 7. 当前文档使用建议

针对这次 HTML 流式不落盘问题：

- 历史调查：放 `investigations/`
- 当前修复推进：放 `plans/current-work.md`
- 最终选型（例如是否关闭 includeUsage、是否换 client）：放 `decisions/`
- 回归验证命令：放 `runbooks/`
- 每天实际改了什么：放 `changelogs/`

---

## 8. 一句话规则

**计划看 `plans/`，历史看 `investigations/`，拍板看 `decisions/`，操作看 `runbooks/`，每天做了什么看 `changelogs/`。**
