package com.yuaicodemother.model.dto.app;

import lombok.Data;

@Data
public class AppAddRequest {
    /**
     * 应用初始化的 prompt
     */
    private String initPrompt;

    private static final long serialVersionUID = 1L;
}
