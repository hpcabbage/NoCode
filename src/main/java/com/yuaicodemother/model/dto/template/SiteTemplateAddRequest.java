package com.yuaicodemother.model.dto.template;

import lombok.Data;

import java.io.Serializable;

@Data
public class SiteTemplateAddRequest implements Serializable {

    private String name;

    private String description;

    private String cover;

    private String category;

    private String initPrompt;

    private String codeGenType;

    private String templateSource;

    private Long sourceAppId;

    private Integer isPublic;

    private static final long serialVersionUID = 1L;
}
