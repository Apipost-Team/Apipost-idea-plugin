package com.wwr.apipost.config;

import com.google.common.base.Splitter;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.wwr.apipost.config.domain.MockRule;
import com.wwr.apipost.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static com.wwr.apipost.config.DefaultConstants.DEFAULT_PROPERTY_FILE_CACHE;

/**
 * 配置处理工具类.
 * <p>
 * 备注: 配置文件读取使用.ypix文件
 */
public final class ApiPostConfigUtils {

    private ApiPostConfigUtils() {
    }

    /**
     * 查找配置文件
     */
    public static VirtualFile findConfigFile(Project project, Module module) {
        VirtualFile yapiConfigFile = null;
        if (module != null) {
            VirtualFile[] moduleContentRoots = Optional.ofNullable(ModuleRootManager.getInstance(module).getContentRoots()).orElse(new VirtualFile[0]);
            for (VirtualFile moduleContentRoot : moduleContentRoots) {
                yapiConfigFile = moduleContentRoot.findFileByRelativePath(".idea/" + DEFAULT_PROPERTY_FILE_CACHE);
                if (yapiConfigFile != null && yapiConfigFile.exists()) {
                    break;
                }
            }
        }
        if ((yapiConfigFile == null || !yapiConfigFile.exists()) && project != null) {
            VirtualFile[] projectContentRoots = Optional.ofNullable(ProjectRootManager.getInstance(project).getContentRoots()).orElse(new VirtualFile[0]);
            for (VirtualFile projectContentRoot : projectContentRoots) {
                yapiConfigFile = projectContentRoot.findFileByRelativePath(".idea/" + DEFAULT_PROPERTY_FILE_CACHE);
                if (yapiConfigFile != null && yapiConfigFile.exists()) {
                    break;
                }
            }
        }
        return yapiConfigFile;
    }

    public static ApiPostConfig readConfig(VirtualFile vf) throws IOException {
        String content = new String(vf.contentsToByteArray(), StandardCharsets.UTF_8);
        Properties properties = new Properties();
        properties.load(new StringReader(content));
        return ApiPostConfig.fromProperties(properties);
    }


    /**
     * 读取配置(xml)
     */
    public static ApiPostConfig readFromXml(String xml, String moduleName)
            throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        Element root = doc.getDocumentElement();
        String rootName = root.getNodeName();
        if ("project".equals(rootName)) {
            return doReadXmlYapiProjectConfigByOldVersion(doc);
        } else {
            NodeList nodes = root.getChildNodes();
            ApiPostConfig rootConfig = doReadXmlYapiProjectConfigByNodeList(nodes);

            if (StringUtils.isNotEmpty(moduleName)) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    if (!"project".equals(node.getNodeName())) {
                        continue;
                    }
                    NamedNodeMap attributes = node.getAttributes();
                    String projectTagName = attributes.getNamedItem("name").getNodeValue();
                    if (moduleName.equalsIgnoreCase(projectTagName)) {
                        ApiPostConfig moduleConfig = doReadXmlYapiProjectConfigByNodeList(node.getChildNodes());
                        mergeToFirst(rootConfig, moduleConfig);
                        break;
                    }
                }
            }
            return rootConfig;
        }
    }

    private static ApiPostConfig doReadXmlYapiProjectConfigByOldVersion(Document doc) {
        ApiPostConfig config = new ApiPostConfig();

        Splitter splitter = Splitter.on(",").trimResults().omitEmptyStrings();
        NodeList nodes = doc.getElementsByTagName("option");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String attributeName = node.getAttributes().getNamedItem("name").getNodeValue();
            String text = node.getTextContent().trim();
            if (StringUtils.isEmpty(text)) {
                continue;
            }

            switch (attributeName) {
                case "projectId":
                    config.setYapiProjectId(text);
                    break;
                case "returnClass":
                case "returnWrapType":
                    config.setReturnWrapType(text);
                    break;
                case "returnUnwrapTypes":
                    config.setReturnUnwrapTypes(splitter.splitToList(text));
                    break;
                case "parameterIgnoreTypes":
                    config.setParameterIgnoreTypes(splitter.splitToList(text));
                    break;
                case "mockRules":
                    Type type = new TypeToken<List<MockRule>>() {
                    }.getType();
                    List<MockRule> mockRules = JsonUtils.fromJson(text, type);
                    config.setMockRules(mockRules);
                    break;
                case "dateTimeFormatMvc":
                    config.setDateTimeFormatMvc(text);
                    break;
                case "dateTimeFormatJson":
                    config.setDateTimeFormatJson(text);
                    break;
            }
        }
        return config;
    }

    @NotNull
    private static ApiPostConfig doReadXmlYapiProjectConfigByNodeList(NodeList nodes) {
        ApiPostConfig config = new ApiPostConfig();
        Splitter splitter = Splitter.on(",").trimResults().omitEmptyStrings();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String text = node.getTextContent().trim();
            if (StringUtils.isEmpty(text)) {
                continue;
            }

            switch (node.getNodeName()) {
                case "projectId":
                    config.setYapiProjectId(text);
                    break;
                case "returnClass":
                case "returnWrapType":
                    config.setReturnWrapType(text);
                    break;
                case "returnUnwrapTypes":
                    config.setReturnUnwrapTypes(splitter.splitToList(text));
                    break;
                case "parameterIgnoreTypes":
                    config.setParameterIgnoreTypes(splitter.splitToList(text));
                    break;
                case "mockRules":
                    Type type = new TypeToken<List<MockRule>>() {
                    }.getType();
                    List<MockRule> mockRules = JsonUtils.fromJson(text, type);
                    config.setMockRules(mockRules);
                    break;
                case "dateTimeFormatMvc":
                    config.setDateTimeFormatMvc(text);
                    break;
                case "dateTimeFormatJson":
                    config.setDateTimeFormatJson(text);
                    break;
            }
        }
        return config;
    }

    /**
     * 配置合并.
     */
    public static void mergeToFirst(ApiPostConfig a, ApiPostConfig b) {
        if (b != null) {
            if (StringUtils.isNotEmpty(b.getYapiProjectId())) {
                a.setYapiProjectId(b.getYapiProjectId());
            }
            if (StringUtils.isNotEmpty(b.getReturnWrapType())) {
                a.setReturnWrapType(b.getReturnWrapType());
            }
        }

    }
}
