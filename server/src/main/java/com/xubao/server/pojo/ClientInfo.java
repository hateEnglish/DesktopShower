package com.xubao.server.pojo;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author xubao
 * @Date 2018/2/17
 */
public class ClientInfo {
    private String nickName;
    private String address;
    private long beginWatchTime;
    private long lastHeartBeatTime;

    private int watchDelay;
    private int sendDelay;
    private Quality sendQuality;


    public ItemClickHandler getOnMouseClickHandler() {
        return onMouseClickHandler;
    }

    public void setOnMouseClickHandler(ItemClickHandler onMouseClickHandler) {
        this.onMouseClickHandler = onMouseClickHandler;
    }

    private ItemClickHandler onMouseClickHandler;

    //private HBox listItem;

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

//    public HBox getListItem(){
//        if(listItem!=null){
//            return listItem;
//        }
//        listItem = new HBox();
//
//        int prefWidth = 100;
//
//        Label name = new Label(nickName);
//        name.setPrefWidth(prefWidth);
//        Label ip = new Label(address.toString());
//        ip.setPrefWidth(prefWidth);
//        Label wDelay = new Label(watchDelay+"ms");
//        wDelay.setPrefWidth(prefWidth);
//        Label sDelay = new Label(sendDelay+"ms");
//        sDelay.setPrefWidth(prefWidth);
//        Label quality = new Label(sendQuality.toString());
//        quality.setPrefWidth(prefWidth);
//
//        listItem.getChildren().addAll(name,ip,wDelay,sDelay,quality);
//
//        return listItem;
//    }




    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientInfo that = (ClientInfo) o;

        return address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return address.hashCode();
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

    public static class ListViewCell extends ListCell<ClientInfo> {

        private ClientInfo clientInfo;

        @Override
        protected void updateItem(ClientInfo clientInfo, boolean empty) {
            super.updateItem(clientInfo, empty);
            int prefWidth = 118;
            if (clientInfo == null) {
                return;
            }
            this.clientInfo = clientInfo;

            HBox hBox = new HBox();
            //hBox.setOnMouseClicked(clientInfo.getOnMouseClickHandler());

            hBox.setPrefHeight(30);

            Label nickName = new Label(clientInfo.nickName);
            nickName.setPrefWidth(prefWidth);
            nickName.setAlignment(Pos.CENTER);

            String address = clientInfo.address.substring(1).substring(0,clientInfo.address.lastIndexOf(":")-1);
            Label ipAddress = new Label(address);
            ipAddress.setAlignment(Pos.CENTER);
            ipAddress.setPrefWidth(prefWidth-30);

            Date date = new Date(clientInfo.beginWatchTime);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Label beginWatchTime = new Label(simpleDateFormat.format(date));
            beginWatchTime.setPrefWidth(prefWidth-35);
            beginWatchTime.setAlignment(Pos.CENTER);

            Label sendQuality = new Label("高");
            sendQuality.setPrefWidth(prefWidth);
            sendQuality.setAlignment(Pos.CENTER);

            Label watchDelay = new Label(5+"");
            watchDelay.setPrefWidth(prefWidth-30);
            watchDelay.setAlignment(Pos.CENTER);

            hBox.getChildren().addAll(ipAddress, nickName, beginWatchTime,sendQuality, watchDelay);

            setGraphic(hBox);

        }

        @Override
        public void updateSelected(boolean selected) {

            //System.out.println("update------------------------selected=" + selected);
            super.updateSelected(selected);
        }

    }

    public static abstract class ItemClickHandler implements EventHandler<MouseEvent> {

        protected ClientInfo clientInfo;


        public ClientInfo getServerInfo() {
            return clientInfo;
        }

        public void setServerInfo(ClientInfo clientInfo) {
            this.clientInfo = clientInfo;
        }
    }
}
