package com.wwr.apipost.handle.apipost.config;

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
    private JLabel serverUrl;
    private JTextArea preUrlMap;
    private JScrollPane scrollPane;

    public ApiPostSettingsForm() {
        contentPane.setPreferredSize(new Dimension(400, 200));
    }

    public ApiPostSettings get() {
        ApiPostSettings settings = new ApiPostSettings();
        settings.setToken(token.getText().trim());
        settings.setProjectId(projectId.getText().trim());
        settings.setRemoteUrl(remoteUrl.getText().trim());
        settings.setPreMapUrl(preUrlMap.getText().trim());
        return settings;
    }

    public void set(ApiPostSettings settings) {
        if (StringUtils.isNotBlank(settings.getToken())) {
            token.setText(settings.getToken());
        }
        if (StringUtils.isNotBlank(settings.getProjectId())) {
            projectId.setText(settings.getProjectId());
        }
        if (StringUtils.isNotBlank(settings.getRemoteUrl())) {
            remoteUrl.setText(settings.getRemoteUrl());
        }
        if (StringUtils.isNotBlank(settings.getPreMapUrl())) {
            preUrlMap.setText(settings.getPreMapUrl());
        }
    }

    /**
     * 在弹框中不显示
     */
    public void hiddenServerUrl() {
        scrollPane.setVisible(false);
        serverUrl.setVisible(false);
        preUrlMap.setVisible(false);
    }
}
