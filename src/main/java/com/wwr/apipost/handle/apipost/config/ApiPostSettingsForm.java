package com.wwr.apipost.handle.apipost.config;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import org.codehaus.plexus.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.stream.Collectors;

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
        Map<String, String> stringMap = StrUtil.split(preUrlMap.getText().trim(), "\r\n")
                .stream().map(String::trim).filter(StrUtil::isNotBlank)
                .map(item -> item.split("="))
                .collect(Collectors.toMap(split -> split[0], split -> split[1]));
        settings.setPreMapUrl(stringMap);
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
        Map<String, String> preMapUrl = settings.getPreMapUrl();
        if (preMapUrl != null && preMapUrl.size() > 0) {
            StringBuilder preUrlMapStr = new StringBuilder();
            for (Map.Entry<String, String> entry : preMapUrl.entrySet()) {
                String serverName = entry.getKey();
                String serverUrl = entry.getValue();
                preUrlMapStr.append(serverName).append("=").append(serverUrl).append("\r\n");
            }
            preUrlMap.setText(preUrlMapStr.toString());
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
