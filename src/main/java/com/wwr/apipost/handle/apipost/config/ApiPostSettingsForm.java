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

    public ApiPostSettings get() {
        ApiPostSettings settings = new ApiPostSettings();
        settings.setToken(token.getText().trim());
        settings.setProjectId(projectId.getText().trim());
        return settings;
    }

    public void set(ApiPostSettings settings) {
        if (StringUtils.isNotBlank(settings.getToken())) {
            token.setText(settings.getToken());
        }
        if (StringUtils.isNotBlank(settings.getProjectId())) {
            projectId.setText(settings.getProjectId());
        }
    }
}
