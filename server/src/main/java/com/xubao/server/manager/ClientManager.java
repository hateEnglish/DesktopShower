package com.xubao.server.manager;

import com.xubao.comment.config.CommentConfig;
import com.xubao.server.pojo.ClientInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author xubao
 * @Date 2018/2/17
 */
public class ClientManager {
    private static ClientManager clientManager = new ClientManager();

    public static ClientManager getInstance() {
        return clientManager;
    }

    public int heartbeatTimeout= CommentConfig.getInstance().getProperInt("server.heartbeat_timeout");

    private CopyOnWriteArrayList<ClientInfo> watchingClient = new CopyOnWriteArrayList<>();

    public ClientInfo findClientByAddress(SocketAddress address) {
        for (ClientInfo client : watchingClient) {
            if (client.getAddress().equals(address)) {
                return client;
            }
        }
        return null;
    }

    public ClientInfo delClientByAddress(SocketAddress address){
        ClientInfo client = findClientByAddress(address);
        watchingClient.remove(client);
        return client;
    }

    public boolean addClient(ClientInfo client){
        return watchingClient.add(client);
    }

    public void removeHeartbeatTimeoutClient(){
        for(ClientInfo client:watchingClient){
            if(System.currentTimeMillis()-client.getLastHeartBeatTime()>heartbeatTimeout){
                watchingClient.remove(client);
            }
        }
    }

    public ObservableList<HBox> getClientListItems(){
        List<HBox> items = new ArrayList<>();
        for(ClientInfo clientInfo:watchingClient){
            items.add(clientInfo.getListItem());
        }

        ObservableList<HBox> hBoxes = FXCollections.observableArrayList(items);
        return hBoxes;
    }
}
