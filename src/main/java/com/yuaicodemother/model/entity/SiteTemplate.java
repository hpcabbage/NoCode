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
 * 网站模板 实体类。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("site_template")
public class SiteTemplate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String name;

    private String description;

    private String cover;

    private String category;

    @Column("initPrompt")
    private String initPrompt;

    @Column("codeGenType")
    private String codeGenType;

    @Column("templateSource")
    private String templateSource;

    @Column("sourceAppId")
    private Long sourceAppId;

    @Column("userId")
    private Long userId;

    @Column("isPublic")
    private Integer isPublic;

    @Column("useCount")
    private Integer useCount;

    @Column("editTime")
    private LocalDateTime editTime;

    @Column("createTime")
    private LocalDateTime createTime;

    @Column("updateTime")
    private LocalDateTime updateTime;

    @Column(value = "isDelete", isLogicDelete = true)
    private Integer isDelete;
}
