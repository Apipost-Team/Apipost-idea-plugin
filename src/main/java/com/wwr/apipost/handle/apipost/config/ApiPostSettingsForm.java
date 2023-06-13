package com.wwr.apipost.handle.apipost.config;

import com.wwr.apipost.handle.apipost.config.prefix.ApiPostPrefixUrlSettingsForm;
import lombok.Getter;
import org.codehaus.plexus.util.StringUtils;

import javax.swing.*;
import java.awt.*;

@Getter
public class ApiPostSettingsForm extends JDialog {
    private JPanel contentPane;
    private JTextField token;
    private JTextField projectId;
    private JTextField remoteUrl;
    private JTextField workDir;

    public ApiPostSettingsForm() {
        contentPane.setPreferredSize(new Dimension(400, 200));
    }

    public ApiPostSettings get(ApiPostPrefixUrlSettingsForm urlSettingsForm) {
        ApiPostSettings settings = new ApiPostSettings();
        settings.setToken(token.getText().trim());
        settings.setProjectId(projectId.getText().trim());
        settings.setRemoteUrl(remoteUrl.getText().trim());
        settings.setWorkDir(workDir.getText().trim());
        return settings;
    }

    public void set(ApiPostSettings settings, ApiPostPrefixUrlSettingsForm urlSettingsForm) {
        if (StringUtils.isNotBlank(settings.getToken())) {
            token.setText(settings.getToken());
        }
        if (StringUtils.isNotBlank(settings.getProjectId())) {
            projectId.setText(settings.getProjectId());
        }
        if (StringUtils.isNotBlank(settings.getRemoteUrl())) {
            remoteUrl.setText(settings.getRemoteUrl());
        }
        if (StringUtils.isNotBlank(settings.getWorkDir())){
            workDir.setText(settings.getWorkDir());
        }
    }
}
