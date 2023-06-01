package com.wwr.apipost.handle.apipost.config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

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
public class APiPostSettingsConfiguration implements Configurable {

    private ApiPostSettingsForm form;

    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "ApiPost Config";
    }

    @Override
    public @Nullable JComponent createComponent() {
        if (form == null) {
            form = new ApiPostSettingsForm();
        }
        return form.getContentPane();
    }

    @Override
    public boolean isModified() {
        ApiPostSettings settings = ApiPostSettings.getInstance();
        ApiPostSettings apiPostSettings = form.get();
        // 每个输入框逐一比对
        if (!Objects.equals(settings.getRemoteUrl(), apiPostSettings.getRemoteUrl())) {
            return Boolean.TRUE;
        }
        if (!Objects.equals(settings.getToken(), apiPostSettings.getToken())) {
            return Boolean.TRUE;
        }
        if (!Objects.equals(settings.getWorkDir(), apiPostSettings.getWorkDir())) {
            return Boolean.TRUE;
        }
        return !Objects.equals(settings.getProjectId(), apiPostSettings.getProjectId());
    }

    @Override
    public void apply() {
        ApiPostSettings apiPostSettings = form.get();
        ApiPostSettings.storeInstance(apiPostSettings);
    }

    @Override
    public void reset() {
        ApiPostSettings settings = ApiPostSettings.getInstance();
//        settings.setProjectId(null);
        form.set(settings);
    }

    @Override
    public void disposeUIResources() {
        this.form = null;
    }
}
