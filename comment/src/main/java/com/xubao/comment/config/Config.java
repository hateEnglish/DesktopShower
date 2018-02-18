package com.xubao.comment.config;

/**
 * @Author xubao
 * @Date 2018/2/18
 */
public abstract class Config {
    protected static ConfigLoad commentConfig;

    protected Config(){}

    public static String getProper(String properName){
        return commentConfig.getProper(properName);
    }

    public static int getProperInt(String properName){
        return Integer.parseInt(getProper(properName));
    }

    public static boolean getProperBoolean(String properName){
        return Boolean.parseBoolean(getProper(properName));
    }
}
