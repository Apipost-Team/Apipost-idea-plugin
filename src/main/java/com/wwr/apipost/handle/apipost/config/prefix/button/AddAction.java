package com.wwr.apipost.handle.apipost.config.prefix.button;

import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.wwr.apipost.handle.apipost.config.prefix.dialog.ChooseModuleDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 新增服务的前置URL
 *
 * @author linyuan
 */
public class AddAction extends AbstractAction {

    JTable prefixUrl;

    public AddAction(JTable prefixUrl) {
        super("Add");
        this.prefixUrl = prefixUrl;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        List<String> moduleNameList = getModuleNameList();
        if (!moduleNameList.isEmpty()) {
            ChooseModuleDialog dialog = new ChooseModuleDialog(moduleNameList);
            dialog.show();
            if (dialog.isOK()) {
                String selectModuleName = dialog.getSelectModuleName();
                addRowData(selectModuleName);
            }
        }
    }


    private void addRowData(String moduleName) {
        if (StrUtil.isNotBlank(moduleName)) {
            DefaultTableModel model = (DefaultTableModel) prefixUrl.getModel();
            Object[] rowData = {moduleName, "http://127.0.0.1:8080"};
            model.addRow(rowData);
            prefixUrl.repaint();

            int rowCount = model.getRowCount();
            int newRow = rowCount - 1;
            prefixUrl.setRowSelectionInterval(newRow, newRow);
        }
    }

    private List<String> getModuleNameList() {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        ModuleManager moduleManager = ModuleManager.getInstance(project);
        Module[] modules = moduleManager.getModules();
        List<String> tableModuleNameList = getTableModuleNameList();
        List<String> moduleNameList = new ArrayList<>();
        for (Module module : modules) {
            String name = module.getName();
            if (!tableModuleNameList.contains(name)) {
                moduleNameList.add(name);
            }
        }
        return moduleNameList;
    }

    private List<String> getTableModuleNameList() {
        int rowCount = prefixUrl.getRowCount();
        List<String> tableModuleName = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            Object name = prefixUrl.getModel().getValueAt(i, 0);
            if (!StrUtil.isBlankIfStr(name)) {
                tableModuleName.add(name.toString());
            }
        }
        return tableModuleName;
    }

}