package com.wwr.apipost.parse.model;

import com.wwr.apipost.config.domain.Property;
import com.wwr.apipost.config.domain.RequestBodyType;
import lombok.Data;

import java.util.List;

/**
 * 请求参数信息
 */
@Data
public class RequestInfo {

    private List<Property> parameters;
    private RequestBodyType requestBodyType;
    private Property requestBody;
    private List<Property> requestBodyForm;

}
