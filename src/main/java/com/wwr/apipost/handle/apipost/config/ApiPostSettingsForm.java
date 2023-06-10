package com.wwr.apipost.handle.apipost.config;

import com.intellij.internal.statistic.eventLog.util.StringUtil;
import com.intellij.openapi.components.ServiceManager;
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
        ApiPostSettings settings = new ApiPostSettings();
        /**
         * 不知道为什么要这么做
         */
        ApiPostSettings oldSettings = ApiPostSettings.getInstance();
        settings.setProjectName(oldSettings.getProjectName());
        settings.setToken(token.getText().trim());

        String newProjectId = projectId.getText().trim();
        String oldProjectId = settings.getProjectId();
        settings.setProjectId(newProjectId);
        if (StringUtils.isNotBlank(projectName)){
            settings.setProjectName(projectName);
            if (!newProjectId.equals(oldProjectId)){
                settings.updateProjectMap(projectName, newProjectId);
            }
        }

        settings.setRemoteUrl(remoteUrl.getText().trim());
        return settings;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
        this.projectIdTitle.setText("项目ID(" +projectName +")" );
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
        if (StringUtils.isNotBlank(settings.getProjectName())){
            projectIdTitle.setText("项目ID(" + settings.getProjectName() + ")" );
        }else{
            projectIdTitle.setText("项目ID");
        }
    }
}
