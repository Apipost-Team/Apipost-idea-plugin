package com.wwr.apipost.parse.model;

import com.google.common.collect.Lists;
import com.intellij.openapi.module.Module;
import com.wwr.apipost.config.domain.Api;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 接口解析结果
 */
@Data
public class ClassApiData {

    /**
     * 有效的类
     */
    private boolean valid = true;

    /**
     * 声明的分类名称
     */
    private String declaredCategory;

    private List<MethodApiData> methodDataList;
    /**
     * 模块
     */
    private Module module;

    public List<Api> getApis() {
        if (methodDataList == null || methodDataList.isEmpty()) {
            return Collections.emptyList();
        }
        List<Api> apis = Lists.newArrayList();
        for (MethodApiData methodApiInfo : methodDataList) {
            apis.addAll(methodApiInfo.getApis());
        }
        return apis;
    }
}
