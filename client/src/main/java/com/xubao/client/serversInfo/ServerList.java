package com.xubao.client.serversInfo;

import com.xubao.client.pojo.ServerInfo;
import com.xubao.comment.config.CommentConfig;

import java.util.*;

/**
 * @Author xubao
 * @Date 2018/2/20
 */
public class ServerList {
    private static ServerList serverList = new ServerList();


    public static ServerList getInstance() {
        return serverList;
    }

    private Set<ServerInfo> serverInfoList = new HashSet<>();

    private int serverTimeout = CommentConfig.getInstance().getProperInt("client.broadcast_timeout");

    public void addServerInfo(ServerInfo serverInfo) {

        if(!serverInfoList.add(serverInfo)){
            serverInfoList.remove(serverInfo);
            serverInfoList.add(serverInfo);
        }
    }

    public void removeTimeOutServer() {
        Iterator<ServerInfo> iterator = serverInfoList.iterator();
        for (; iterator.hasNext(); ) {
            if (iterator.next().getLastReceiveTime() + serverTimeout < System.currentTimeMillis()) {
                iterator.remove();
            }
        }
    }
}
