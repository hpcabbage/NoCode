package com.yuaicodemother.service;

import com.yuaicodemother.ai.enums.CodeGenTypeEnum;
import com.yuaicodemother.model.entity.AppFrontendVersion;

import java.io.File;

public interface AppFrontendVersioningService {

    AppFrontendVersion createVersionFromSavedCode(Long appId, String userPrompt, CodeGenTypeEnum codeGenType, File savedDir);

    AppFrontendVersion commitCurrentVersion(Long appId, String versionTitle, String changeSummary, String userPrompt);

    AppFrontendVersion rollbackToVersion(Long versionId, String rollbackReason);
}
