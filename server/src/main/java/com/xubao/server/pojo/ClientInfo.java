package com.xubao.server.pojo;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

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

    private int watchDelay;
    private int sendDelay;
    private Quality sendQuality;

    private HBox listItem;

    public enum Quality{
        HEIGHT("高"),
        MIDDLE("中等"),
        LOWER("低"),
        ;

        private String name;

        Quality(String name){
            this.name = name;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }

    public HBox getListItem(){
        if(listItem!=null){
            return listItem;
        }
        listItem = new HBox();

        int prefWidth = 100;

        Label name = new Label(nickName);
        name.setPrefWidth(prefWidth);
        Label ip = new Label(address.toString());
        ip.setPrefWidth(prefWidth);
        Label wDelay = new Label(watchDelay+"ms");
        wDelay.setPrefWidth(prefWidth);
        Label sDelay = new Label(sendDelay+"ms");
        sDelay.setPrefWidth(prefWidth);
        Label quality = new Label(sendQuality.toString());
        quality.setPrefWidth(prefWidth);

        listItem.getChildren().addAll(name,ip,wDelay,sDelay,quality);

        return listItem;
    }




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

    public int getWatchDelay()
    {
        return watchDelay;
    }

    public void setWatchDelay(int watchDelay)
    {
        this.watchDelay = watchDelay;
    }

    public int getSendDelay()
    {
        return sendDelay;
    }

    public void setSendDelay(int sendDelay)
    {
        this.sendDelay = sendDelay;
    }

    public Quality getSendQuality()
    {
        return sendQuality;
    }

    public void setSendQuality(Quality sendQuality)
    {
        this.sendQuality = sendQuality;
    }

    @Override
    public String toString()
    {
        return "ClientInfo{" +
                "nickName='" + nickName + '\'' +
                ", address=" + address +
                ", beginWatchTime=" + beginWatchTime +
                ", lastHeartBeatTime=" + lastHeartBeatTime +
                ", watchDelay=" + watchDelay +
                ", sendDealy=" + sendDelay +
                ", sendQuality=" + sendQuality +
                '}';
    }
}
