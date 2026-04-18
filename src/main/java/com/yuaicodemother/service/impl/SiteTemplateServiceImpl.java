package com.yuaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.yuaicodemother.exception.BusinessException;
import com.yuaicodemother.exception.ErrorCode;
import com.yuaicodemother.exception.ThrowUtils;
import com.yuaicodemother.mapper.SiteTemplateMapper;
import com.yuaicodemother.model.dto.template.SiteTemplateAddRequest;
import com.yuaicodemother.model.dto.template.SiteTemplateCreateFromAppRequest;
import com.yuaicodemother.model.dto.template.SiteTemplateQueryRequest;
import com.yuaicodemother.mapper.AppMapper;
import com.yuaicodemother.model.entity.App;
import com.yuaicodemother.model.entity.SiteTemplate;
import com.yuaicodemother.model.entity.User;
import com.yuaicodemother.model.vo.SiteTemplateVO;
import com.yuaicodemother.model.vo.UserVO;
import com.yuaicodemother.service.SiteTemplateService;
import com.yuaicodemother.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SiteTemplateServiceImpl extends ServiceImpl<SiteTemplateMapper, SiteTemplate> implements SiteTemplateService {

    @Resource
    private UserService userService;

    @Resource
    private AppMapper appMapper;

    @Override
    public Long addSiteTemplate(SiteTemplateAddRequest request, User loginUser) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StrUtil.isBlank(request.getName()), ErrorCode.PARAMS_ERROR, "模板名称不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(request.getInitPrompt()), ErrorCode.PARAMS_ERROR, "模板初始化提示词不能为空");

        SiteTemplate siteTemplate = new SiteTemplate();
        BeanUtil.copyProperties(request, siteTemplate);
        siteTemplate.setUserId(loginUser.getId());
        siteTemplate.setUseCount(0);
        siteTemplate.setTemplateSource(StrUtil.isBlank(request.getTemplateSource()) ? "user" : request.getTemplateSource());
        siteTemplate.setCodeGenType(StrUtil.isBlank(request.getCodeGenType()) ? "html" : request.getCodeGenType());
        siteTemplate.setIsPublic(request.getIsPublic() == null ? 0 : request.getIsPublic());
        siteTemplate.setEditTime(LocalDateTime.now());
        boolean result = this.save(siteTemplate);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "创建模板失败");
        return siteTemplate.getId();
    }

    @Override
    public Long createTemplateFromApp(SiteTemplateCreateFromAppRequest request, User loginUser) {
        ThrowUtils.throwIf(request == null || request.getAppId() == null || request.getAppId() <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StrUtil.isBlank(request.getName()), ErrorCode.PARAMS_ERROR, "模板名称不能为空");

        App app = appMapper.selectOneById(request.getAppId());
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限将该应用保存为模板");
        }

        SiteTemplate siteTemplate = SiteTemplate.builder()
                .name(request.getName())
                .description(request.getDescription())
                .cover(request.getCover())
                .category(request.getCategory())
                .initPrompt(app.getInitPrompt())
                .codeGenType(app.getCodeGenType())
                .templateSource("app_derived")
                .sourceAppId(app.getId())
                .userId(loginUser.getId())
                .isPublic(request.getIsPublic() == null ? 0 : request.getIsPublic())
                .useCount(0)
                .editTime(LocalDateTime.now())
                .build();

        boolean result = this.save(siteTemplate);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "保存模板失败");
        return siteTemplate.getId();
    }

    @Override
    public QueryWrapper getQueryWrapper(SiteTemplateQueryRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("id", request.getId())
                .eq("category", request.getCategory())
                .eq("codeGenType", request.getCodeGenType())
                .eq("templateSource", request.getTemplateSource())
                .eq("userId", request.getUserId())
                .eq("isPublic", request.getIsPublic());
        if (StrUtil.isNotBlank(request.getName())) {
            queryWrapper.like("name", request.getName());
        }
        if (StrUtil.isNotBlank(request.getSearchText())) {
            queryWrapper.and(q -> q.like("name", request.getSearchText())
                    .or("description like ?", "%" + request.getSearchText() + "%"), true);
        }
        if (request.getIsPublic() != null && request.getIsPublic() == 1) {
            queryWrapper.orderBy("useCount", false).orderBy("createTime", false);
        } else {
            queryWrapper.orderBy("createTime", false);
        }
        return queryWrapper;
    }

    @Override
    public SiteTemplateVO getSiteTemplateVO(SiteTemplate siteTemplate) {
        if (siteTemplate == null) {
            return null;
        }
        SiteTemplateVO vo = new SiteTemplateVO();
        BeanUtil.copyProperties(siteTemplate, vo);
        if (siteTemplate.getUserId() != null) {
            User user = userService.getById(siteTemplate.getUserId());
            UserVO userVO = userService.getUserVO(user);
            vo.setUser(userVO);
        }
        return vo;
    }

    @Override
    public List<SiteTemplateVO> getSiteTemplateVOList(List<SiteTemplate> templateList) {
        if (CollUtil.isEmpty(templateList)) {
            return new ArrayList<>();
        }
        Set<Long> userIds = templateList.stream().map(SiteTemplate::getUserId).filter(id -> id != null).collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userIds.isEmpty() ? Map.of() : userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        return templateList.stream().map(template -> {
            SiteTemplateVO vo = getSiteTemplateVO(template);
            if (template.getUserId() != null) {
                vo.setUser(userVOMap.get(template.getUserId()));
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<SiteTemplateVO> listSiteTemplateVOByPage(SiteTemplateQueryRequest request, User loginUser) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        long pageNum = request.getPageNum();
        long pageSize = request.getPageSize();
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "每页最多查询 20 个模板");
        QueryWrapper queryWrapper = this.getQueryWrapper(request);
        Page<SiteTemplate> templatePage = this.page(Page.of(pageNum, pageSize), queryWrapper);
        Page<SiteTemplateVO> voPage = new Page<>(pageNum, pageSize, templatePage.getTotalRow());
        voPage.setRecords(this.getSiteTemplateVOList(templatePage.getRecords()));
        return voPage;
    }

    @Override
    public boolean increaseUseCount(Long templateId) {
        SiteTemplate siteTemplate = this.getById(templateId);
        ThrowUtils.throwIf(siteTemplate == null, ErrorCode.NOT_FOUND_ERROR, "模板不存在");
        SiteTemplate updateTemplate = new SiteTemplate();
        updateTemplate.setId(templateId);
        updateTemplate.setUseCount((siteTemplate.getUseCount() == null ? 0 : siteTemplate.getUseCount()) + 1);
        return this.updateById(updateTemplate);
    }
}
