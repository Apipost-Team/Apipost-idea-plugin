package com.wwr.apipost.handle.apipost.action;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.google.gson.JsonObject;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.wwr.apipost.action.AbstractAction;
import com.wwr.apipost.config.ApiPostConfig;
import com.wwr.apipost.config.domain.Api;
import com.wwr.apipost.config.domain.EventData;
import com.wwr.apipost.handle.apipost.config.ApiPostSettings;
import com.wwr.apipost.handle.apipost.config.ApiPostSettingsDialog;
import com.wwr.apipost.handle.apipost.domain.ApiPostSyncRequestEntity;
import com.wwr.apipost.handle.apipost.domain.ApiPostSyncResponseVO;
import com.wwr.apipost.openapi.OpenApiDataConvert;
import com.wwr.apipost.openapi.OpenApiGenerator;
import com.wwr.apipost.parse.util.NotificationUtils;
import com.wwr.apipost.util.FileUtilsExt;
import com.wwr.apipost.util.psi.PsiModuleUtils;
import io.swagger.v3.oas.models.OpenAPI;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static com.wwr.apipost.config.DefaultConstants.API_POST_PROJECT_ID_PREFIX;
import static com.wwr.apipost.parse.util.NotificationUtils.notifyError;
import static com.wwr.apipost.parse.util.NotificationUtils.notifyInfo;
import static com.wwr.apipost.util.JsonUtils.fromJson;
import static com.wwr.apipost.util.JsonUtils.toJson;


/**
 * <p>
 *
 * </p>
 *
 * @author wwr
 * @version 1.0
 * @date 2023/3/24
 *
 * @since 1.0.1
 */
public class ApiPostUploadAction extends AbstractAction {

    public static final String ACTION_TEXT = "Upload To ApiPost";

    /**
     * 远端地址
     */
//     public static final String remoteUrl = "https://sync-project.apipost.cn/api/convert";
//    public static final String remoteUrl = "http://127.0.0.1:8080/open/save";


    @SuppressWarnings("unused all")
    public ApiPostUploadAction() {
        super(IconLoader.getIcon("/icons/upload.png", ApiPostUploadAction.class.getClassLoader()), true);
    }

    @Override
    public boolean before(AnActionEvent event, ApiPostConfig config) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        ApiPostSettings settings = ApiPostSettings.getInstance();
        settings.setProjectId(config.getApiPostProjectId());
        if (!settings.isValidate()) {
            ApiPostSettingsDialog dialog = ApiPostSettingsDialog.show(project, event.getPresentation().getText());
            return !dialog.isCanceled();
        }
        return true;
    }

    @Override
    public boolean after(AnActionEvent event, ApiPostConfig config, EventData data) {
        ApiPostSettings settings = ServiceManager.getService(ApiPostSettings.class);
        if (StringUtils.isNotBlank(settings.getProjectId())) {
            config.setApiPostProjectId(settings.getProjectId());
            try {
                FileUtilsExt.writeText(data.getLocalDefaultFileCache(), API_POST_PROJECT_ID_PREFIX + "=" + settings.getProjectId());
            } catch (IOException e) {
                NotificationUtils.notifyError("apipost", "配置写入失败");
                return false;
            }
        }
        return true;
    }

    @Override
    public void handle(AnActionEvent event, ApiPostConfig config, List<Api> apis) {
        // 获取当前类所在模块名
        Module module = PsiModuleUtils.findModuleByEvent(event);
        ApiPostSettings settings = ApiPostSettings.getInstance();
        String token = settings.getToken();
        String projectId = settings.getProjectId();
        String remoteUrl = settings.getRemoteUrl();

        OpenAPI openApi = new OpenApiDataConvert().convert(apis);
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
            notifyInfo("Upload Result","Upload success!");
        } else {
            notifyError("Upload Result", "Upload failed!" + responseVO.getMessage());
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(ACTION_TEXT);
    }

    @Override
    public void setDefaultIcon(boolean isDefaultIconSet) {
        super.setDefaultIcon(isDefaultIconSet);
    }
}
