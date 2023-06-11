package com.wwr.apipost.handle.apipost.config;

import com.intellij.openapi.options.Configurable;
import com.wwr.apipost.handle.apipost.config.prefix.ApiPostPrefixUrlSettingsForm;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
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

    private ApiPostPrefixUrlSettingsForm urlSettingsForm;

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
        if (urlSettingsForm == null) {
            urlSettingsForm = new ApiPostPrefixUrlSettingsForm();
        }

        JPanel jPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(jPanel, BoxLayout.Y_AXIS);
        jPanel.setLayout(boxLayout);
        JPanel contentPane = form.getContentPane();
        contentPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, contentPane.getPreferredSize().height));
        jPanel.add(contentPane);

        jPanel.add(Box.createVerticalGlue());

        jPanel.add(urlSettingsForm.getContentPane());
        return jPanel;
    }

    @Override
    public boolean isModified() {
        ApiPostSettings settings = ApiPostSettings.getInstance();
        ApiPostSettings apiPostSettings = form.get(urlSettingsForm);

        if (!Objects.equals(settings.getRemoteUrl(), apiPostSettings.getRemoteUrl())) {
            return Boolean.TRUE;
        }
        if (!Objects.equals(settings.getToken(), apiPostSettings.getToken())) {
            return Boolean.TRUE;
        }
        if (!Objects.equals(settings.getProjectId(), apiPostSettings.getProjectId())) {
            return Boolean.TRUE;
        }
        return !Objects.equals(settings.getPrefixUrlList(), apiPostSettings.getPrefixUrlList());
    }

    @Override
    public void apply() {
        ApiPostSettings apiPostSettings = form.get(urlSettingsForm);
        urlSettingsForm.get(apiPostSettings);
        ApiPostSettings.storeInstance(apiPostSettings);
    }

    @Override
    public void reset() {
        ApiPostSettings settings = ApiPostSettings.getInstance();
//        settings.setProjectId(null);
        form.set(settings, urlSettingsForm);
    }

    @Override
    public void disposeUIResources() {
        this.form = null;
    }
}
