package com.yuaicodemother.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AppFrontendVersionDiffVO implements Serializable {

    private Long leftVersionId;

    private Long rightVersionId;

    private Integer leftVersionNo;

    private Integer rightVersionNo;

    private List<String> addedFiles;

    private List<String> removedFiles;

    private List<String> changedFiles;

    private static final long serialVersionUID = 1L;
}
