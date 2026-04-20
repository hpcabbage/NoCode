package com.yuaicodemother.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppFrontendVersionFileDiffVO implements Serializable {

    private Long leftVersionId;

    private Long rightVersionId;

    private String filePath;

    private String leftContent;

    private String rightContent;

    private static final long serialVersionUID = 1L;
}
