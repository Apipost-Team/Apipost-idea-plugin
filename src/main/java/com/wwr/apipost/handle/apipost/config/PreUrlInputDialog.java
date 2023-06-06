package com.wwr.apipost.handle.apipost.config;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class PreUrlInputDialog extends DialogWrapper {

    private JTextField textField;

    public PreUrlInputDialog(String title) {
        super(true);
        init();
        setTitle(title);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        // 创建输入框和布局
        JPanel panel = new JPanel(new BorderLayout());
        textField = new JTextField();
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }

    public String getTextFieldText() {
        return textField.getText() == null ? "" : textField.getText();
    }

}
