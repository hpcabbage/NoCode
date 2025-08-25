package com.yuaicodemother.controller;

import com.mybatisflex.core.paginate.Page;
import com.yuaicodemother.common.BaseResponse;
import com.yuaicodemother.common.ResultUtils;
import com.yuaicodemother.model.dto.app.AppAddRequest;
import com.yuaicodemother.model.dto.app.AppUpdateRequest;
import com.yuaicodemother.service.AppService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.yuaicodemother.model.entity.App;
import org.springframework.web.bind.annotation.RestController;
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

}
