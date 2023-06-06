package com.wwr.apipost.handle.apipost.config;

import cn.hutool.core.util.StrUtil;
import com.intellij.ui.JBColor;
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

    public ApiPostSettingsForm() {
        contentPane.setPreferredSize(new Dimension(400, 200));
        preUrlMap.setBorder(BorderFactory.createLineBorder(JBColor.BLACK));
        preUrlMap.setSelectedTextColor(JBColor.BLUE);
//        preUrlMap.setBorder(BorderFactory.createEmptyBorder());
//        preUrlMap.setFocusable(true);
    }

    public ApiPostSettings get() {
        ApiPostSettings settings = new ApiPostSettings();
        settings.setToken(token.getText().trim());
        settings.setProjectId(projectId.getText().trim());
        settings.setRemoteUrl(remoteUrl.getText().trim());
        Map<String, String> stringMap = StrUtil.split(preUrlMap.getText().trim(), ";")
                .stream().filter(StrUtil::isNotBlank)
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
                preUrlMapStr.append(serverName).append("=").append(serverUrl).append(";");
            }
            preUrlMap.setText(preUrlMapStr.toString());
        }
    }

    /**
     * 感觉没必要显示
     */
    public void hiddenServerUrl() {
        serverUrl.setVisible(false);
        preUrlMap.setVisible(false);
    }
}
