package com.yuaicodemother.controller;

import com.mybatisflex.core.paginate.Page;
import com.yuaicodemother.common.BaseResponse;
import com.yuaicodemother.common.DeleteRequest;
import com.yuaicodemother.common.ResultUtils;
import com.yuaicodemother.model.dto.app.AppAddRequest;
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

}
