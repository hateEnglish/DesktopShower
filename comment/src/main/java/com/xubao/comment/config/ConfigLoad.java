package com.xubao.comment.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Author xubao
 * @Date 2018/2/9
 */
public final class ConfigLoad {

    private static Logger log = LoggerFactory.getLogger(ConfigLoad.class);

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
            log.error("加载配置文件{}错误", fileName);
            System.exit(0);
        }

        return config;
    }

}
