package com.xubao.comment.config;

import org.junit.Test;

/**
 * @Author xubao
 * @Date 2018/2/9
 */
public class ConfigTest {

    @Test
    public void configTest(){
        System.out.println(CommentConfig.getProper("server.conn_port"));
    }
}
