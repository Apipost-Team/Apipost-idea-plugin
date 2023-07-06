package com.wwr.apipost.util;

import cn.hutool.core.net.Ipv4Util;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.wwr.apipost.config.domain.PrefixUrl;
import com.wwr.apipost.handle.apipost.config.ApiPostSettings;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author linyuan
 * @since 2023/06/07 8:51
 */
@UtilityClass
public class CommonUtil {

    private static final String SERVER_DEFAULT_PORT = "8080";

    public static String getServerPerUrl(Module module, ApiPostSettings settings) {
        List<PrefixUrl> prefixUrlList = settings.getPrefixUrlList();
        if (prefixUrlList != null && !prefixUrlList.isEmpty()) {
            Map<String, String> nameAndUrlMap = prefixUrlList.stream().collect(Collectors.toMap(PrefixUrl::getModuleName, PrefixUrl::getPrefixUrl));
            if(nameAndUrlMap.size()==1){
                return (String)nameAndUrlMap.values().toArray()[0];
            }
            return nameAndUrlMap.get(module.getName());
        }
        return null;
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

    public static String getPrefixUrl(Module module,ApiPostSettings setting) throws Exception {
        //获取模块访问路径,默认 bootstrap.yml的优先级大于application.yml的优先级大于application.properties
        String contentPath="";
        Integer port = null;
        VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        if (contentRoots.length != 0) {
            String projectRootPath = contentRoots[0].getPath();
            File file;
            if(StringUtils.isNotBlank(setting.getProfile())){
                file=new File(projectRootPath, "src/main/resources/bootstrap-"+setting.getProfile()+".yml");
                if(!file.exists()){
                    file=new File(projectRootPath, "src/main/resources/application-"+setting.getProfile()+".yml");
                    if(!file.exists()){
                        file=new File(projectRootPath, "src/main/resources/application-"+setting.getProfile()+".properties");
                    }
                }
            }else{
                file=new File(projectRootPath, "src/main/resources/bootstrap.yml");
                if(!file.exists()){
                    file=new File(projectRootPath, "src/main/resources/application.yml");
                    if(!file.exists()){
                        file=new File(projectRootPath, "src/main/resources/application.properties");
                    }
                }
            }
            if(file.getName().endsWith(".properties")){
                FileInputStream inputStream = new FileInputStream(file);
                Properties properties = new Properties();
                properties.load(inputStream);
                contentPath=properties.getProperty("server.servlet.context-path");
                String property = properties.getProperty("server.port");
                if(Objects.nonNull(property)){
                    port= Integer.valueOf(property);
                }
            }
            if(file.getName().endsWith(".yml")){
                InputStream inputStream = Files.newInputStream(file.toPath());
                Yaml yaml = new Yaml();
                Map<String, Object> data = yaml.load(inputStream);
                JSONObject server = JSONUtil.parseObj(data).getJSONObject("spring");
                if(null!=setting.getProfile() && !"".equals(setting.getProfile())){
                    server = JSONUtil.parseObj(data).getJSONObject("server");
                }else{
                    server=(JSONObject) server.get("server");
                }
                if(null!=server){
                    String port1 = server.getStr("port");
                    if(Objects.nonNull(port1)){
                        port= Integer.valueOf(port1);
                    }
                    server = (JSONObject) server.get("servlet");
                    if (server != null) {
                        contentPath=server.getStr("context-path");
                    }
                }
            }
        }
        contentPath = StringUtils.isEmpty(contentPath)?"":contentPath;
        return getServerIp()+":"+(port == null ? SERVER_DEFAULT_PORT : port)+contentPath;
    }

}
