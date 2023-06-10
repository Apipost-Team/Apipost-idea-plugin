package com.wwr.apipost.handle.apipost.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

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
@Getter
@Setter
@State(name = "ApiPostSettings", storages = @Storage("ApiPostSettings.xml"))
public class ApiPostSettings implements PersistentStateComponent<ApiPostSettings> {

    /**
     * token
     */
    private String token;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 项目id
     */
    private String projectName;

    /**
     * 远端地址
     *
     */
    private String remoteUrl;

    /**
     * project name 和 project id 对应关系
     *
     */
    private Map<String, String> projectMap = new LinkedHashMap<>();

    /**
     * 强制把文件中项目设置为当前
     * @param projectName
     */
    public static void syncProjectName(String projectName){
        ApiPostSettings settings = ServiceManager.getService(ApiPostSettings.class);
        String projectName1 = settings.getProjectName();
        if (!projectName1.equals(projectName)){
            settings.setProjectName(projectName1);
            storeInstance(settings);
        }
    }

    public static ApiPostSettings getInstance() {
        ApiPostSettings settings = ServiceManager.getService(ApiPostSettings.class);
        if (org.codehaus.plexus.util.StringUtils.isBlank(settings.getRemoteUrl())){
            settings.setRemoteUrl("https://sync-project.apipost.cn/api/convert"); //设置默认值
        }

        String projectName1 = settings.getProjectName();
        Map<String, String> projectMap1 = settings.getProjectMap();
        if (!org.codehaus.plexus.util.StringUtils.isBlank(projectName1)){
            String projectId = projectMap1.get(projectName1);
            if (org.codehaus.plexus.util.StringUtils.isBlank(projectId)){
                settings.setProjectId(null);
            }else{
                settings.setProjectId(projectId);
            }
        }

        return settings;
    }

    public static void storeInstance(@NotNull ApiPostSettings state) {
        getInstance().loadState(state);
    }

    @Nullable
    @Override
    public ApiPostSettings getState() {
        return this;
    }


    @Override
    public void loadState(@NotNull ApiPostSettings apiPostSettings) {
        String projectId = apiPostSettings.getProjectId();
        if (!org.codehaus.plexus.util.StringUtils.isBlank(projectId)){
            String projectName = apiPostSettings.getProjectName();
            if (!org.codehaus.plexus.util.StringUtils.isBlank(projectName)){
                Map<String, String> projectMap1 = apiPostSettings.getProjectMap();
                projectMap1.put(projectName, projectId);
//                apiPostSettings.setProjectMap();
            }
        }
        XmlSerializerUtil.copyBean(apiPostSettings, this);
    }

    /**
     * 配置是否有效
     */
    public boolean isValidate() {
        return StringUtils.isNotEmpty(token) && StringUtils.isNotEmpty(projectId);
    }
}
