package com.wwr.apipost.handle.apipost.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 *
 * @author JouTao
 * @since 1.0.1
 */
public class ApiPostEditWorkDirDialog extends DialogWrapper {

    private ApiPostEditWorkDirForm form;

    ApiPostEditWorkDirDialog(@Nullable Project project, String title) {
        super(project);
        setTitle(title);
        init();
    }

    /**
     * 显示弹框
     */
    public static ApiPostEditWorkDirDialog show(Project project, String title) {
        ApiPostEditWorkDirDialog dialog = new ApiPostEditWorkDirDialog(project, title);
        dialog.show();
        return dialog;
    }

    @Override
    protected void init() {
        super.init();
        ApiPostSettings setting = ApiPostSettings.getInstance();
        form.set(setting.getWorkDir());
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (form == null) {
            form = new ApiPostEditWorkDirForm();
        }
        return form.getContentPane();
    }

    @Override
    protected void doOKAction() {
        String workDirs = form.get();
        ApiPostSettings setting = ApiPostSettings.getInstance();
        setting.setWorkDir(workDirs);
        ApiPostSettings.storeInstance(setting);
        ApiPostWorkDirDialog.editWorkDir(workDirs);
        super.doOKAction();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return null;
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
    }

}
