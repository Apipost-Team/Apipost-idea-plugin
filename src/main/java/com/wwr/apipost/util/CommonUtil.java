package com.wwr.apipost.util;

import cn.hutool.core.net.Ipv4Util;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.wwr.apipost.handle.apipost.config.ApiPostSettings;
import lombok.experimental.UtilityClass;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * @author linyuan
 * @since 2023/06/07 8:51
 */
@UtilityClass
public class CommonUtil {

    private static final String SERVER_DEFAULT_PORT = "8080";

    public static String getServerPerUrl(Module module, ApiPostSettings settings) {
        String preMapUrl = Optional.ofNullable(settings.getPreMapUrl()).orElse("");
        String moduleName = module.getName();

        String regex = moduleName + "=[^\\r\\n\\s]+";
        String preUrl = ReUtil.get(regex, preMapUrl, 0);
        if (StrUtil.isNotBlank(preUrl)) {
            return preUrl.split("=")[1];
        }
        String defaultPreUrl = getServerIp() + ":" + getServerPort(module);
        if (!preMapUrl.endsWith("\r\n")) {
            preMapUrl += "\r\n";
        }
        settings.setPreMapUrl(preMapUrl + moduleName + "=" + defaultPreUrl);
        return defaultPreUrl;
    }

    /**
     * 获取服务端口号
     */
    public static String getServerPort(Module module) {
        String port = null;
        VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        if (contentRoots.length != 0) {
            String projectRootPath = contentRoots[0].getPath();

            File propertiesFile = new File(projectRootPath, "src/main/resources/application.properties");
            if (propertiesFile.exists()) {
                port = getPropertiesPort(propertiesFile);
            }
            File ymlFile = new File(projectRootPath, "src/main/resources/application.yml");
            if (ymlFile.exists()) {
                port = getYmlPort(ymlFile);
            }
        }
        return port == null ? SERVER_DEFAULT_PORT : port;
    }


    public static String getServerIp() {
        String prefix = "http://";
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return prefix + localHost.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prefix + Ipv4Util.LOCAL_IP;
    }


    private String getPropertiesPort(File propertiesFile) {
        try (FileInputStream inputStream = new FileInputStream(propertiesFile)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.getProperty("server.port");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getYmlPort(File ymlFile) {
        try (InputStream inputStream = Files.newInputStream(ymlFile.toPath())) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(inputStream);
            JSONObject server = JSONUtil.parseObj(data).getJSONObject("server");
            if (server != null) {
                return server.getStr("port");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
