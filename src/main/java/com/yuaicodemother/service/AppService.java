package com.yuaicodemother.service;

import com.mybatisflex.core.service.IService;
import com.yuaicodemother.common.DeleteRequest;
import com.yuaicodemother.model.dto.app.AppAddRequest;
import com.yuaicodemother.model.dto.app.AppUpdateRequest;
import com.yuaicodemother.model.entity.App;
import com.yuaicodemother.model.entity.User;
import com.yuaicodemother.model.vo.AppVO;
import jakarta.servlet.http.HttpServletRequest;

public interface AppService extends IService<App> {
    Long addApp(AppAddRequest appAddRequest, HttpServletRequest  request);

    boolean save(App app);

    void updateApp(AppUpdateRequest appUpdateRequest, HttpServletRequest request);

    Boolean deleteApp(DeleteRequest deleteRequest, HttpServletRequest request);

    AppVO getAppVO(App app);
}
