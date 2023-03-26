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

    private static final String PASSWORD_KEY = "apiPost";


    /**
     * token
     */
    private String token;

    /**
     * 项目id
     */
    private String projectId;


    public static ApiPostSettings getInstance() {
        ApiPostSettings settings = ServiceManager.getService(ApiPostSettings.class);
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
        XmlSerializerUtil.copyBean(apiPostSettings, this);
    }

    /**
     * 配置是否有效
     */
    public boolean isValidate() {
        return StringUtils.isNotEmpty(token) && StringUtils.isNotEmpty(projectId);
    }
}
