package com.wwr.apipost.handle.apipost.config;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;
import org.codehaus.plexus.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

@Getter
public class ApiPostSettingsForm extends JDialog {
    private JPanel contentPane;
    private JTextField token;
    private JTextField projectId;
    private JTextField remoteUrl;
    private JCheckBox serverUrlCheckBox;
    private JTextArea preUrlMap;
    private JScrollPane scrollPane;
    /**
     * serverUrlCheckBox 选中状态
     */
    private Boolean suCheckBoxState;

    public ApiPostSettingsForm() {
        contentPane.setPreferredSize(new Dimension(400, 200));
        serverUrlCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                // 当 serverUrlCheckBox 被选中时，显示 scrollPane
                scrollPane.setVisible(true);
                this.suCheckBoxState = true;
            } else {
                // 当 serverUrlCheckBox 取消选中时，隐藏 scrollPane
                scrollPane.setVisible(false);
                this.suCheckBoxState = false;
            }
        });

    }

    public ApiPostSettings get() {
        ApiPostSettings settings = new ApiPostSettings();
        settings.setToken(token.getText().trim());
        settings.setProjectId(projectId.getText().trim());
        settings.setRemoteUrl(remoteUrl.getText().trim());
        settings.setPreMapUrl(preUrlMap.getText().trim());
        settings.setSuCheckBoxState(suCheckBoxState);
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
        if(ObjectUtil.isNotNull(settings.getSuCheckBoxState())){
            suCheckBoxState = settings.getSuCheckBoxState();
            serverUrlCheckBox.setSelected(settings.getSuCheckBoxState());
        }
    }

    /**
     * 在弹框中不显示
     */
    public void hiddenServerUrl() {
        scrollPane.setVisible(false);
        serverUrlCheckBox.setVisible(false);
        preUrlMap.setVisible(false);
    }



}
