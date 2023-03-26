package com.wwr.apipost.parse.model;

import com.wwr.apipost.config.domain.HttpMethod;
import lombok.Data;

import java.util.List;

/**
 * 请求路径和方法信息
 */
@Data
public class PathInfo {

    private HttpMethod method;

    private List<String> paths;

    public String getPath() {
        return paths != null && paths.size() > 0 ? paths.get(0) : null;
    }

}
