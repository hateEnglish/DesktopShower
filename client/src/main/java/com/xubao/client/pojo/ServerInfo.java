package com.xubao.client.pojo;

import javafx.collections.ObservableList;
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
    private String commend;
    private String driverName;

    private HBox hBox;

    private long lastReceiveTime;

    public ServerInfo(String connAddress, String multicastAddress, String commend, String driverName) {
        this.connAddress = connAddress;
        this.multicastAddress = multicastAddress;
        this.commend = commend;
        this.driverName = driverName;
    }

    public ServerInfo(){
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

    public String getCommend() {
        return commend;
    }

    public void setCommend(String commend) {
        this.commend = commend;
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

    public static class ListViewCell extends ListCell<ServerInfo>{
        @Override
        protected void updateItem(ServerInfo item, boolean empty) {
            int prefWidth = 118;
            if(item==null){
                return;
            }
            if(item.hBox ==null){
                item.hBox = new HBox();
                Label ip = new Label(item.connAddress);
                ip.setPrefWidth(prefWidth);
                Label multicast = new Label(item.multicastAddress);
                multicast.setPrefWidth(prefWidth);
                Label comment = new Label(item.commend);
                comment.setPrefWidth(prefWidth);
                Label driver = new Label(item.driverName);
                driver.setPrefWidth(prefWidth);

                item.hBox.getChildren().addAll(ip,multicast,comment,driver);
            }else{
                ObservableList<Node> children = item.hBox.getChildren();
                ((Label)children.get(0)).setText(item.connAddress);
                ((Label)children.get(1)).setText(item.multicastAddress);
                ((Label)children.get(2)).setText(item.commend);
                ((Label)children.get(3)).setText(item.driverName);
            }



            setGraphic(item.hBox);
        }

        @Override
        public void updateSelected(boolean selected) {
            super.updateSelected(selected);
        }
    }
}