package com.yuaicodemother.model.dto.app;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppCommitVersionRequest implements Serializable {

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 用户确认提交时填写的版本标题，可选
     */
    private String versionTitle;

    /**
     * 用户确认提交时填写的版本摘要，可选
     */
    private String changeSummary;

    /**
     * 触发本次提交的说明，可选
     */
    private String userPrompt;

    private static final long serialVersionUID = 1L;
}
