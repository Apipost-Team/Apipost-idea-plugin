package com.wwr.apipost.handle.apipost.config;

import lombok.Getter;
import org.codehaus.plexus.util.StringUtils;

import javax.swing.*;
import java.awt.event.*;

@Getter
public class ApiPostSettingsForm extends JDialog {
    private JPanel contentPane;
    private JTextField token;
    private JTextField projectId;
    private String projectName;
    private JTextField remoteUrl;
    private JLabel projectIdTitle;

    public ApiPostSettings get() {
        //ApiPostSettings settings = new ApiPostSettings();
        ApiPostSettings settings = ApiPostSettings.getInstance();
        settings.setToken(token.getText().trim());
        settings.setProjectId(projectId.getText().trim());
        settings.setProjectName(projectName);
        settings.setRemoteUrl(remoteUrl.getText().trim());
        return settings;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
        this.projectIdTitle.setText(projectName + "ÏîÄ¿ID");
    }
    public void set(ApiPostSettings settings) {
        if (StringUtils.isNotBlank(settings.getToken())) {
            token.setText(settings.getToken());
        }
        if (StringUtils.isNotBlank(settings.getProjectId())) {
            projectId.setText(settings.getProjectId());
        }
        if (StringUtils.isNotBlank(settings.getRemoteUrl())){
            remoteUrl.setText(settings.getRemoteUrl());
        }
    }
}
