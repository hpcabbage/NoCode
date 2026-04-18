package com.yuaicodemother.model.dto.template;

import lombok.Data;

import java.io.Serializable;

@Data
public class SiteTemplateAdminUpdateRequest implements Serializable {

    private Long id;

    private Integer isPublic;

    private static final long serialVersionUID = 1L;
}
