package com.yuaicodemother.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 应用前端版本记录 实体类。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("app_frontend_version")
public class AppFrontendVersion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column("appId")
    private Long appId;

    @Column("versionNo")
    private Integer versionNo;

    @Column("versionTitle")
    private String versionTitle;

    @Column("changeSummary")
    private String changeSummary;

    @Column("userPrompt")
    private String userPrompt;

    @Column("codeGenType")
    private String codeGenType;

    @Column("sourceType")
    private String sourceType;

    @Column("versionPath")
    private String versionPath;

    @Column("metaPath")
    private String metaPath;

    @Column("parentVersionNo")
    private Integer parentVersionNo;

    @Column("sourceVersionId")
    private Long sourceVersionId;

    @Column("isStable")
    private Integer isStable;

    @Column("versionStatus")
    private String versionStatus;

    @Column("createdBy")
    private Long createdBy;

    @Column("createTime")
    private LocalDateTime createTime;

    @Column("updateTime")
    private LocalDateTime updateTime;

    @Column(value = "isDelete", isLogicDelete = true)
    private Integer isDelete;
}
