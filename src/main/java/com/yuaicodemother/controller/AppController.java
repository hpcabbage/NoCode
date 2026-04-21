package com.yuaicodemother.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.yuaicodemother.annotation.AuthCheck;
import com.yuaicodemother.common.BaseResponse;
import com.yuaicodemother.common.DeleteRequest;
import com.yuaicodemother.common.ResultUtils;
import com.yuaicodemother.constant.AppConstant;
import com.yuaicodemother.constant.UserConstant;
import com.yuaicodemother.exception.BusinessException;
import com.yuaicodemother.exception.ErrorCode;
import com.yuaicodemother.exception.ThrowUtils;
import com.yuaicodemother.model.dto.app.*;
import com.yuaicodemother.model.entity.User;
import com.yuaicodemother.model.vo.AppFrontendVersionDiffVO;
import com.yuaicodemother.model.vo.AppFrontendVersionFileDiffVO;
import com.yuaicodemother.model.vo.AppFrontendVersionVO;
import com.yuaicodemother.model.vo.AppVO;
import com.yuaicodemother.ratelimter.annotation.RateLimit;
import com.yuaicodemother.ratelimter.enums.RateLimitType;
import com.yuaicodemother.model.dto.app.GenerationRuntimeState;
import com.yuaicodemother.model.enums.GenerationPhaseEnum;
import com.yuaicodemother.model.enums.GenerationStatusEnum;
import com.yuaicodemother.service.AppService;
import com.yuaicodemother.service.GenerationRuntimeRegistry;
import com.yuaicodemother.service.ProjectDownloadService;
import com.yuaicodemother.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.yuaicodemother.model.entity.App;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 应用 控制层。
 *
 */
@RestController
@RequestMapping("/app")
public class AppController {
    @Autowired
    private AppService appService;
    @Autowired
    private UserService userService;
    @Resource
    private ProjectDownloadService projectDownloadService;
    @Resource
    private GenerationRuntimeRegistry generationRuntimeRegistry;


