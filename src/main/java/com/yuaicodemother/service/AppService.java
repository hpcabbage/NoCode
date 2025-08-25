package com.yuaicodemother.service;

import com.yuaicodemother.model.dto.app.AppAddRequest;
import com.yuaicodemother.model.dto.app.AppUpdateRequest;
import com.yuaicodemother.model.entity.App;
import jakarta.servlet.http.HttpServletRequest;

public interface AppService {
    Long addApp(AppAddRequest appAddRequest, HttpServletRequest  request);

    boolean save(App app);

    void updateApp(AppUpdateRequest appUpdateRequest, HttpServletRequest request);
}
