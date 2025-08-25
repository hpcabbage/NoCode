package com.yuaicodemother.model.dto.app;

import lombok.Data;

@Data
public class AppUpdateRequest {
    /**
     * Id
     */
    private Long Id;

    /**
     * 应用名称
     */
    private  String appName;

    private static final Long serialVersionUID = 1L;
}
