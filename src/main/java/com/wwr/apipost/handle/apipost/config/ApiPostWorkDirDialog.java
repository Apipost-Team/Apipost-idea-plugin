package com.wwr.apipost.handle.apipost.config;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.google.gson.JsonObject;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.wwr.apipost.config.domain.Api;
import com.wwr.apipost.handle.apipost.domain.ApiPostSyncRequestEntity;
import com.wwr.apipost.handle.apipost.domain.ApiPostSyncResponseVO;
import com.wwr.apipost.openapi.OpenApiDataConvert;
import com.wwr.apipost.openapi.OpenApiGenerator;
import io.swagger.v3.oas.models.OpenAPI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static ApiPostWorkDirForm form;

    private Module module;

    private List<Api> apis;

    ApiPostWorkDirDialog(@Nullable Project project, String title,Module m,List<Api> a){
        super(project);
        setTitle(title);
        this.apis=a;
        this.module=m;
        init();
        this.form.buttonAction(project,title);
    }

    /**
     * 显示弹框
     */
    public static ApiPostWorkDirDialog show(Project project, String title,Module m,List<Api> a) {
        ApiPostWorkDirDialog dialog = new ApiPostWorkDirDialog(project, title,m,a);
        dialog.show();
        return dialog;
    }

    @Override
    protected void init() {
        super.init();
        ApiPostSettings setting = ApiPostSettings.getInstance();
        form.reSet(setting.getWorkDir());
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
        super.doOKAction();
        Map<String, Object> map = doOKAction(module, apis);
        if((boolean)map.get("isSuccess")){
            notifyInfo((String) map.get("title"), (String) map.get("content"));
        }else{
            notifyError((String) map.get("title"), (String) map.get("content"));
        }
    }

    public Map<String,Object> doOKAction(Module module, List<Api> apis) {
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
        Map<String,Object> map=new HashMap<>();
        if (apiNum < 1){
            map.put("title","Upload Result");
            map.put("content","Api not found!");
            map.put("isSuccess",true);
            return map;
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
            map.put("title","upload error");
            map.put("content","network error!");
            map.put("isSuccess",false);
            return map;
        }
        ApiPostSyncResponseVO responseVO = fromJson(responseBody, ApiPostSyncResponseVO.class);
        if (responseVO.isSuccess()) {
            map.put("title","Upload Result");
            map.put("content",String.format("Upload %d Api success!", apiNum));
            map.put("isSuccess",true);
        } else {
            map.put("title","Upload Result");
            map.put("content","Upload failed!" + responseVO.getMessage());
            map.put("isSuccess",false);
        }
        return map;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        String workDir = form.get();
        if(workDir.contains(",")){
            return new ValidationInfo("工作目录请不要包含英文逗号！！！", form.getWorkDir());
        }
        return null;
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
    }

    public static void editWorkDir(String workDirs) {
        form.reSet(workDirs);
    }
}
