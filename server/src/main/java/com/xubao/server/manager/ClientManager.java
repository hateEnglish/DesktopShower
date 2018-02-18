package com.xubao.server.manager;

import com.xubao.server.pojo.ClientInfo;

import java.net.SocketAddress;
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
}
