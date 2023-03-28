package com.wwr.apipost.handle.apipost.domain;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author wwr
 * @version 1.0
 * @date 2023/3/25
 * @since 1.0.1
 */
public class ApiPostSyncRequestEntity implements Serializable {

    @SerializedName("project_id")
    private String projectId;

    @SerializedName("openapi")
    private JsonObject openApi;


    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public JsonObject getOpenApi() {
        return openApi;
    }

    public void setOpenApi(JsonObject openApi) {
        this.openApi = openApi;
    }

}
