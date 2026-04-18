package com.yuaicodemother.model.dto.template;

import com.yuaicodemother.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class SiteTemplateQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private String name;

    private String category;

    private String codeGenType;

    private String templateSource;

    private Long userId;

    private Integer isPublic;

    private String searchText;

    private static final long serialVersionUID = 1L;
}
