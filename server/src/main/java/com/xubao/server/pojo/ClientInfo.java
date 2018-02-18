package com.xubao.server.pojo;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @Author xubao
 * @Date 2018/2/17
 */
public class ClientInfo {
    private String nickName;
    private SocketAddress address;
    private long beginWatchTime;
    private long lastHeartBeatTime;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public void setAddress(SocketAddress address) {
        this.address = address;
    }

    public long getBeginWatchTime() {
        return beginWatchTime;
    }

    public void setBeginWatchTime(long beginWatchTime) {
        this.beginWatchTime = beginWatchTime;
    }

    public long getLastHeartBeatTime() {
        return lastHeartBeatTime;
    }

    public void setLastHeartBeatTime(long lastHeartBeatTime) {
        this.lastHeartBeatTime = lastHeartBeatTime;
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "nickName='" + nickName + '\'' +
                ", address=" + address +
                ", beginWatchTime=" + beginWatchTime +
                ", lastHeartBeatTime=" + lastHeartBeatTime +
                '}';
    }
}
