package com.wwr.apipost.config.domain;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.wwr.apipost.action.AbstractAction;
import com.wwr.apipost.config.DefaultConstants;
import com.wwr.apipost.util.psi.PsiFileUtils;
import lombok.Data;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author wwr
 * @version 1.0
 * @date 2023/3/30
 * @since 1.0.1
 */
@Data
public class EventData {

    /**
     * 源事件
     */
    AnActionEvent event;
    /**
     * 项目
     */
    Project project;

    /**
     * 模块
     */
    Module module;

    /**
     * 选择的文件
     */
    VirtualFile[] selectedFiles;

    /**
     * 选择的Java文件
     */
    List<PsiJavaFile> selectedJavaFiles;

    /**
     * 选择类
     */
    PsiClass selectedClass;

    /**
     * 选择方法
     */
    PsiMethod selectedMethod;

    /**
     * 是否应当继续解析处理
     */
    public boolean shouldHandle() {
        return project != null && module != null && (selectedJavaFiles != null || selectedClass != null);
    }

    /**
     * 从事件中解析需要的通用数据
     */
    public static EventData of(AnActionEvent event) {
        EventData data = new EventData();
        data.event = event;
        data.project = event.getData(CommonDataKeys.PROJECT);
        data.module = event.getData(LangDataKeys.MODULE);
        data.selectedFiles = event.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        if (data.project != null && data.selectedFiles != null) {
            data.selectedJavaFiles = PsiFileUtils.getPsiJavaFiles(data.project, data.selectedFiles);
        }
        Editor editor = event.getDataContext().getData(CommonDataKeys.EDITOR);
        PsiFile editorFile = event.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if (editor != null && editorFile != null) {
            PsiElement referenceAt = editorFile.findElementAt(editor.getCaretModel().getOffset());
            data.selectedClass = PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
            data.selectedMethod = PsiTreeUtil.getContextOfType(referenceAt, PsiMethod.class);
        }
        return data;
    }


    public File getLocalDefaultFileCache() {
        return Paths.get(new File(module.getModuleFilePath()).getParentFile().getPath(), DefaultConstants.DEFAULT_PROPERTY_FILE_CACHE).toFile();
    }

}
