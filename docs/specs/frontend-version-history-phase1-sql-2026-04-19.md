# 前端修改版本记录第一阶段表设计与建表 SQL（2026-04-19）

## 1. 当前结论

第一阶段建议**新增独立表**，不要把前端版本历史直接塞进 `app` 表。

原因：
- `app` 适合保存应用最新态；
- 前端版本记录本质上是“一对多”的演化历史；
- 后续版本列表、回滚、预览、差异分析都更适合围绕独立版本表扩展。

因此第一阶段建议新增表：

- `app_frontend_version`

---

## 2. 第一阶段最小字段建议

### 必须保留
- `id`：主键
- `appId`：所属应用 id
- `versionNo`：应用内递增版本号，便于展示“第几版”
- `versionTitle`：版本标题，给列表展示用，例如“AI 修改首页 Hero 区块”
- `userPrompt`：触发本次前端修改的用户输入
- `codeGenType`：前端生成类型（HTML / VUE / MULTI_FILE）
- `versionPath`：本次前端版本文件目录路径
- `versionStatus`：版本状态，例如 `READY` / `FAILED`
- `createdBy`：触发本次版本生成的用户 id
- `createTime`：创建时间
- `updateTime`：更新时间
- `isDelete`：逻辑删除

### 建议保留
- `changeSummary`：版本备注，第一阶段允许为空，由用户提交版本时填写
- `sourceType`：版本来源，例如 `MANUAL_COMMIT` / `ROLLBACK`
- `metaPath`：版本元信息文件路径
- `parentVersionNo`：父版本号

---

## 3. 第一阶段明确不做

第一阶段先不放这些重字段：

- 完整代码正文大字段
- 双版本 diff 数据
- 页面截图 URL
- 回滚状态机
- 真实 Git commit hash / commit message / repo 管理字段

原因：
- 当前目标是先拿到“前端修改版本记录”主链路；
- 若直接把完整代码 blob 存库，表会过重；
- 当前阶段只需要像 Git 一样有版本层级，不需要真的接 Git 提交。

---

## 4. 写入时机建议

第一阶段建议写入时机定义为：

**由用户主动触发一次版本提交后，再把当前前端结果复制到一个新的版本目录，并写入一条版本记录。**

这样能保证：
- 不是用户一发消息就记版本；
- 不是前端生成成功后自动记版本；
- 而是由用户在确认当前结果可用后，手动提交一个版本。

---

## 5. 建表 SQL（MySQL）

```sql
CREATE TABLE `app_frontend_version` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 id',
  `appId` bigint NOT NULL COMMENT '所属应用 id',
  `versionNo` int NOT NULL COMMENT '应用内前端版本号，从 1 递增',
  `versionTitle` varchar(128) DEFAULT NULL COMMENT '版本标题',
  `changeSummary` varchar(512) DEFAULT NULL COMMENT '版本备注',
  `userPrompt` text COMMENT '触发本次前端修改的用户输入',
  `codeGenType` varchar(32) NOT NULL COMMENT '代码生成类型:HTML/VUE/MULTI_FILE',
  `sourceType` varchar(32) NOT NULL DEFAULT 'MANUAL_COMMIT' COMMENT '版本来源:MANUAL_COMMIT/ROLLBACK',
  `versionPath` varchar(512) NOT NULL COMMENT '前端版本文件目录路径',
  `metaPath` varchar(512) DEFAULT NULL COMMENT '版本元信息文件路径',
  `parentVersionNo` int DEFAULT NULL COMMENT '父版本号',
  `versionStatus` varchar(32) NOT NULL DEFAULT 'READY' COMMENT '版本状态:READY/FAILED',
  `createdBy` bigint NOT NULL COMMENT '触发本次版本生成的用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_appId` (`appId`),
  KEY `idx_appId_versionNo` (`appId`, `versionNo`),
  KEY `idx_createdBy` (`createdBy`),
  KEY `idx_createTime` (`createTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用前端修改版本记录表';
```

---

## 6. versionNo 生成建议

建议规则：

```text
同一个 appId 下，versionNo = 当前最大 versionNo + 1
```

例如：
- appId=12，第 1 次成功生成前端版本，`versionNo=1`
- 第 2 次成功生成前端版本，`versionNo=2`

这样后续前端展示最直观。

---

## 7. 第一阶段最小查询能力

第一阶段列表查询最少支持：

- 按 `appId` 查询版本列表
- 按 `versionNo` 倒序

典型查询：

```sql
SELECT *
FROM `app_frontend_version`
WHERE `appId` = ?
  AND `isDelete` = 0
ORDER BY `versionNo` DESC, `id` DESC;
```

---

## 8. 当前推荐结论

如果你现在就要先落数据库，我建议直接先建这张表。

它有几个好处：
- 不会污染 `app` 主表；
- 足够轻，第一阶段容易收口；
- 已经能支撑后面的版本列表；
- 数据库能明确记录“这版前端文件放在哪里”；
- 后续如果要做回滚 / 对比 / 预览，也有扩展空间。
