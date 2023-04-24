package com.wwr.apipost.config;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wwr.apipost.config.domain.BeanCustom;
import com.wwr.apipost.config.domain.MockRule;
import com.wwr.apipost.handle.apipost.config.ApiPostSettings;
import com.wwr.apipost.parse.util.NotificationUtils;
import com.wwr.apipost.util.FileUtilsExt;
import com.wwr.apipost.util.PropertiesLoader;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wwr.apipost.config.DefaultConstants.*;

/**
 * ��Ӧ�ļ�.apipost
 */
@Data
public class ApiPostConfig {

    /**
     * �ϸ�ģʽ: δָ�����ࡢ�ӿ���������
     */
    private boolean strict = true;

    /**
     * ·��ǰ׺
     */
    private String path;

    /**
     * yapi��Ŀid
     */
    private String yapiProjectId;

    /**
     * rap2��Ŀid
     */
    private String rap2ProjectId;

    /**
     * eolink��ĿhashKey
     */
    private String eolinkProjectId;

    /**
     * showdoc��Ŀid
     */
    private String showdocProjectId;

    /**
     * apifox��Ŀid
     */
    private String apifoxProjectId;

    /**
     * YApi�����ַ: ����ͳһ��¼����
     */
    private String yapiUrl;

    /**
     * YApi��Ŀtoken: ����ͳһ��¼����
     */
    private String yapiProjectToken;

    /**
     * ����ֵ��װ��
     */
    private String returnWrapType;

    /**
     * ����ֵ���װ��
     */
    private List<String> returnUnwrapTypes;

    /**
     * ����������
     */
    private List<String> parameterIgnoreTypes;

    /**
     * �Զ���bean����
     */
    private Map<String, BeanCustom> beans;

    /**
     * ����mock����
     */
    private List<MockRule> mockRules;

    /**
     * �Զ���ע��ֵ����@RequestBodyע��
     */
    private RequestBodyParamType requestBodyParamType;

    /**
     * ʱ���ʽ: ��ѯ�����ͱ�
     */
    private String dateTimeFormatMvc;

    /**
     * ʱ���ʽ: Json
     */
    private String dateTimeFormatJson;

    /**
     * ���ڸ�ʽ
     */
    private String dateFormat;

    /**
     * ʱ���ʽ
     */
    private String timeFormat;

    /**
     * apiPost��Ŀid
     */
    private String apiPostProjectId;

    private static final Pattern BEANS_PATTERN = Pattern.compile("^beans\\[(.+)]$");

    @Data
    public static class RequestBodyParamType {
        /**
         * ע������
         */
        private String annotation;

        /**
         * ע������
         */
        private String property;

        public RequestBodyParamType(String type) {
            String[] splits = type.split("#");
            this.annotation = splits[0];
            if (splits.length > 1) {
                this.property = splits[1];
            } else {
                this.property = "value";
            }
        }
    }

    /**
     * ��������
     */
    public static ApiPostConfig fromProperties(Properties properties) {
        Splitter splitter = Splitter.on(",").trimResults().omitEmptyStrings();
        String strict = properties.getProperty("strict", "");
        String path = properties.getProperty("path", null);
        String yapiProjectId = properties.getProperty("yapiProjectId", "");
        String yapiUrl = properties.getProperty("yapiUrl", "");
        String yapiProjectToken = properties.getProperty("yapiProjectToken", "");
        String rap2ProjectId = properties.getProperty("rap2ProjectId", "");
        String eolinkProjectId = properties.getProperty("eolinkerProjectId", "");
        String showdocProjectId = properties.getProperty("showdocProjectId", "");
        String apifoxProjectId = properties.getProperty("apifoxProjectId", "");
        String returnWrapType = properties.getProperty("returnWrapType", "");
        String returnUnwrapTypes = properties.getProperty("returnUnwrapTypes", "");
        String parameterIgnoreTypes = properties.getProperty("parameterIgnoreTypes", "");
        String mockRules = properties.getProperty("mockRules");
        String dateTimeFormatMvc = properties.getProperty("dateTimeFormatMvc", "");
        String dateTimeFormatJson = properties.getProperty("dateTimeFormatJson", "");
        String dateFormat = properties.getProperty("dateFormat", "");
        String timeFormat = properties.getProperty("timeFormat", "");
        String requestBodyParamType = properties.getProperty("requestBodyParamType", "");

        ApiPostConfig config = new ApiPostConfig();
        if (StringUtils.isNotEmpty(strict)) {
            config.strict = Boolean.parseBoolean(strict);
        }
        config.setPath(path);
        config.yapiUrl = yapiUrl.trim();
        config.yapiProjectToken = yapiProjectToken.trim();
        config.yapiProjectId = yapiProjectId.trim();
        config.rap2ProjectId = rap2ProjectId.trim();
        config.eolinkProjectId = eolinkProjectId.trim();
        config.showdocProjectId = showdocProjectId.trim();
        config.apifoxProjectId = apifoxProjectId.trim();
        config.returnWrapType = returnWrapType.trim();
        config.returnUnwrapTypes = splitter.splitToList(returnUnwrapTypes);
        config.parameterIgnoreTypes = splitter.splitToList(parameterIgnoreTypes);
        config.dateTimeFormatMvc = dateTimeFormatMvc;
        config.dateTimeFormatJson = dateTimeFormatJson;
        config.dateFormat = dateFormat;
        config.timeFormat = timeFormat;
        if (StringUtils.isNotEmpty(requestBodyParamType)) {
            config.requestBodyParamType = new RequestBodyParamType(requestBodyParamType);
        }

        // �����Զ���bean����: beans[xxx].json=xxx
        Gson gson = new Gson();
        Map<String, BeanCustom> beans = Maps.newHashMap();
        config.setBeans(beans);
        for (String p : properties.stringPropertyNames()) {
            String propertyValue = properties.getProperty(p);
            if (StringUtils.isEmpty(propertyValue)) {
                continue;
            }
            Matcher matcher = BEANS_PATTERN.matcher(p);
            if (!matcher.matches()) {
                continue;
            }
            String beanType = matcher.group(1);
            BeanCustom beanCustom = gson.fromJson(propertyValue, BeanCustom.class);
            beans.put(beanType, beanCustom);
        }

        // ����mock����
        if (StringUtils.isNotEmpty(mockRules)) {
            Type type = new TypeToken<List<MockRule>>() {
            }.getType();
            config.mockRules = gson.fromJson(mockRules, type);
        }
        return config;
    }

