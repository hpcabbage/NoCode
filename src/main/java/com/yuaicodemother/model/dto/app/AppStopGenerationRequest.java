package com.yuaicodemother.model.dto.app;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppStopGenerationRequest implements Serializable {
    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 本轮生成 id
     */
    private String generationId;

    private static final long serialVersionUID = 1L;
}
