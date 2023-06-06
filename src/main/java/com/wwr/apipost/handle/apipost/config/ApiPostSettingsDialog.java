package com.wwr.apipost.handle.apipost.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * <p>
 *
 * </p>
 *
 * @author wwr
 * @version 1.0
 * @date 2023/3/24
 * 
 * @since 1.0.1
 */
public class ApiPostSettingsDialog extends DialogWrapper {

    private ApiPostSettingsForm form;

    ApiPostSettingsDialog(@Nullable Project project, String title) {
        super(project);
        setTitle(title);
        init();
    }

    /**
     * 显示弹框
     */
    public static ApiPostSettingsDialog show(Project project, String title) {
        ApiPostSettingsDialog dialog = new ApiPostSettingsDialog(project, title);
        //隐藏服务前置URL框
        dialog.form.hiddenServerUrl();
        dialog.show();
        return dialog;
    }

    @Override
    protected void init() {
        super.init();
        ApiPostSettings setting = ApiPostSettings.getInstance();
        form.set(setting);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (form == null) {
            form = new ApiPostSettingsForm();
        }
        return form.getContentPane();
    }

    @Override
    protected void doOKAction() {
        ApiPostSettings settings = form.get();
        if (StringUtils.isBlank(settings.getToken())) {
            setErrorText("token不能为空", form.getToken());
        }
        if (StringUtils.isBlank(settings.getProjectId())) {
            setErrorText("项目id不能为空", form.getProjectId());
        }
        if (settings.isValidate()) {
            ApiPostSettings.storeInstance(form.get());
            super.doOKAction();
        }
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        ApiPostSettings data = form.get();
        if (StringUtils.isEmpty(data.getToken())) {
            return new ValidationInfo("Token不能为空", form.getToken());
        }
        if (StringUtils.isEmpty(data.getProjectId())) {
            return new ValidationInfo("项目ID不能为空", form.getProjectId());
        }
        return null;
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
    }

    public boolean isCanceled() {
        return this.getExitCode() == DialogWrapper.CANCEL_EXIT_CODE;
    }
}
