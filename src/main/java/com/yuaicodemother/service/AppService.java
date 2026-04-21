package com.yuaicodemother.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.yuaicodemother.ai.enums.CodeGenTypeEnum;
import com.yuaicodemother.common.DeleteRequest;
import com.yuaicodemother.model.dto.app.AppAddRequest;
import com.yuaicodemother.model.dto.app.AppCommitVersionRequest;
import com.yuaicodemother.model.dto.app.AppFrontendVersionQueryRequest;
import com.yuaicodemother.model.dto.app.AppQueryRequest;
import com.yuaicodemother.model.dto.app.AppRollbackVersionRequest;
import com.yuaicodemother.model.dto.app.AppSetVersionStableRequest;
import com.yuaicodemother.model.dto.app.AppStopGenerationRequest;
import com.yuaicodemother.model.dto.app.AppUpdateRequest;
import com.yuaicodemother.model.entity.App;
import com.yuaicodemother.model.entity.User;
import com.mybatisflex.core.paginate.Page;
import com.yuaicodemother.model.vo.AppFrontendVersionDiffVO;
import com.yuaicodemother.model.vo.AppFrontendVersionFileDiffVO;
import com.yuaicodemother.model.vo.AppFrontendVersionVO;
import com.yuaicodemother.model.vo.AppVO;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Flux;

import java.io.Serializable;
import java.util.List;

public interface AppService extends IService<App> {
    Long addApp(AppAddRequest appAddRequest, HttpServletRequest  request);


    void updateApp(AppUpdateRequest appUpdateRequest, HttpServletRequest request);

    Boolean deleteApp(DeleteRequest deleteRequest, HttpServletRequest request);

    AppVO getAppVO(App app);

    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    List<AppVO> getAppVOList(List<App> appList);

    Page<AppVO> listMyAppVOByPage(AppQueryRequest appQueryRequest, HttpServletRequest request);

    Flux<String> chatToGenCode(Long appId ,String userMessage, String generationId, User loginUser);

    boolean stopChatGeneration(AppStopGenerationRequest request, User loginUser);

    AppFrontendVersionVO commitAppVersion(AppCommitVersionRequest request, User loginUser);

    AppFrontendVersionVO rollbackAppVersion(AppRollbackVersionRequest request, User loginUser);

    Page<AppFrontendVersionVO> listAppVersionVOByPage(AppFrontendVersionQueryRequest request, User loginUser);

    AppFrontendVersionVO getAppVersionVOById(Long versionId, User loginUser);

    AppFrontendVersionVO setAppVersionStable(AppSetVersionStableRequest request, User loginUser);

    AppFrontendVersionDiffVO getAppVersionDiff(Long leftVersionId, Long rightVersionId, User loginUser);

    AppFrontendVersionFileDiffVO getAppVersionFileDiff(Long leftVersionId, Long rightVersionId, String filePath, User loginUser);

    String deployApp(Long appId, User loginUser);

    boolean removeById(Serializable id);
}
