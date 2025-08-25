package com.yuaicodemother.controller;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.yuaicodemother.common.BaseResponse;
import com.yuaicodemother.common.DeleteRequest;
import com.yuaicodemother.common.ResultUtils;
import com.yuaicodemother.constant.AppConstant;
import com.yuaicodemother.exception.ErrorCode;
import com.yuaicodemother.exception.ThrowUtils;
import com.yuaicodemother.model.dto.app.AppAddRequest;
import com.yuaicodemother.model.dto.app.AppQueryRequest;
import com.yuaicodemother.model.dto.app.AppUpdateRequest;
import com.yuaicodemother.model.vo.AppVO;
import com.yuaicodemother.service.AppService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.yuaicodemother.model.entity.App;

import java.util.List;

/**
 * 应用 控制层。
 *
 */
@RestController
@RequestMapping("/app")
public class AppController {
    @Autowired
    private AppService appService;
    @PostMapping("/add")
    public BaseResponse< Long> add(@RequestBody AppAddRequest appAddRequest, HttpServletRequest  request){
        return ResultUtils.success(appService.addApp(appAddRequest, request));
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateApp(@RequestBody AppUpdateRequest appUpdateRequest, HttpServletRequest request) {
        appService.updateApp(appUpdateRequest, request);
        return ResultUtils.success(true);
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


}
