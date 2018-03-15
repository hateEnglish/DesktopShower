package com.xubao.client.pojo;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

/**
 * @Author xubao
 * @Date 2018/2/20
 */
public class ServerInfo {
    private String connAddress;
    private String multicastAddress;
    private String nickName;
    private String comment;
    private String driverName;
    private ListViewCell listCell;


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

    public ListViewCell getListCell() {
        return listCell;
    }

    public void setListCell(ListViewCell listCell) {
        this.listCell = listCell;
    }

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
        private HBox hBox;
        private Label ipAddress;
        private Label multicastAddress;
        private Label comment;
        private Label driver;

        @Override
        protected void updateItem(ServerInfo serverInfo, boolean empty) {
            int prefWidth = 118;
            if (serverInfo == null) {
                return;
            }
            this.serverInfo = serverInfo;

            serverInfo.setListCell(this);

            if (this.hBox == null) {
                this.hBox = new HBox();
                this.hBox.setPrefHeight(30);
                this.ipAddress = new Label(serverInfo.connAddress);
                this.ipAddress.setAlignment(Pos.CENTER);
                this.ipAddress.setPrefWidth(prefWidth);
                this.multicastAddress = new Label(serverInfo.multicastAddress);
                this.multicastAddress.setPrefWidth(prefWidth);
                this.multicastAddress.setAlignment(Pos.CENTER);
                this.comment = new Label(serverInfo.comment);
                this.comment.setPrefWidth(prefWidth);
                this.comment.setAlignment(Pos.CENTER);
                this.driver = new Label(serverInfo.driverName);
                this.driver.setPrefWidth(prefWidth);
                this.driver.setAlignment(Pos.CENTER);

                this.hBox.getChildren().addAll(this.ipAddress, this.multicastAddress, this.comment, this.driver);
            } else {
                this.ipAddress.setText(serverInfo.connAddress);
                this.multicastAddress.setText(serverInfo.multicastAddress);
                this.comment.setText(serverInfo.comment);
                this.driver.setText(serverInfo.driverName);
            }


            setGraphic(this.hBox);
            super.updateItem(serverInfo, empty);
        }

        @Override
        public void updateSelected(boolean selected) {

            System.out.println("update------------------------selected=" + selected);
            super.updateSelected(selected);
        }


        public ServerInfo getServerInfo() {
            return serverInfo;
        }

        public Label getIpAddress() {
            return ipAddress;
        }

        public Label getMulticastAddress() {
            return multicastAddress;
        }

        public Label getComment() {
            return comment;
        }

        public Label getDriver() {
            return driver;
        }
    }
}