package com.wwr.apipost.handle.apipost.action;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.wwr.apipost.action.AbstractAction;
import com.wwr.apipost.config.ApiPostConfig;
import com.wwr.apipost.config.domain.Api;
import com.wwr.apipost.handle.apipost.config.ApiPostSettings;
import com.wwr.apipost.handle.apipost.config.ApiPostSettingsDialog;
import com.wwr.apipost.handle.apipost.domain.ApiPostSyncRequestEntity;
import com.wwr.apipost.handle.apipost.domain.ApiPostSyncResponseVO;
import com.wwr.apipost.openapi.OpenApiDataConvert;
import com.wwr.apipost.openapi.OpenApiFileType;
import com.wwr.apipost.openapi.OpenApiGenerator;
import com.wwr.apipost.util.psi.PsiModuleUtils;
import io.swagger.v3.oas.models.OpenAPI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    // public static final String remoteUrl = "https://sync-project.apipost.cn/api/convert";
    public static final String remoteUrl = "http://127.0.0.1:8080/open/save";


    @Override
    public boolean before(AnActionEvent event, ApiPostConfig config) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        ApiPostSettings settings = ApiPostSettings.getInstance();
        if (!settings.isValidate()) {
            ApiPostSettingsDialog dialog = ApiPostSettingsDialog.show(project, event.getPresentation().getText());
            return !dialog.isCanceled();
        }
        return true;
    }

    @Override
    public void handle(AnActionEvent event, ApiPostConfig config, List<Api> apis) {
        // 获取当前类所在模块名称
        Module module = PsiModuleUtils.findModuleByEvent(event);
        ApiPostSettings settings = ApiPostSettings.getInstance();
        String token = settings.getToken();
        String projectId = settings.getProjectId();

        OpenApiFileType fileType = OpenApiFileType.JSON;

        OpenAPI openApi = new OpenApiDataConvert().convert(apis);
        openApi.getInfo().setTitle(module.getName());
        String content = new OpenApiGenerator().generate(fileType, openApi);
        // 上传到ApiPost
        ApiPostSyncRequestEntity entity = new ApiPostSyncRequestEntity();
        entity.setOpenApi(content);
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
            notifyError("upload error：network error！");
            throw new RuntimeException("网络异常！");
        }
        ApiPostSyncResponseVO responseVO = fromJson(responseBody, ApiPostSyncResponseVO.class);
        if (responseVO.isSuccess()) {
            notifyInfo("上传成功");
        } else {
            notifyError("上传失败，请检查网络或配置");
        }
        System.out.println(responseVO);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(ACTION_TEXT);
    }
}
