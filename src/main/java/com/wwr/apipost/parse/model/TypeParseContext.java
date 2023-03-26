package com.wwr.apipost.parse.model;

import lombok.Data;

import java.util.List;

/**
 * 类型解析上下文参数
 */
@Data
public class TypeParseContext {

    /**
     * 分组校验
     */
    private List<String> jsr303ValidateGroups;

}
