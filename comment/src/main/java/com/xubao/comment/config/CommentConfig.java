package com.xubao.comment.config;

/**
 * @Author xubao
 * @Date 2018/2/9
 */
public final class CommentConfig extends Config{
    static{
        commentConfig = ConfigLoad.getConfig("comment_setting.properties");
    }

    public static void main(String[] args){
        System.out.println(CommentConfig.getProper("server.conn_port"));
    }

}
