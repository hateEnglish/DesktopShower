package com.xubao.comment.log;


/**
 * @Author xubao
 * @Date 2018/2/5
 */
public class Logger {

    public static void debug(Class<?> clazz,String msg,Object... args){
        org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(clazz);
        log.debug(String.format(msg,args));
    }

    public static void info(Class<?> clazz,String msg,Object... args){
        org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(clazz);
        log.info(String.format(msg,args));
    }

    public static void warn(Class<?> clazz,String msg,Object... args){
        org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(clazz);
        log.warn(String.format(msg,args));
    }

    public static void error(Class<?> clazz,String msg,Object... args){
        org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(clazz);
        log.error(String.format(msg,args));
    }
}
