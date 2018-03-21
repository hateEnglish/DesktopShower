package com.xubao.client.manager;


import com.xubao.client.multicastReceive.MulticastReceive;

/**
 * @Author xubao
 * @Date 2018/3/20
 */
public class InfoManager {
    private static InfoManager infoManager = new InfoManager();
    public static InfoManager getInstance(){
        return infoManager;
    }

    private InfoManager(){
    }

    private String nickName = "-----";
    private ConnServerState connServerState;

    public enum ConnServerState{
        CONNECTING, //正在连接
        CONNECTED,  //已连接
        DISCONNECT,;//已经断开连接
    }

    private MulticastReceive multicastReceive;

    public MulticastReceive getMulticastReceive() {
        return multicastReceive;
    }

    public void setMulticastReceive(MulticastReceive multicastReceive) {
        this.multicastReceive = multicastReceive;
    }

    public ConnServerState getConnServerState() {
        return connServerState;
    }

    public void setConnServerState(ConnServerState connServerState) {
        this.connServerState = connServerState;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
