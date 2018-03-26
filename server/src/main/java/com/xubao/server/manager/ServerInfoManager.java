package com.xubao.server.manager;

import com.xubao.comment.processorUtil.ProcessorCollector;
import com.xubao.comment.processorUtil.ProcessorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 * @Author xubao
 * @Date 2018/3/25
 */
public final class ServerInfoManager {
    private static Logger log = LoggerFactory.getLogger(ServerInfoManager.class);

    private static ServerInfoManager serverInfoManager = new ServerInfoManager();
    public static ServerInfoManager getInstance(){
        return serverInfoManager;
    }

    public ProcessorProvider processorProvider;

    private ServerInfoManager(){
        log.debug("初始化开始");
        processorProvider = ProcessorCollector.collectProcessorsFromPackage("com.xubao.server.connection.processor");
        log.debug("初始化结束");
    }

    //截取屏幕范围
    public Rectangle shotArea;
    //缓存大小
    public int shotCacheSize = 30;
    //缓存间隔
    public int shotInterval = 40;

    static{
        Dimension   screensize   =   Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)screensize.getWidth();
        int height = (int)screensize.getHeight();
        log.debug("屏幕:width={},height={}",width,height);
        serverInfoManager.shotArea = new Rectangle(width,height);


    }

    public static void main(String[] args){
        ServerInfoManager.getInstance();
        Toolkit.getDefaultToolkit().beep();
    }
}
