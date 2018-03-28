package com.xubao.client.pojo;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * @Author xubao
 * @Date 2018/2/20
 */
public class ServerInfo {
    public final static String PWD_REPLACE = "**********";

    private String connAddress;
    private String multicastAddress;
    private String nickName;
    private String comment;
    private String driverName;
    //private ListViewCell listCell;
    private boolean isNeedPwd;
    private String watchPwd;

    private ItemClickHandler onMouseClickHandler;

    private long lastReceiveTime;

    public ServerInfo(String connAddress, String multicastAddress, String comment, String driverName) {
        this.connAddress = connAddress;
        this.multicastAddress = multicastAddress;
        this.comment = comment;
        this.driverName = driverName;
        lastReceiveTime = System.currentTimeMillis();
    }

    public ServerInfo() {
        lastReceiveTime = System.currentTimeMillis();
    }

    public String getConnAddress() {
        return connAddress;
    }

    public void setConnAddress(String connAddress) {
        this.connAddress = connAddress;
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    public void setMulticastAddress(String multicastAddress) {
        this.multicastAddress = multicastAddress;
    }

    public int getMulticastPort() {
        return Integer.parseInt(multicastAddress.split(":")[0]);
    }


    public int getConnPort() {
        return Integer.parseInt(connAddress.split(":")[0]);
    }


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public long getLastReceiveTime() {
        return lastReceiveTime;
    }

    public void setLastReceiveTime(long lastReceiveTime) {
        this.lastReceiveTime = lastReceiveTime;
    }

    public boolean isNeedPwd() {
        return isNeedPwd;
    }

    public void setNeedPwd(boolean needPwd) {
        isNeedPwd = needPwd;
    }

    public String getWatchPwd() {
        return watchPwd;
    }

    public void setWatchPwd(String watchPwd) {
        this.watchPwd = watchPwd;
    }

    public ItemClickHandler getOnMouseClickHandler() {
        return onMouseClickHandler;
    }

    public void setOnMouseClickHandler(ItemClickHandler onMouseClickHandler) {
        this.onMouseClickHandler = onMouseClickHandler;
    }


//    public void setListCell(ListViewCell listCell) {
//        this.listCell = listCell;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerInfo that = (ServerInfo) o;

        return connAddress != null ? connAddress.equals(that.connAddress) : that.connAddress == null;
    }

    @Override
    public int hashCode() {
        int result = connAddress != null ? connAddress.hashCode() : 0;
        return result;
    }

    public static class ListViewCell extends ListCell<ServerInfo> {

        private ServerInfo serverInfo;
//        private HBox hBox;
//        private Label ipAddress;
//        private Label multicastAddress;
//        private Label comment;
//        private Label driver;


        @Override
        public void startEdit() {
            System.out.println("start-------------");
            super.startEdit();
        }

        @Override
        protected void updateItem(ServerInfo serverInfo, boolean empty) {
            super.updateItem(serverInfo, empty);
            int prefWidth = 118;
            if (serverInfo == null) {
                return;
            }
            this.serverInfo = serverInfo;

            //serverInfo.setListCell(this);

            HBox hBox = new HBox();
            hBox.setOnMouseClicked(serverInfo.getOnMouseClickHandler());

            hBox.setPrefHeight(30);
            Label ipAddress = new Label(serverInfo.connAddress);
            ipAddress.setAlignment(Pos.CENTER);
            ipAddress.setPrefWidth(prefWidth);
            Label multicastAddress = new Label(serverInfo.multicastAddress);
            if(serverInfo.isNeedPwd){
                multicastAddress.setText(PWD_REPLACE);
            }
            multicastAddress.setPrefWidth(prefWidth);
            multicastAddress.setAlignment(Pos.CENTER);
            Label comment = new Label(serverInfo.comment);
            comment.setPrefWidth(prefWidth);
            comment.setAlignment(Pos.CENTER);
            Label driver = new Label(serverInfo.driverName);
            driver.setPrefWidth(prefWidth);
            driver.setAlignment(Pos.CENTER);

            hBox.getChildren().addAll(ipAddress, multicastAddress, comment, driver);

            setGraphic(hBox);

        }

        @Override
        public void updateSelected(boolean selected) {

            System.out.println("update------------------------selected=" + selected);
            super.updateSelected(selected);
        }

    }

    public static abstract class ItemClickHandler implements EventHandler<MouseEvent> {

        protected ServerInfo serverInfo;


        public ServerInfo getServerInfo() {
            return serverInfo;
        }

        public void setServerInfo(ServerInfo serverInfo) {
            this.serverInfo = serverInfo;
        }
    }
}