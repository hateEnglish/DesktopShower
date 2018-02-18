package com.xubao.comment.config;

import com.xubao.comment.log.Logger;

import java.io.InputStream;
import java.util.*;

/**
 * @Author xubao
 * @Date 2018/2/9
 */
public final class ConfigLoad {

    private ConfigLoad() {
    }

    protected static Map<String, ConfigLoad> configs = new HashMap<>();

    protected Properties properties;

    public static ConfigLoad getConfig(String fileName) {
        return configs.get(fileName) == null ? loadConfig(fileName) : configs.get(fileName);
    }

    public String getProper(String properName, String defaultValue) {
        return properties.getProperty(properName, defaultValue);
    }

    public String getProper(String properName) {
        return getProper(properName, null);
    }

    protected static ConfigLoad loadConfig(String fileName) {
        InputStream is;
        ConfigLoad config = null;
        try {
            is = ConfigLoad.class.getClassLoader().getResourceAsStream(fileName);
            config = new ConfigLoad();
            config.properties = new Properties();
            config.properties.load(is);
            is.close();

            configs.put(fileName, config);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(ConfigLoad.class, "加载配置文件%s错误", fileName);
            System.exit(0);
        }

        return config;
    }

}
