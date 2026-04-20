package com.yuaicodemother.model.dto.app;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppRollbackVersionRequest implements Serializable {

    /**
     * 目标版本 id
     */
    private Long versionId;

    /**
     * 回滚说明，可选
     */
    private String rollbackReason;

    private static final long serialVersionUID = 1L;
}
