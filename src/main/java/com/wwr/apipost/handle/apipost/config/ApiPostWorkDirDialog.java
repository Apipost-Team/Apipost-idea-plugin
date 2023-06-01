package com.wwr.apipost.handle.apipost.config;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.google.gson.JsonObject;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.wwr.apipost.config.ApiPostConfig;
import com.wwr.apipost.config.domain.Api;
import com.wwr.apipost.handle.apipost.domain.ApiPostSyncRequestEntity;
import com.wwr.apipost.handle.apipost.domain.ApiPostSyncResponseVO;
import com.wwr.apipost.openapi.OpenApiDataConvert;
import com.wwr.apipost.openapi.OpenApiGenerator;
import io.swagger.v3.oas.models.OpenAPI;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

import static com.wwr.apipost.parse.util.NotificationUtils.notifyError;
import static com.wwr.apipost.parse.util.NotificationUtils.notifyInfo;
import static com.wwr.apipost.util.JsonUtils.fromJson;
import static com.wwr.apipost.util.JsonUtils.toJson;

/**
 * @author JouTao
 * @version 1.0
 * @date 2023/6/1
 * 
 * @since 1.0.1
 */
public class ApiPostWorkDirDialog extends DialogWrapper {

    private ApiPostWorkDirForm form;

    ApiPostWorkDirDialog(@Nullable Project project, String title) {
        super(project);
        setTitle(title);
        init();
    }

    /**
     * 显示弹框
     */
    public static ApiPostWorkDirDialog show(Project project, String title) {
        ApiPostWorkDirDialog dialog = new ApiPostWorkDirDialog(project, title);
        dialog.show();
        return dialog;
    }

    @Override
    protected void init() {
        super.init();
        ApiPostSettings setting = ApiPostSettings.getInstance();
        form.set(setting.getWorkDir());
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (form == null) {
            form = new ApiPostWorkDirForm();
        }
        return form.getContentPane();
    }

    @Override
    protected void doOKAction() {
        String workDir = form.get();
        if (StringUtils.isBlank(workDir)){
            setErrorText("请选择工作目录", form.getWorkDir());
        }
        super.doOKAction();
    }

    public void doOKAction(Module module,List<Api> apis) {
        super.doOKAction();
        String workDir = form.get();
        ApiPostSettings settings = ApiPostSettings.getInstance();
        String token = settings.getToken();
        String projectId = settings.getProjectId();
        String remoteUrl = settings.getRemoteUrl();
        for (Api api : apis) {
            api.setCategory(workDir);
        }
        OpenAPI openApi = new OpenApiDataConvert().convert(apis);
        int apiNum =  openApi.getPaths().size();
        if (apiNum < 1){
            notifyInfo("Upload Result","Api not found!");
            return;
        }
        openApi.getInfo().setTitle(module.getName());
        JsonObject apiJsonObject = new OpenApiGenerator().generate(openApi);
        // 上传到ApiPost
        ApiPostSyncRequestEntity entity = new ApiPostSyncRequestEntity();
        entity.setOpenApi(apiJsonObject);
        entity.setProjectId(projectId);
        String requestBodyJson = toJson(entity);
        String responseBody;
        try {
            HttpResponse response = HttpRequest.post(remoteUrl)
                    .header("Content-Type", "application/json")
                    .header("token", token)
                    .body(requestBodyJson)
                    .execute();
            responseBody = response.body();
        } catch (Exception e) {
            notifyError("upload error: network error!");
            return;
        }
        ApiPostSyncResponseVO responseVO = fromJson(responseBody, ApiPostSyncResponseVO.class);

        if (responseVO.isSuccess()) {
            notifyInfo("Upload Result", String.format("Upload %d Api success!", apiNum));
        } else {
            notifyError("Upload Result", "Upload failed!" + responseVO.getMessage());
        }
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        String workDir = form.get();
        if (StringUtils.isBlank(workDir)){
            setErrorText("请选择工作目录", form.getWorkDir());
        }
        return null;
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
    }

    public boolean isCanceled() {
        return this.getExitCode() == DialogWrapper.CANCEL_EXIT_CODE;
    }
}