    @PostMapping("/add")
    public BaseResponse<Long> addApp(@RequestBody AppAddRequest appAddRequest, HttpServletRequest  request){
        return ResultUtils.success(appService.addApp(appAddRequest, request));
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateApp(@RequestBody AppUpdateRequest appUpdateRequest, HttpServletRequest request) {
        appService.updateApp(appUpdateRequest, request);
        return ResultUtils.success(true);
    }

    @PostMapping("/commit/version")
    public BaseResponse<AppFrontendVersionVO> commitAppVersion(@RequestBody AppCommitVersionRequest appCommitVersionRequest,
                                                               HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(appService.commitAppVersion(appCommitVersionRequest, loginUser));
    }

    @PostMapping("/version/rollback")
    public BaseResponse<AppFrontendVersionVO> rollbackAppVersion(@RequestBody AppRollbackVersionRequest rollbackRequest,
                                                                 HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(appService.rollbackAppVersion(rollbackRequest, loginUser));
    }

    @PostMapping("/version/list/page")
    public BaseResponse<Page<AppFrontendVersionVO>> listAppVersionVOByPage(@RequestBody AppFrontendVersionQueryRequest queryRequest,
                                                                           HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(appService.listAppVersionVOByPage(queryRequest, loginUser));
    }

    @GetMapping("/version/get/vo")
    public BaseResponse<AppFrontendVersionVO> getAppVersionVOById(@RequestParam Long versionId,
                                                                  HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(appService.getAppVersionVOById(versionId, loginUser));
    }

    @PostMapping("/version/stable")
    public BaseResponse<AppFrontendVersionVO> setAppVersionStable(@RequestBody AppSetVersionStableRequest stableRequest,
                                                                  HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(appService.setAppVersionStable(stableRequest, loginUser));
    }

    @GetMapping("/version/diff")
    public BaseResponse<AppFrontendVersionDiffVO> getAppVersionDiff(@RequestParam Long leftVersionId,
                                                                    @RequestParam Long rightVersionId,
                                                                    HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(appService.getAppVersionDiff(leftVersionId, rightVersionId, loginUser));
    }

    @GetMapping("/version/file-diff")
    public BaseResponse<AppFrontendVersionFileDiffVO> getAppVersionFileDiff(@RequestParam Long leftVersionId,
                                                                            @RequestParam Long rightVersionId,
                                                                            @RequestParam String filePath,
                                                                            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(appService.getAppVersionFileDiff(leftVersionId, rightVersionId, filePath, loginUser));
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteApp(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request){
        Boolean result = appService.deleteApp(deleteRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取应用详情
     * @param id
     * @return 应用详情
     */
    @GetMapping("/get/vo")
    public BaseResponse<AppVO> getAppVOById(long id) {
        App app = appService.getById(id);
        AppVO appVO = appService.getAppVO(app);
        return ResultUtils.success(appVO);
    }
    /**
     * 分页获取当前用户创建的应用列表
     *
     * @param appQueryRequest 查询请求
     * @param request         请求
     * @return 应用列表
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<AppVO>> listMyAppVOByPage(@RequestBody AppQueryRequest appQueryRequest, HttpServletRequest request) {
        Page<AppVO> appVOPage = appService.listMyAppVOByPage(appQueryRequest, request);
        return ResultUtils.success(appVOPage);
    }

    /**
     * 分页获取精选应用列表
     *
     * @param appQueryRequest 查询请求
     * @return 精选应用列表
     */
    @PostMapping("/good/list/page/vo")
    public BaseResponse<Page<AppVO>> listGoodAppVOByPage(@RequestBody AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 限制每页最多 20 个
        long pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "每页最多查询 20 个应用");
        long pageNum = appQueryRequest.getPageNum();
        // 只查询精选的应用
        appQueryRequest.setPriority(AppConstant.GOOD_APP_PRIORITY);
        QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);
        // 分页查询
        Page<App> appPage = appService.page(Page.of(pageNum, pageSize), queryWrapper);
        // 数据封装
        Page<AppVO> appVOPage = new Page<>(pageNum, pageSize, appPage.getTotalRow());
        List<AppVO> appVOList = appService.getAppVOList(appPage.getRecords());
        appVOPage.setRecords(appVOList);
        return ResultUtils.success(appVOPage);
    }

    /**
     * 管理员删除应用
     *
     * @param deleteRequest 删除请求
     * @return 删除结果
     */
    @PostMapping("/admin/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteAppByAdmin(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        App oldApp = appService.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = appService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 管理员更新应用
     *
     * @param appAdminUpdateRequest 更新请求
     * @return 更新结果
     */
    @PostMapping("/admin/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateAppByAdmin(@RequestBody AppAdminUpdateRequest appAdminUpdateRequest) {
        if (appAdminUpdateRequest == null || appAdminUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = appAdminUpdateRequest.getId();
        // 判断是否存在
        App oldApp = appService.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        App app = new App();
        BeanUtil.copyProperties(appAdminUpdateRequest, app);
        // 设置编辑时间
        app.setEditTime(LocalDateTime.now());
        boolean result = appService.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 管理员分页获取应用列表
     *
     * @param appQueryRequest 查询请求
     * @return 应用列表
     */
    @PostMapping("/admin/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<AppVO>> listAppVOByPageByAdmin(@RequestBody AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = appQueryRequest.getPageNum();
        long pageSize = appQueryRequest.getPageSize();
        QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);
        Page<App> appPage = appService.page(Page.of(pageNum, pageSize), queryWrapper);
        // 数据封装
        Page<AppVO> appVOPage = new Page<>(pageNum, pageSize, appPage.getTotalRow());
        List<AppVO> appVOList = appService.getAppVOList(appPage.getRecords());
        appVOPage.setRecords(appVOList);
        return ResultUtils.success(appVOPage);
    }

    /**
     * 管理员根据 id 获取应用详情
     *
     * @param id 应用 id
     * @return 应用详情
     */
    @GetMapping("/admin/get/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<AppVO> getAppVOByIdByAdmin(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(appService.getAppVO(app));
    }

    /**
     * 应用聊天生成代码（流式 SSE）
     *
     * @param appId   应用 ID
     * @param message 用户消息
     * @param request 请求对象
     * @return 生成结果流
     */
    @GetMapping(value = "/chat/gen/code", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @RateLimit(limitType = RateLimitType.USER, rate = 5, rateInterval = 60, message = "AI 对话请求过于频繁，请稍后再试")
    public void chatToGenCode(@RequestParam Long appId,
                              @RequestParam String message,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        // 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID无效");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "用户消息不能为空");
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/event-stream;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");

        String generationId = java.util.UUID.randomUUID().toString();
        GenerationRuntimeState runtimeState = GenerationRuntimeState.builder()
                .generationId(generationId)
                .appId(appId)
                .userId(loginUser.getId())
                .status(GenerationStatusEnum.GENERATING)
                .phase(GenerationPhaseEnum.UNDERSTANDING)
                .stopRequested(false)
                .build();
        generationRuntimeRegistry.register(runtimeState);

        Flux<String> contentFlux = appService.chatToGenCode(appId, message, generationId, loginUser);

        try {
            var writer = response.getWriter();
            writer.write("event: generation-started\n");
            writer.write("data: " + JSONUtil.toJsonStr(Map.of(
                    "generationId", generationId,
                    "type", "GENERATION_STARTED",
                    "status", GenerationStatusEnum.GENERATING.name(),
                    "phase", GenerationPhaseEnum.UNDERSTANDING.name()
            )) + "\n\n");
            writer.flush();

            contentFlux.doOnNext(chunk -> {
                        try {
                            Map<String, Object> wrapper = Map.of(
                                    "generationId", generationId,
                                    "type", "CONTENT_CHUNK",
                                    "status", GenerationStatusEnum.GENERATING.name(),
                                    "phase", generationRuntimeRegistry.get(generationId) != null
                                            ? generationRuntimeRegistry.get(generationId).getPhase().name()
                                            : GenerationPhaseEnum.GENERATING.name(),
                                    "d", chunk
                            );
                            String jsonData = JSONUtil.toJsonStr(wrapper);
                            writer.write("data: " + jsonData + "\n\n");
                            writer.flush();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .doOnComplete(() -> {
                        try {
                            boolean stopped = generationRuntimeRegistry.isStopRequested(generationId);
                            writer.write("event: " + (stopped ? "generation-stopped" : "generation-completed") + "\n");
                            writer.write("data: " + JSONUtil.toJsonStr(Map.of(
                                    "generationId", generationId,
                                    "type", stopped ? "GENERATION_STOPPED" : "GENERATION_COMPLETED",
                                    "status", stopped ? GenerationStatusEnum.STOPPED.name() : GenerationStatusEnum.SUCCESS.name(),
                                    "phase", stopped ? GenerationPhaseEnum.GENERATING.name() : GenerationPhaseEnum.COMPLETED.name()
                            )) + "\n\n");
                            writer.write("event: done\n");
                            writer.write("data: {}\n\n");
                            writer.flush();
                            generationRuntimeRegistry.remove(generationId);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .blockLast();
        } catch (Exception e) {
            generationRuntimeRegistry.remove(generationId);
            throw new RuntimeException("SSE 响应写出失败", e);
        }
    }

    @PostMapping("/chat/gen/code/stop")
    public BaseResponse<Boolean> stopChatGeneration(@RequestBody AppStopGenerationRequest stopRequest,
                                                    HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(appService.stopChatGeneration(stopRequest, loginUser));
    }

    /**
     * 应用部署
     *
     * @param appDeployRequest 部署请求
     * @param request          请求
     * @return 部署 URL
     */
    @PostMapping("/deploy")
    public BaseResponse<String> deployApp(@RequestBody AppDeployRequest appDeployRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(appDeployRequest == null, ErrorCode.PARAMS_ERROR);
        Long appId = appDeployRequest.getAppId();
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 调用服务部署应用
        String deployUrl = appService.deployApp(appId, loginUser);
        return ResultUtils.success(deployUrl);
    }

    /**
     * 下载应用代码
     *
     * @param appId    应用ID
     * @param request  请求
     * @param response 响应
     */
    @GetMapping("/download/{appId}")
    public void downloadAppCode(@PathVariable Long appId,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        // 1. 基础校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID无效");
        // 2. 查询应用信息
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 权限校验：只有应用创建者可以下载代码
        User loginUser = userService.getLoginUser(request);
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限下载该应用代码");
        }
        // 4. 构建应用代码目录路径（生成目录，非部署目录）
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        // 5. 检查代码目录是否存在
        File sourceDir = new File(sourceDirPath);
        ThrowUtils.throwIf(!sourceDir.exists() || !sourceDir.isDirectory(),
                ErrorCode.NOT_FOUND_ERROR, "应用代码不存在，请先生成代码");
        // 6. 生成下载文件名（不建议添加中文内容）
        String downloadFileName = String.valueOf(appId);
        // 7. 调用通用下载服务
        projectDownloadService.downloadProject(sourceDirPath, downloadFileName, response);
    }

}
