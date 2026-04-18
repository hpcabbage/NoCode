package com.yuaicodemother.model.dto.app;

import lombok.Data;

@Data
public class AppAddRequest {
    /**
     * 应用初始化的 prompt
     */
    private String initPrompt;

    /**
     * 来源模板id
     */
    private Long templateId;

    /**
     * 用户补充提示词
     */
    private String customPrompt;

    private static final long serialVersionUID = 1L;
}
