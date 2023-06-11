package com.wwr.apipost.handle.apipost.config.prefix.dialog;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * 选择服务
 *
 * @author linyuan
 **/
public class ChooseModuleDialog extends DialogWrapper {
    private final ComboBox<String> stringComboBox = new ComboBox<>();

    private final java.util.List<String> moduleNameList;

    private String selectModuleName;

    public ChooseModuleDialog(java.util.List<String> moduleNameList) {
        super(true);
        setTitle("Choose Module");
        this.moduleNameList = moduleNameList;
        init();
    }

    @Override
    protected void init() {
        if (moduleNameList != null) {
            for (String moduleName : moduleNameList) {
                stringComboBox.addItem(moduleName);
            }
        }
        super.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(stringComboBox, BorderLayout.CENTER);
        return panel;
    }

    @Override
    protected void doOKAction() {
        int selectedIndex = stringComboBox.getSelectedIndex();
        selectModuleName = stringComboBox.getItemAt(selectedIndex);
        super.doOKAction();
    }


    public String getSelectModuleName() {
        return this.selectModuleName;
    }
}
