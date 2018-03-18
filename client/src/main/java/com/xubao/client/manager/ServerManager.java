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

    public static ServerManager getInstance() {
        return serverManager;
    }

    private List<ServerInfo> serverInfoList = new ArrayList<>();
    private List<ServerInfo> newAddServer = new ArrayList<>();

    private int serverTimeout = CommentConfig.getInstance().getProperInt("client.broadcast_timeout");

    public void addServerInfo(ServerInfo serverInfo) {

        if (serverInfoList.contains(serverInfo)) {
            serverInfoList.remove(serverInfo);
            serverInfoList.add(serverInfo);
        } else {
            serverInfoList.add(serverInfo);
            newAddServer.add(serverInfo);
        }

    }

    public List<Integer> getAndRemoveTimeOutServerIndex() {
        Iterator<ServerInfo> iterator = serverInfoList.iterator();
        List<Integer> removeServerIndexs = new ArrayList();
        int i = 0;
        for (; iterator.hasNext(); ) {
            if (iterator.next().getLastReceiveTime() + serverTimeout < System.currentTimeMillis()) {
                removeServerIndexs.add(i);
                iterator.remove();
            }
            i++;
        }

        return removeServerIndexs.size() == 0 ? null : removeServerIndexs;
    }

    public List<ServerInfo> getNewAddServer() {
        if (newAddServer.size() == 0)
            return null;

        List<ServerInfo> temp = newAddServer;
        newAddServer = new ArrayList<>();
        return temp;
    }

    public List<ServerInfo> getServerInfoList() {
        return serverInfoList;
    }
}
