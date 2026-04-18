package com.yuaicodemother.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SiteTemplateVO implements Serializable {

    private Long id;

    private String name;

    private String description;

    private String cover;

    private String category;

    private String initPrompt;

    private String codeGenType;

    private String templateSource;

    private Long sourceAppId;

    private Long userId;

    private Integer isPublic;

    private Integer useCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private UserVO user;

    private static final long serialVersionUID = 1L;
}
