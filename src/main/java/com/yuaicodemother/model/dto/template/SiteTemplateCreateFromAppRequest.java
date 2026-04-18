package com.yuaicodemother.model.dto.template;

import lombok.Data;

import java.io.Serializable;

@Data
public class SiteTemplateCreateFromAppRequest implements Serializable {

    private Long appId;

    private String name;

    private String description;

    private String cover;

    private String category;

    private Integer isPublic;

    private static final long serialVersionUID = 1L;
}
