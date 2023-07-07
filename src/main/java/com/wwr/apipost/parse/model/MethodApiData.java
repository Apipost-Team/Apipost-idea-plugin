package com.wwr.apipost.parse.model;

import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiMethod;
import com.wwr.apipost.config.domain.Api;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 方法解析数据
 */
@Data
public class MethodApiData {

    /**
     * 是否是有效的接口方法
     */
    private boolean valid = true;

    /**
     * 目标方法
     */
    private PsiMethod method;


    /**
     * 指定的接口名称
     */
    private String declaredApiSummary;
    /**
     * 模块
     */
    private Module module;

    /**
     * 接口列表
     */
    private List<Api> apis;

    public List<Api> getApis() {
        return apis != null ? apis : Collections.emptyList();
    }
}
