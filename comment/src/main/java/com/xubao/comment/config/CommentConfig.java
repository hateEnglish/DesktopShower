package com.xubao.comment.config;

/**
 * @Author xubao
 * @Date 2018/2/9
 */
public final class CommentConfig extends Config{

    private static Config config = new CommentConfig();

    public CommentConfig(){
        commentConfig = ConfigLoad.loadConfig("comment_setting.properties");
    }

    public static Config getInstance(){
        return config;
    }

    public static void main(String[] args){
        System.out.println(CommentConfig.getInstance().getProper("server.conn_port"));
    }

}
