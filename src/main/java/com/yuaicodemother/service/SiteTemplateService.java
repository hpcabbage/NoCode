package com.yuaicodemother.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.yuaicodemother.model.dto.template.SiteTemplateAddRequest;
import com.yuaicodemother.model.dto.template.SiteTemplateCreateFromAppRequest;
import com.yuaicodemother.model.dto.template.SiteTemplateQueryRequest;
import com.yuaicodemother.model.entity.SiteTemplate;
import com.yuaicodemother.model.entity.User;
import com.yuaicodemother.model.vo.SiteTemplateVO;

import java.util.List;

public interface SiteTemplateService extends IService<SiteTemplate> {

    Long addSiteTemplate(SiteTemplateAddRequest request, User loginUser);

    Long createTemplateFromApp(SiteTemplateCreateFromAppRequest request, User loginUser);

    QueryWrapper getQueryWrapper(SiteTemplateQueryRequest request);

    SiteTemplateVO getSiteTemplateVO(SiteTemplate siteTemplate);

    List<SiteTemplateVO> getSiteTemplateVOList(List<SiteTemplate> templateList);

    Page<SiteTemplateVO> listSiteTemplateVOByPage(SiteTemplateQueryRequest request, User loginUser);

    boolean increaseUseCount(Long templateId);
}
