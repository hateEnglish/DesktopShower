package com.xubao.client.pojo;

import com.xubao.comment.config.CommentConfig;

import java.net.SocketAddress;

/**
 * @Author xubao
 * @Date 2018/2/20
 */
public class ServerInfo {
    private SocketAddress address;
    private int connPort;
    private String nickName;
    private String commend;

    private long lastReceiveTime;

    public SocketAddress getAddress() {
        return address;
    }

    public void setAddress(SocketAddress address) {
        this.address = address;
    }

    public int getConnPort() {
        return connPort;
    }

    public long getLastReceiveTime() {
        return lastReceiveTime;
    }

    public void setLastReceiveTime(long lastReceiveTime) {
        this.lastReceiveTime = lastReceiveTime;
    }

    public void setConnPort(int connPort) {
        this.connPort = connPort;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCommend() {
        return commend;
    }

    public void setCommend(String commend) {
        this.commend = commend;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerInfo that = (ServerInfo) o;

        if (connPort != that.connPort) return false;
        return address != null ? address.equals(that.address) : that.address == null;
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + connPort;
        return result;
    }
}