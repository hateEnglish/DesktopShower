package com.xubao.comment.config;

/**
 * @Author xubao
 * @Date 2018/2/18
 */
public abstract class Config {
    protected ConfigLoad commentConfig;

    protected Config() {
    }

    public String getProper(String properName) {
        return commentConfig.getProper(properName);
    }

    public int getProperInt(String properName) {
        return Integer.parseInt(getProper(properName));
    }

    public boolean getProperBoolean(String properName) {
        return Boolean.parseBoolean(getProper(properName));
    }
}
