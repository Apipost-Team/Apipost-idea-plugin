package com.wwr.apipost.parse.model;

import com.intellij.openapi.module.Module;
import com.wwr.apipost.config.domain.Api;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author change
 * @date 2023-06-29 20:42
 */
@Data
public class ApiModule {
    /**
     * 接口列表
     */
    private List<Api> apis;
    /**
     * 模块
     */
    private Module module;

    public ApiModule(List<Api> apis, Module module) {
        this.apis = apis;
        this.module = module;
    }

    public ApiModule() {
        this.apis = new ArrayList<>();
    }
}
