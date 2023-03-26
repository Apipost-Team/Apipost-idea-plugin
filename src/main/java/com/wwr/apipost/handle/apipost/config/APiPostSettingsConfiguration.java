package com.wwr.apipost.handle.apipost.config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

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
        return "ApiPost配置";
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
        return !settings.equals(apiPostSettings);
    }

    @Override
    public void apply() {
        ApiPostSettings apiPostSettings = form.get();
        ApiPostSettings.storeInstance(apiPostSettings);
    }

    @Override
    public void reset() {
        ApiPostSettings settings = ApiPostSettings.getInstance();
        form.set(settings);
    }

    @Override
    public void disposeUIResources() {
        this.form = null;
    }
}
