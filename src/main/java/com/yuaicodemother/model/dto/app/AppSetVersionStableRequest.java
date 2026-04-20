package com.yuaicodemother.model.dto.app;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppSetVersionStableRequest implements Serializable {

    /**
     * 版本 id
     */
    private Long versionId;

    /**
     * 是否标记为稳定版本
     */
    private Integer isStable;

    private static final long serialVersionUID = 1L;
}
