package com.xubao.client.pojo;

import com.xubao.comment.config.CommentConfig;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.net.SocketAddress;

/**
 * @Author xubao
 * @Date 2018/2/20
 */
public class ServerInfo {
    private SocketAddress address;
    private SocketAddress multicastAddress;
    private int connPort;
    private String nickName;
    private String commend;
    private String driverName;

    private HBox listItem;

    private long lastReceiveTime;

    public ServerInfo(){}

    public ServerInfo(SocketAddress address, SocketAddress multicastAddress, String commend, String driverName)
    {
        this.address = address;
        this.multicastAddress = multicastAddress;
        this.commend = commend;
        this.driverName = driverName;
    }

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

    public SocketAddress getMulticastAddress()
    {
        return multicastAddress;
    }

    public void setMulticastAddress(SocketAddress multicastAddress)
    {
        this.multicastAddress = multicastAddress;
    }

    public String getDriverName()
    {
        return driverName;
    }

    public void setDriverName(String driverName)
    {
        this.driverName = driverName;
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


    public HBox getListItem(){
        if(listItem!=null){
            return listItem;
        }
        listItem = new HBox();

        int prefWidth = 118;

        Label ip = new Label(address.toString());
        ip.setPrefWidth(prefWidth);
        Label multicast = new Label(multicastAddress.toString());
        multicast.setPrefWidth(prefWidth);
        Label comment = new Label(commend);
        comment.setPrefWidth(prefWidth);
        Label driver = new Label(driverName);
        driver.setPrefWidth(prefWidth);

        listItem.getChildren().addAll(ip,multicast,comment,driver);

        return listItem;
    }
}