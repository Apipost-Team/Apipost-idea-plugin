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

import java.util.Optional;

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
     * 远端地址
     *
     */
    private String remoteUrl;

    /**
     * 服务的URL
     */
    private String preMapUrl;

    /**
     * 服务的URL勾选状态
     */
    private Boolean suCheckBoxState;

    public static ApiPostSettings getInstance() {
        ApiPostSettings settings = ServiceManager.getService(ApiPostSettings.class);
        if (org.codehaus.plexus.util.StringUtils.isBlank(settings.getRemoteUrl())){
            settings.setRemoteUrl("https://sync-project.apipost.cn/api/convert"); //设置默认值
        }

        return settings.getState();
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
        XmlSerializerUtil.copyBean(apiPostSettings, this);
    }

    /**
     * 配置是否有效
     */
    public boolean isValidate() {
        return StringUtils.isNotEmpty(token) && StringUtils.isNotEmpty(projectId);
    }

    /**
     * 是否开启服务URL
     * @return boolean
     */
    public boolean isEnableServerUrl(){
        return Optional.ofNullable(getSuCheckBoxState()).orElse(false);
    }
}