    /**
     * �ϲ�����
     */
    public static ApiPostConfig getMergedInternalConfig(ApiPostConfig settings, File fileCache) {
        Properties customProperties = PropertiesLoader.getProperties(fileCache);
        Properties defaultProperties = PropertiesLoader.getProperties(DEFAULT_PROPERTY_FILE);
        ApiPostConfig internal = ApiPostConfig.fromProperties(defaultProperties);
        // �Զ�������
        ApiPostSettings customSetting = ApiPostSettings.getInstance();

        ApiPostConfig config = new ApiPostConfig();
        config.setStrict(settings.isStrict());
        config.setPath(settings.getPath());
        config.setYapiUrl(settings.getYapiUrl());
        config.setYapiProjectId(settings.getYapiProjectId());
        config.setYapiProjectToken(settings.getYapiProjectToken());
        config.setRap2ProjectId(settings.getRap2ProjectId());
        config.setEolinkProjectId(settings.getEolinkProjectId());
        config.setShowdocProjectId(settings.getShowdocProjectId());
        config.setApifoxProjectId(settings.getApifoxProjectId());
        config.setReturnWrapType(settings.getReturnWrapType());
        config.setDateTimeFormatMvc(settings.getDateTimeFormatMvc());
        config.setDateTimeFormatJson(settings.getDateTimeFormatJson());
        config.setDateFormat(settings.getDateFormat());
        config.setTimeFormat(settings.getTimeFormat());
        config.setRequestBodyParamType(settings.getRequestBodyParamType());
        config.setApiPostProjectId(customProperties.getProperty(API_POST_PROJECT_ID_PREFIX, ""));
        if (StringUtils.isNotBlank(customSetting.getProjectId())) {
            config.setApiPostProjectId(customSetting.getProjectId());
            try {
                FileUtilsExt.writeText(fileCache, API_POST_PROJECT_ID_PREFIX + "=" + customSetting.getProjectId());
            } catch (IOException e) {
                NotificationUtils.notifyError("apipost", "����д��ʧ��");
            }
        }

        // ʱ���ʽ
        if (StringUtils.isBlank(settings.getDateTimeFormatMvc())) {
            config.setDateTimeFormatMvc(internal.getDateTimeFormatMvc());
        }
        if (StringUtils.isBlank(settings.getDateTimeFormatJson())) {
            config.setDateTimeFormatJson(internal.getDateTimeFormatJson());
        }
        if (StringUtils.isBlank(settings.getDateFormat())) {
            config.setDateFormat(internal.getDateFormat());
        }
        if (StringUtils.isBlank(settings.getTimeFormat())) {
            config.setTimeFormat(internal.getTimeFormat());
        }

        // ���װ����
        List<String> returnUnwrapTypes = Lists.newArrayList();
        returnUnwrapTypes.addAll(internal.getReturnUnwrapTypes());
        if (settings.getReturnUnwrapTypes() != null) {
            returnUnwrapTypes.addAll(settings.getReturnUnwrapTypes());
        }
        config.setReturnUnwrapTypes(returnUnwrapTypes);

        // ���Բ�������
        List<String> parameterIgnoreTypes = Lists.newArrayList();
        if (settings.getParameterIgnoreTypes() != null) {
            config.setReturnUnwrapTypes(returnUnwrapTypes);
            parameterIgnoreTypes.addAll(settings.getParameterIgnoreTypes());
        }
        parameterIgnoreTypes.addAll(internal.getParameterIgnoreTypes());
        config.setParameterIgnoreTypes(parameterIgnoreTypes);

        // �Զ���bean����
        Map<String, BeanCustom> beans = Maps.newHashMap();
        if (internal.getBeans() != null) {
            beans.putAll(internal.getBeans());
        }
        if (settings.getBeans() != null) {
            beans.putAll(settings.getBeans());
        }
        config.setBeans(beans);

        // mock����
        List<MockRule> mockRules = Lists.newArrayList();
        if (settings.getMockRules() != null) {
            mockRules.addAll(settings.getMockRules());
        }
        if (internal.getMockRules() != null) {
            mockRules.addAll(internal.getMockRules());
        }
        config.setMockRules(mockRules);
        return config;
    }

    public BeanCustom getBeanCustomSettings(String type) {
        BeanCustom custom = null;
        if (this.beans != null) {
            custom = this.beans.get(type);
        }
        if (custom != null) {
            if (custom.getIncludes() == null) {
                custom.setIncludes(Collections.emptyNavigableSet());
            }
            if (custom.getExcludes() == null) {
                custom.setExcludes(Collections.emptyNavigableSet());
            }
            if (custom.getFields() == null) {
                custom.setFields(Maps.newHashMapWithExpectedSize(0));
            }
        }
        return custom;
    }

}
