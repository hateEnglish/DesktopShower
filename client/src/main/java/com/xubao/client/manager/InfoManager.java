package com.xubao.client.manager;

import com.xubao.comment.proto.Connection;

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
        initMsg();
    }

    public Connection.Register.Builder registerMsgBuilder = Connection.Register.newBuilder();

    public Connection.Heartbeat.Builder heartbeatMsgBuilder = Connection.Heartbeat.newBuilder();

    private void initMsg(){
        registerMsgBuilder.setNickName("无名");

        heartbeatMsgBuilder.setTime(System.currentTimeMillis());
        heartbeatMsgBuilder.setInfo("ok");
    }


}
