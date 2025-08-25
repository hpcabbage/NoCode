package com.yuaicodemother.model.vo;

import java.time.LocalDateTime;

public class AppVO {
    /**
     * id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用封面
     */
    private String cover;

    /**
     * 应用初始化的prompt
     */
    private String initPrompt;

    /**
     * 代码生成类型
     */
    private String codeGenType;

    /**
     * 应用部署标识
     */
    private String deployKey;

    /**
     * 应用部署时间
     */
    private LocalDateTime deployedTime;

    /**
     * 应用优先级
     */
    private Integer priority;

    /**
     * 应用创建者ID
     */
    private Long userId;

    /**
     * 应用创建时间
     */
    private LocalDateTime creatTime;

    /**
     * 应用更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建应用信息
     */
    private UserVO userVo;

    private static final long serialVersionUID = 1L;
}
