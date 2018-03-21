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

    public int heartbeatTimeout = CommentConfig.getInstance().getProperInt("server.heartbeat_timeout");

    private List<ClientInfo> watchingClient = new CopyOnWriteArrayList<>();

    private List<ClientInfo> newAddClient = new ArrayList<>();


    public ClientInfo findClientByAddress(String address) {
        for (ClientInfo client : watchingClient) {
            if (client.getAddress().equals(address)) {
                return client;
            }
        }
        return null;
    }

    public ClientInfo delClientByAddress(String address) {
        ClientInfo client = findClientByAddress(address);
        watchingClient.remove(client);
        return client;
    }

    public void addClient(ClientInfo client) {
        if (watchingClient.contains(client)) {
            watchingClient.remove(client);
            watchingClient.add(client);
        } else {
            watchingClient.add(client);
            newAddClient.add(client);
        }
    }


    public List<ClientInfo> getNewAddClients() {
        List<ClientInfo> temp = newAddClient;
        newAddClient = new ArrayList<>();
        return temp;
    }

    public List<Integer> removeHeartbeatTimeoutClientReturnIndexs() {
        List<Integer> indexs = new ArrayList<>(watchingClient.size());

        int i = 0;
        for (ClientInfo client : watchingClient) {
            if (System.currentTimeMillis() - client.getLastHeartBeatTime() > heartbeatTimeout) {
                watchingClient.remove(client);
                indexs.add(i);
            }
            i++;
        }

        return indexs.size() == 0 ? null : indexs;
    }

    public void removeAllClient() {
        watchingClient.clear();
    }

//    public ObservableList<HBox> getClientListItems(){
//        List<HBox> items = new ArrayList<>();
//        for(ClientInfo clientInfo:watchingClient){
//            items.add(clientInfo.getListItem());
//        }
//
//        ObservableList<HBox> hBoxes = FXCollections.observableArrayList(items);
//        return hBoxes;
//    }
}
