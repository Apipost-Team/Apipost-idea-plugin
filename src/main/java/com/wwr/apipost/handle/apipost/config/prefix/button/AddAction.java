package com.wwr.apipost.handle.apipost.config.prefix.button;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.wwr.apipost.handle.apipost.config.ApiPostSettings;
import com.wwr.apipost.handle.apipost.config.prefix.dialog.ChooseModuleDialog;
import com.wwr.apipost.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        ApiPostSettings setting = ApiPostSettings.getInstance();
        List<String> moduleNameList = getModuleNameList(setting);
        if (!moduleNameList.isEmpty()) {
            ChooseModuleDialog dialog = new ChooseModuleDialog(moduleNameList);
            dialog.show();
            if (dialog.isOK()) {
                String selectModuleName = dialog.getSelectModuleName();
                try {
                    addRowData(selectModuleName,setting);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void addRowData(String moduleName,ApiPostSettings setting) {
        if (StringUtils.isNotBlank(moduleName)) {
            DefaultTableModel model = (DefaultTableModel) prefixUrl.getModel();
            Object[] rowData = {moduleName};
            model.addRow(rowData);
            prefixUrl.repaint();
            int rowCount = model.getRowCount();
            int newRow = rowCount - 1;
            prefixUrl.setRowSelectionInterval(newRow, newRow);
            Project project = null;
            IdeFrame ideFrame = WindowManager.getInstance().getIdeFrame(null);
            if (ideFrame != null) {
                project = ideFrame.getProject();
            }
            if(null!=project){
                ModuleManager moduleManager = ModuleManager.getInstance(project);
                Module[] modules = moduleManager.getModules();
                List<Module> modulesList = Arrays.asList(modules);
                Module module = modulesList.stream().filter(t -> t.getName().equals(moduleName)).collect(Collectors.toList()).get(0);
                String prefixUrl1 = null;
                try {
                    prefixUrl1 = CommonUtil.getPrefixUrl(module, setting);
                } catch (Exception e) {
                    e.printStackTrace();
                    prefixUrl.editCellAt(newRow, 1);
                }
                if(null!=prefixUrl1 && !"".equals(prefixUrl1)){
                    prefixUrl.getModel().setValueAt(prefixUrl1,newRow, 1);
                }else{
                    prefixUrl.editCellAt(newRow, 1);
                }
            }
        }
    }

    private List<String> getModuleNameList(ApiPostSettings setting) {
        Project project = null;
        IdeFrame ideFrame = WindowManager.getInstance().getIdeFrame(null);
        if (ideFrame != null) {
            project = ideFrame.getProject();
        }
        if (project == null) {
            return Collections.emptyList();
        }
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
            if (name != null && !"".equals(name)) {
                tableModuleName.add(name.toString());
            }
        }
        return tableModuleName;
    }

}