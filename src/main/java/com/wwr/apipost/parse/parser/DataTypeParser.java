package com.wwr.apipost.parse.parser;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiType;
import com.wwr.apipost.config.ApiPostConfig;
import com.wwr.apipost.config.domain.DataTypes;
import com.wwr.apipost.util.PropertiesLoader;
import com.wwr.apipost.util.psi.PsiTypeUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * 字段类型工具类.
 */
public final class DataTypeParser {

    private static final String FILE = "types.properties";
    private final Project project;
    private final Module module;
    private final ApiPostConfig settings;

    public DataTypeParser(Project project, Module module, ApiPostConfig settings) {
        this.project = project;
        this.module = module;
        this.settings = settings;
    }

    /**
     * 获取字段类型
     */
    public String parse(PsiType type) {
        // 数组类型处理
        if (PsiTypeUtils.isArray(type) || PsiTypeUtils.isCollection(type, this.project, this.module)) {
            return DataTypes.ARRAY;
        }
        boolean isEnum = PsiTypeUtils.isEnum(type);
        if (isEnum) {
            return DataTypes.STRING;
        }
        String dataType = getTypeInProperties(type);
        return StringUtils.isEmpty(dataType) ? DataTypes.OBJECT : dataType;
    }

    public static String getTypeInProperties(PsiType type) {
        Properties typeProperties = PropertiesLoader.getProperties(FILE);
        return typeProperties.getProperty(type.getCanonicalText());
    }

}
