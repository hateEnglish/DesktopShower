package com.xubao.client.manager;


import com.xubao.client.multicastReceive.MulticastReceive;
import com.xubao.comment.processorUtil.ProcessorCollector;
import com.xubao.comment.processorUtil.ProcessorProvider;

/**
 * @Author xubao
 * @Date 2018/3/20
 */
public class ClientInfoManager {
    private static ClientInfoManager clientInfoManager = new ClientInfoManager();
    public static ClientInfoManager getInstance(){
        return clientInfoManager;
    }

    private ClientInfoManager(){
    }

    public ProcessorProvider processorProvider = ProcessorCollector.collectProcessorsFromPackage("com.xubao.client.connection.processor");


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
