package com.wwr.apipost.util.psi;

import com.google.common.collect.Lists;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;

/**
 * PsiFile工具类
 */
@UtilityClass
public class PsiFileUtils {

    /**
     * 获取Java文件
     */
    public static List<PsiJavaFile> getPsiJavaFiles(Project project, VirtualFile[] psiFiles) {
        List<PsiJavaFile> files = Lists.newArrayListWithExpectedSize(psiFiles.length);
        PsiManager psiManager = PsiManager.getInstance(project);
        for (VirtualFile f : psiFiles) {
            Module module = ModuleUtilCore.findModuleForFile(f, project);
            if (module == null) {
                continue;
            }
            if (f.isDirectory()) {
                VirtualFile[] children = f.getChildren();
                List<PsiJavaFile> theFiles = getPsiJavaFiles(project, children);
                files.addAll(theFiles);
                continue;
            }
            PsiFile file = psiManager.findFile(f);
            if (file instanceof PsiJavaFileImpl) {
                files.add((PsiJavaFileImpl) file);
            }
        }
        return files;
    }

    /**
     * 获取PsiClass
     */
    public static List<PsiClass> getPsiClassByFile(List<PsiJavaFile> psiJavaFiles) {
        List<PsiClass> psiClassList = Lists.newArrayListWithCapacity(psiJavaFiles.size());
        for (PsiJavaFile psiJavaFile : psiJavaFiles) {
            Arrays.stream(psiJavaFile.getClasses())
                    .filter(o -> !o.isInterface()
                            && o.getModifierList() != null
                            && o.getModifierList().hasModifierProperty(PsiModifier.PUBLIC))
                    .findFirst().ifPresent(psiClassList::add);
        }
        return psiClassList;
    }
}
