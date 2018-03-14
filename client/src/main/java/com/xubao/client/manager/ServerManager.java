package com.xubao.client.manager;

import com.xubao.client.pojo.ServerInfo;
import com.xubao.comment.config.CommentConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;

import java.util.*;

/**
 * @Author xubao
 * @Date 2018/3/3
 */
public class ServerManager {

    private static ServerManager serverManager = new ServerManager();

    public static ServerManager getInstance(){
        return serverManager;
    }

    private Set<ServerInfo> serverInfoList = new HashSet<>();

    private int serverTimeout = CommentConfig.getInstance().getProperInt("client.broadcast_timeout");

    public void addServerInfo(ServerInfo serverInfo) {
        System.out.println(System.currentTimeMillis()+"lp1");
        if(!serverInfoList.add(serverInfo)){
            serverInfoList.remove(serverInfo);
            serverInfoList.add(serverInfo);
        }
        System.out.println(System.currentTimeMillis()+"lp");
    }

    public void removeTimeOutServer() {
        Iterator<ServerInfo> iterator = serverInfoList.iterator();
        for (; iterator.hasNext(); ) {
            if (iterator.next().getLastReceiveTime() + serverTimeout < System.currentTimeMillis()) {
                iterator.remove();
            }
        }
    }


    public Set<ServerInfo> getServerInfoList() {
        return serverInfoList;
    }
}
