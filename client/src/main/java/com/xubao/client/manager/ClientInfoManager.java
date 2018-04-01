package com.xubao.client.manager;


import com.xubao.client.multicastReceive.MulticastReceive;
import com.xubao.client.pojo.ServerInfo;
import com.xubao.comment.processorUtil.ProcessorCollector;
import com.xubao.comment.processorUtil.ProcessorProvider;
import com.xubao.client.pojo.MousePointInfo;
import javafx.scene.paint.Color;

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

    public boolean connFullScreen = false;

    public enum ConnServerState{
        CONNECTING, //正在连接
        CONNECTED,  //已连接
        DISCONNECT,;//已经断开连接
    }

    private MulticastReceive multicastReceive;

    //真正连接的组播地址
    public boolean isNeedPwd = false;
    public String watchPwd = "";
    public String multicastAddress = "";

    //当前选中的serverInfo
    public ServerInfo serverInfo = null;

    //显示鼠标信息
    public MousePointInfo mousePointInfo;
    {
        mousePointInfo = new MousePointInfo();
        mousePointInfo.setColor(Color.RED);
        mousePointInfo.setRadius(20);
    }

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
