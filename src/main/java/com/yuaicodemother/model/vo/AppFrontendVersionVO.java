package com.yuaicodemother.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class AppFrontendVersionVO implements Serializable {

    private Long id;

    private Long appId;

    private Integer versionNo;

    private String versionTitle;

    private String changeSummary;

    private String userPrompt;

    private String codeGenType;

    private String sourceType;

    private String versionPath;

    private String metaPath;

    private Integer parentVersionNo;

    private Long sourceVersionId;

    private Integer isStable;

    private Boolean currentVersion;

    private String versionStatus;

    private Map<String, String> metaInfo;

    private Long createdBy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}
