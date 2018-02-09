package com.xubao.comment.config;

import java.io.*;
import java.util.Properties;

/**
 * @Author xubao
 * @Date 2018/2/9
 */
public final class CommentConfig {
    private CommentConfig(){}
    private static Config commentConfig = Config.getConfig("comment_setting.properties");

    public static String getProper(String properName){
        return commentConfig.getProper(properName);
    }

    public static void main(String[] args){
        System.out.println(CommentConfig.getProper("server.conn_port"));
    }

}
