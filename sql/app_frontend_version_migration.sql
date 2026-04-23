-- 前端版本记录能力增量迁移 SQL
-- 用途：把已有数据库升级到当前代码所需的版本模型
-- 范围：app_frontend_version + app.currentVersionId

-- 1) 若历史环境尚未建表，先执行基线建表（见 app_frontend_version.sql）
-- 2) 若历史环境已存在 app_frontend_version，但字段不完整，再执行下面增量 ALTER

ALTER TABLE `app_frontend_version`
  ADD COLUMN `sourceVersionId` bigint DEFAULT NULL COMMENT '来源版本 id，主要用于表达回滚来源' AFTER `parentVersionNo`,
  ADD COLUMN `isStable` tinyint NOT NULL DEFAULT 0 COMMENT '是否稳定版本:0-否 1-是' AFTER `sourceVersionId`;

ALTER TABLE `app_frontend_version`
  ADD KEY `idx_sourceVersionId` (`sourceVersionId`);

ALTER TABLE `app`
  ADD COLUMN `currentVersionId` bigint DEFAULT NULL COMMENT '当前使用中的前端版本 id' AFTER `codeGenType`;

-- 可选：为已有回滚数据回填来源关系时，再按实际版本链补 sourceVersionId
-- 可选：若历史上已经有“当前版本”概念，可按业务规则回填 app.currentVersionId
