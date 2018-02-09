package com.xubao.comment.config;

import com.xubao.comment.log.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @Author xubao
 * @Date 2018/2/9
 */
public final class Config {

    private Config() {
    }

    protected static Map<String, Config> configs = new HashMap<>();

    protected Properties properties;

    public static Config getConfig(String fileName) {
        return configs.get(fileName) == null ? loadConfig(fileName) : configs.get(fileName);
    }

    public String getProper(String properName, String defaultValue) {
        return properties.getProperty(properName, defaultValue);
    }

    public String getProper(String properName) {
        return getProper(properName, null);
    }

    protected static Config loadConfig(String fileName) {
        InputStream is;
        Config config = null;
        try {
            is = Config.class.getClassLoader().getResourceAsStream(fileName);
            config = new Config();
            config.properties = new Properties();
            config.properties.load(is);

            configs.put(fileName, config);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(Config.class, "加载配置文件%s错误", fileName);
            System.exit(0);
        }

        return config;
    }

}
