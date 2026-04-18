package com.yuaicodemother.controller;

import com.mybatisflex.core.paginate.Page;
import com.yuaicodemother.annotation.AuthCheck;
import com.yuaicodemother.common.BaseResponse;
import com.yuaicodemother.common.DeleteRequest;
import com.yuaicodemother.common.ResultUtils;
import com.yuaicodemother.constant.UserConstant;
import com.yuaicodemother.exception.BusinessException;
import com.yuaicodemother.exception.ErrorCode;
import com.yuaicodemother.exception.ThrowUtils;
import com.yuaicodemother.model.dto.template.SiteTemplateAddRequest;
import com.yuaicodemother.model.dto.template.SiteTemplateAdminUpdateRequest;
import com.yuaicodemother.model.dto.template.SiteTemplateCreateFromAppRequest;
import com.yuaicodemother.model.dto.template.SiteTemplateQueryRequest;
import com.yuaicodemother.model.dto.template.SiteTemplateUpdateRequest;
import com.yuaicodemother.model.entity.SiteTemplate;
import com.yuaicodemother.model.entity.User;
import com.yuaicodemother.model.vo.SiteTemplateVO;
import com.yuaicodemother.service.SiteTemplateService;
import com.yuaicodemother.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/template")
public class SiteTemplateController {

    @Resource
    private SiteTemplateService siteTemplateService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<Long> addSiteTemplate(@RequestBody SiteTemplateAddRequest request, HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        return ResultUtils.success(siteTemplateService.addSiteTemplate(request, loginUser));
    }

    @PostMapping("/create/from-app")
    public BaseResponse<Long> createTemplateFromApp(@RequestBody SiteTemplateCreateFromAppRequest request,
                                                    HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        return ResultUtils.success(siteTemplateService.createTemplateFromApp(request, loginUser));
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateSiteTemplate(@RequestBody SiteTemplateUpdateRequest request,
                                                    HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        ThrowUtils.throwIf(request == null || request.getId() == null || request.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(siteTemplateService.updateSiteTemplate(request, loginUser));
    }

    @GetMapping("/get/vo")
    public BaseResponse<SiteTemplateVO> getSiteTemplateVOById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        SiteTemplate template = siteTemplateService.getById(id);
        ThrowUtils.throwIf(template == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(siteTemplateService.getSiteTemplateVO(template));
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SiteTemplateVO>> listSiteTemplateVOByPage(@RequestBody SiteTemplateQueryRequest request,
                                                                       HttpServletRequest httpServletRequest) {
        User loginUser = null;
        try {
            loginUser = userService.getLoginUser(httpServletRequest);
        } catch (Exception ignored) {
        }
        return ResultUtils.success(siteTemplateService.listSiteTemplateVOByPage(request, loginUser));
    }

    @PostMapping("/admin/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSiteTemplateByAdmin(@RequestBody SiteTemplateAdminUpdateRequest request) {
        ThrowUtils.throwIf(request == null || request.getId() == null || request.getId() <= 0, ErrorCode.PARAMS_ERROR);
        SiteTemplate oldTemplate = siteTemplateService.getById(request.getId());
        ThrowUtils.throwIf(oldTemplate == null, ErrorCode.NOT_FOUND_ERROR);
        SiteTemplate updateTemplate = new SiteTemplate();
        updateTemplate.setId(request.getId());
        updateTemplate.setIsPublic(request.getIsPublic());
        return ResultUtils.success(siteTemplateService.updateById(updateTemplate));
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSiteTemplate(@RequestBody DeleteRequest deleteRequest,
                                                    HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        SiteTemplate template = siteTemplateService.getById(deleteRequest.getId());
        ThrowUtils.throwIf(template == null, ErrorCode.NOT_FOUND_ERROR);
        boolean isAdmin = UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole());
        if (!isAdmin && !template.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限删除该模板");
        }
        return ResultUtils.success(siteTemplateService.removeById(deleteRequest.getId()));
    }
}
