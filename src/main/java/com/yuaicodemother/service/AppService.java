package com.yuaicodemother.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.yuaicodemother.common.DeleteRequest;
import com.yuaicodemother.model.dto.app.AppAddRequest;
import com.yuaicodemother.model.dto.app.AppQueryRequest;
import com.yuaicodemother.model.dto.app.AppUpdateRequest;
import com.yuaicodemother.model.entity.App;
import com.yuaicodemother.model.entity.User;
import com.yuaicodemother.model.vo.AppVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AppService extends IService<App> {
    Long addApp(AppAddRequest appAddRequest, HttpServletRequest  request);

    boolean save(App app);

    void updateApp(AppUpdateRequest appUpdateRequest, HttpServletRequest request);

    Boolean deleteApp(DeleteRequest deleteRequest, HttpServletRequest request);

    AppVO getAppVO(App app);

    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    List<AppVO> getAppVOList(List<App> appList);

    Page<AppVO> listMyAppVOByPage(AppQueryRequest appQueryRequest, HttpServletRequest request);
}
