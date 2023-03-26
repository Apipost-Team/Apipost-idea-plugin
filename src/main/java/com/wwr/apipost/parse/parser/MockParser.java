package com.wwr.apipost.parse.parser;

import com.google.common.collect.Lists;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.wwr.apipost.config.ApiPostConfig;
import com.wwr.apipost.config.domain.DataTypes;
import com.wwr.apipost.config.domain.MockRule;
import com.wwr.apipost.config.domain.Property;
import com.wwr.apipost.parse.constant.DocumentTags;
import com.wwr.apipost.util.JsonUtils;
import com.wwr.apipost.util.PropertiesLoader;
import com.wwr.apipost.util.psi.PsiDocCommentUtils;
import com.wwr.apipost.util.psi.PsiTypeUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Mock解析
 */
public class MockParser {

    private static final String FILE = "mocks.properties";
    private final Project project;
    private final Module module;
    private final ApiPostConfig settings;

    public MockParser(Project project, Module module, ApiPostConfig settings) {
        this.project = project;
        this.module = module;
        this.settings = settings;
    }

    /**
     * 获取字段类型
     */
    public String parse(Property property, PsiType type, PsiField field, String filedName) {
        // 自定义标记
        if (field != null) {
            String mock = PsiDocCommentUtils.getTagText(field, DocumentTags.Mock);
            if (StringUtils.isNotEmpty(mock)) {
                return mock;
            }
        }

        // 时间类型
        if (DateParser.isDateType(type)) {
            String mock = "@integer(1210573684000, 1896710400000)";
            if (DataTypes.STRING.equals(property.getType())) {
                mock = "@datetime";
                if (StringUtils.isNotEmpty(property.getDateFormat())) {
                    mock = String.format("@datetime(\"%s\")", property.getDateFormat());
                }
            }
            return mock;
        }

        // 枚举类型
        List<String> values = property.getValueList();
        if (!values.isEmpty()) {
            String paramExpression;
            if (property.isNumberOrIntegerType()) {
                List<BigDecimal> decimalValues = values.stream().map(val -> {
                    try {
                        return new BigDecimal(val);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
                paramExpression = JsonUtils.toJson(decimalValues);
            } else {
                paramExpression = JsonUtils.toJson(values);
            }
            return "@pick(" + paramExpression + ")";
        }
        // 数组类型处理
        if (PsiTypeUtils.isArray(type) || PsiTypeUtils.isCollection(type, this.project, this.module)) {
            return null;
        }

        // 数字类型从最大、最小值
        if (property.isNumberOrIntegerType() && (property.getMinimum() != null || property.getMaximum() == null)) {
            List<String> params = Lists.newArrayList();
            if (property.getMinimum() != null) {
                params.add(property.getMinimum().toPlainString());
            }
            if (property.getMaximum() != null) {
                params.add(property.getMaximum().toPlainString());
            }
            String paramExpression = StringUtils.join(params, ",");
            if (property.isIntegerType()) {
                return "@integer(" + paramExpression + ")";
            } else {
                return "@float(" + paramExpression + ")";
            }
        }

        // 自定义规则
        if (StringUtils.isNotEmpty(filedName)) {
            String mock = matchRulesMock(property.getType(), filedName);
            if (StringUtils.isNotEmpty(mock)) {
                return mock;
            }
        }

        // 长度
        if (property.isStringType() && (property.getMinLength() != null || property.getMaxLength() != null)) {
            List<String> params = Lists.newArrayList();
            if (property.getMinLength() != null) {
                params.add(property.getMinLength().toString());
            }
            if (property.getMaxLength() != null) {
                params.add(property.getMaxLength().toString());
            }
            String paramsExpression = StringUtils.join(params, ",");
            return "@string(" + paramsExpression + ")";
        }

        // 规定规则
        Properties properties = PropertiesLoader.getProperties(FILE);
        return properties.getProperty(type.getCanonicalText());
    }

    /**
     * 匹配自定义mock规则
     */
    private String matchRulesMock(String type, String filedName) {
        if (settings.getMockRules() == null) {
            return null;
        }
        for (MockRule rule : settings.getMockRules()) {
            if (rule != null && rule.match(type, filedName)) {
                return rule.getMock();
            }
        }
        return null;
    }

}
