package com.yuaicodemother.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.yuaicodemother.ai.enums.CodeGenTypeEnum;
import com.yuaicodemother.constant.AppConstant;
import com.yuaicodemother.exception.BusinessException;
import com.yuaicodemother.exception.ErrorCode;
import com.yuaicodemother.mapper.AppMapper;
import com.yuaicodemother.model.entity.App;
import com.yuaicodemother.model.entity.AppFrontendVersion;
import com.yuaicodemother.service.AppFrontendVersionService;
import com.yuaicodemother.service.AppFrontendVersioningService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@Slf4j
public class AppFrontendVersioningServiceImpl implements AppFrontendVersioningService {

    @Resource
    private AppMapper appMapper;

    @Resource
    private AppFrontendVersionService appFrontendVersionService;

    @Override
    public AppFrontendVersion createVersionFromSavedCode(Long appId, String userPrompt, CodeGenTypeEnum codeGenType, File savedDir) {
        return createVersionRecord(appId, null, null, userPrompt, codeGenType, savedDir, "MANUAL_COMMIT", null);
    }

    @Override
    public AppFrontendVersion commitCurrentVersion(Long appId, String versionTitle, String changeSummary, String userPrompt) {
        App app = validateApp(appId);
        CodeGenTypeEnum codeGenType = getCodeGenType(app);
        File savedDir = new File(buildCurrentOutputPath(appId, codeGenType));
        if (!savedDir.exists() || !savedDir.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "当前应用前端结果不存在，请先生成代码");
        }
        return createVersionRecord(appId, versionTitle, changeSummary, userPrompt, codeGenType, savedDir, "MANUAL_COMMIT", null);
    }

    @Override
    public AppFrontendVersion rollbackToVersion(Long versionId, String rollbackReason) {
        if (versionId == null || versionId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "版本 ID 非法");
        }
        AppFrontendVersion targetVersion = appFrontendVersionService.getById(versionId);
        if (targetVersion == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "目标版本不存在");
        }
        App app = validateApp(targetVersion.getAppId());
        CodeGenTypeEnum codeGenType = getCodeGenType(app);
        File versionDir = new File(targetVersion.getVersionPath());
        if (!versionDir.exists() || !versionDir.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "目标版本目录不存在，无法回滚");
        }
        File currentOutputDir = new File(buildCurrentOutputPath(app.getId(), codeGenType));
        restoreVersionToCurrentOutput(versionDir, currentOutputDir);
        deleteVersionsAfter(app.getId(), targetVersion.getVersionNo(), targetVersion.getId());
        app.setCurrentVersionId(targetVersion.getId());
        app.setUpdateTime(LocalDateTime.now());
        appMapper.update(app);
        return targetVersion;
    }

    private AppFrontendVersion createVersionRecord(Long appId, String versionTitle, String changeSummary,
                                                   String userPrompt, CodeGenTypeEnum codeGenType, File savedDir,
                                                   String sourceType, Long sourceVersionId) {
        if (appId == null || appId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用 ID 非法");
        }
        if (codeGenType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码生成类型不能为空");
        }
        if (savedDir == null || !savedDir.exists() || !savedDir.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "版本源目录不存在");
        }
        App app = appMapper.selectOneById(appId);
        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }

        Integer nextVersionNo = appFrontendVersionService.getNextVersionNo(appId);
        String versionRootPath = buildVersionRootPath(appId);
        String versionDirName = "v" + nextVersionNo;
        File versionDir = prepareVersionWorkspace(savedDir, versionRootPath, versionDirName, userPrompt, codeGenType);
        File metaFile = new File(versionDir, "meta.txt");
        Integer parentVersionNo = findLatestVersionNo(appId);

        AppFrontendVersion versionRecord = AppFrontendVersion.builder()
                .appId(appId)
                .versionNo(nextVersionNo)
                .versionTitle(StrUtil.blankToDefault(versionTitle, buildVersionTitle(nextVersionNo, userPrompt)))
                .changeSummary(StrUtil.blankToDefault(changeSummary, buildChangeSummary(userPrompt)))
                .userPrompt(userPrompt)
                .codeGenType(codeGenType.getValue())
                .sourceType(StrUtil.blankToDefault(sourceType, "MANUAL_COMMIT"))
                .versionPath(versionDir.getAbsolutePath())
                .metaPath(metaFile.getAbsolutePath())
                .parentVersionNo(parentVersionNo)
                .sourceVersionId(sourceVersionId)
                .isStable(0)
                .versionStatus("READY")
                .createdBy(app.getUserId())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        AppFrontendVersion createdVersion = appFrontendVersionService.createVersionRecord(versionRecord);
        app.setCurrentVersionId(createdVersion.getId());
        app.setUpdateTime(LocalDateTime.now());
        appMapper.update(app);
        return createdVersion;
    }

    private App validateApp(Long appId) {
        if (appId == null || appId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用 ID 非法");
        }
        App app = appMapper.selectOneById(appId);
        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }
        return app;
    }

    private CodeGenTypeEnum getCodeGenType(App app) {
        String codeGenTypeStr = app.getCodeGenType();
        CodeGenTypeEnum codeGenType = CodeGenTypeEnum.getEnumByValue(codeGenTypeStr);
        if (codeGenType == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用代码生成类型不合法");
        }
        return codeGenType;
    }

    private String buildVersionRootPath(Long appId) {
        return AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + "frontend_versions" + File.separator + "app_" + appId;
    }

    private String buildCurrentOutputPath(Long appId, CodeGenTypeEnum codeGenType) {
        return AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + codeGenType.getValue() + "_" + appId;
    }

    private void restoreVersionToCurrentOutput(File versionDir, File currentOutputDir) {
        try {
            if (currentOutputDir.exists()) {
                FileUtil.clean(currentOutputDir);
            } else {
                FileUtil.mkdir(currentOutputDir);
            }
            FileUtil.copyContent(versionDir, currentOutputDir, true);
            File metaFile = new File(currentOutputDir, "meta.txt");
            if (metaFile.exists()) {
                FileUtil.del(metaFile);
            }
        } catch (Exception e) {
            log.error("回滚前端版本失败, versionDir={}, currentOutputDir={}", versionDir, currentOutputDir, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "回滚前端版本失败: " + e.getMessage());
        }
    }

    private File prepareVersionWorkspace(File savedDir, String versionRootPath, String versionDirName, String userPrompt,
                                         CodeGenTypeEnum codeGenType) {
        File rootDir = new File(versionRootPath);
        File versionDir = new File(rootDir, versionDirName);
        File metaFile = new File(versionDir, "meta.txt");
        try {
            FileUtil.mkdir(versionDir);
            FileUtil.copyContent(savedDir, versionDir, true);
            String metaText = "versionDir=" + versionDirName + "\n"
                    + "codeGenType=" + codeGenType.getValue() + "\n"
                    + "userPrompt=" + StrUtil.blankToDefault(userPrompt, "") + "\n"
                    + "createdAt=" + LocalDateTime.now() + "\n";
            FileUtil.writeString(metaText, metaFile, StandardCharsets.UTF_8);
            return versionDir;
        } catch (Exception e) {
            log.error("准备前端版本目录失败, versionRootPath={}", versionRootPath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "准备前端版本目录失败: " + e.getMessage());
        }
    }

    private String buildVersionTitle(Integer versionNo, String userPrompt) {
        String prompt = StrUtil.blankToDefault(userPrompt, "前端修改版本");
        String summary = prompt.length() > 24 ? prompt.substring(0, 24) : prompt;
        return "V" + versionNo + " " + summary;
    }

    private String buildChangeSummary(String userPrompt) {
        if (StrUtil.isBlank(userPrompt)) {
            return "前端修改版本生成";
        }
        return userPrompt.length() > 120 ? userPrompt.substring(0, 120) : userPrompt;
    }

    private void deleteVersionsAfter(Long appId, Integer targetVersionNo, Long targetVersionId) {
        var versionsToDelete = appFrontendVersionService.listVersionsAfter(appId, targetVersionNo).stream()
                .filter(version -> !version.getId().equals(targetVersionId))
                .toList();
        for (AppFrontendVersion version : versionsToDelete) {
            deleteVersionWorkspace(version);
        }
        boolean removed = appFrontendVersionService.removeVersionsAfter(appId, targetVersionNo);
        if (!removed) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除回滚目标之后的版本记录失败");
        }
    }

    private void deleteVersionWorkspace(AppFrontendVersion version) {
        if (version == null || StrUtil.isBlank(version.getVersionPath())) {
            return;
        }
        try {
            File versionDir = new File(version.getVersionPath());
            if (versionDir.exists()) {
                FileUtil.del(versionDir);
            }
        } catch (Exception e) {
            log.error("删除回滚后续版本目录失败, versionId={}, versionPath={}", version.getId(), version.getVersionPath(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除回滚后续版本目录失败: " + e.getMessage());
        }
    }

    private Integer findLatestVersionNo(Long appId) {
        return appFrontendVersionService.listByAppId(appId).stream()
                .findFirst()
                .map(AppFrontendVersion::getVersionNo)
                .orElse(null);
    }
}
