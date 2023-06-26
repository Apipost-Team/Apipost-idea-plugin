package com.wwr.apipost.handle.apipost.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.wwr.apipost.config.domain.PrefixUrl;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
     * 自定义目录
     *
     */
    private String workDir;

    /**
     * 服务前置URL配置信息
     */
    private List<PrefixUrl> prefixUrlList;


    public static ApiPostSettings getInstance() {
        ApiPostSettings settings = ApplicationManager.getApplication().getService(ApiPostSettings.class);
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
}
