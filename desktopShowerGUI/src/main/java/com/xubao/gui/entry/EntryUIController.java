/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xubao.gui.entry;

import com.xubao.client.broadcastReceive.BroadcastReceive;
import com.xubao.client.connection.ConnServer;
import com.xubao.client.connection.MessageSender;
import com.xubao.client.manager.InfoManager;
import com.xubao.client.manager.ServerManager;
import com.xubao.client.multicastReceive.MulticastReceive;
import com.xubao.client.pojo.ServerInfo;
import com.xubao.comment.config.CommentConfig;
import com.xubao.gui.bootstarp.Bootstrap;
import com.xubao.gui.struct.controlStruct.AppKeeper;
import com.xubao.gui.struct.controlStruct.StageKey;
import com.xubao.gui.timeTask.MyTimer;
import com.xubao.server.base.ScreenShotManager;
import com.xubao.server.broadcast.Broadcast;
import com.xubao.server.connection.MessageDispose;
import com.xubao.server.manager.ClientManager;
import com.xubao.server.multicast.Multicast;
import com.xubao.server.pojo.ClientInfo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimerTask;

/**
 * FXML Controller class
 *
 * @author admin
 */
public class EntryUIController implements Initializable {

    //服务
    @FXML
    Button showDesktopBtu;        //分享桌面按钮
    @FXML
    ChoiceBox screenSizeSelect;    //
    @FXML
    ChoiceBox sendDelaySelect;
    @FXML
    ChoiceBox sendQualitySelect;
    @FXML
    CheckBox watchPasswordCheck;
    @FXML
    TextField watchPassword;
    @FXML
    HBox passwordContainer;
    @FXML
    ListView watcherListView;

    //连接
    @FXML
    TextField multicastAddress;
    @FXML
    TextField nickName;
    @FXML
    CheckBox fullScreenCheck;
    @FXML
    ListView serverListView;
    @FXML
    Button connectBut;


    String serverAddress;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {


        System.out.println(System.currentTimeMillis());
        // TODO
        ServerManager.getInstance().addServerInfo(new ServerInfo("127.0", "127.0", "4545", "8989"));

        System.out.println(System.currentTimeMillis());

        initShowScreenBtu();

        initServerListView();

        initSendDelaySelect();

        initConnectBut();

        initWatchListView();

        BroadcastReceive broadcastReceive = new BroadcastReceive();
        try {
            broadcastReceive.initServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //初始化观看列表
    public void initWatchListView() {

        watcherListView.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new ClientInfo.ListViewCell();
            }
        });

        MyTimer.addControlTask(watcherListView, new TimerTask() {
            @Override
            public void run() {
                List<Integer> removeIndexs = ClientManager.getInstance().removeHeartbeatTimeoutClientReturnIndexs();
                List<ClientInfo> newAddClients = ClientManager.getInstance().getNewAddClients();

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (newAddClients != null) {
                            ObservableList<ClientInfo> clientInfos = FXCollections.observableArrayList(newAddClients);
                            watcherListView.getItems().addAll(clientInfos);
                        }

                        if (removeIndexs != null) {
                            for (int index : removeIndexs) {
                                watcherListView.getItems().remove(index);
                            }
                        }
                    }
                });

            }
        }, 0, 1000);

    }

    public void initServerListView() {

        serverListView.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new ServerInfo.ListViewCell();
            }
        });


        //列表定时刷新
        MyTimer.addControlTask(serverListView, new TimerTask() {
            @Override
            public void run() {
                try {
                    List<Integer> timeOutServerIndexs = ServerManager.getInstance().removeTimeOutServerReturnIndexs();
                    List<ServerInfo> newAddServer = ServerManager.getInstance().getNewAddServer();

                    if (newAddServer != null) {
                        for (ServerInfo serverInfo : newAddServer) {
                            ServerInfo.ItemClickHandler eventHandler = new ServerInfo.ItemClickHandler() {
                                @Override
                                public void handle(MouseEvent event) {
                                    String text = serverInfo.getMulticastAddress();
                                    serverAddress = serverInfo.getConnAddress();
                                    multicastAddress.setText(text);
                                }
                            };
                            serverInfo.setOnMouseClickHandler(eventHandler);
                            eventHandler.setServerInfo(serverInfo);
                        }
                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            if (newAddServer != null) {
                                ObservableList<ServerInfo> serverInfos = FXCollections.observableArrayList(newAddServer);
                                serverListView.getItems().addAll(serverInfos);
                            }

                            if (timeOutServerIndexs != null) {
                                ObservableList items = serverListView.getItems();
                                for (int index : timeOutServerIndexs) {
                                    serverListView.getItems().remove(index);
                                }
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 2000);

    }

    public void initSendDelaySelect() {
        ObservableList<String> strings = FXCollections.observableArrayList("1", "2", "3");

        sendDelaySelect.setItems(strings);
    }


    Broadcast broadcast;
    ScreenShotManager screenShotManager;
    Multicast multicast;
    MessageDispose msgDis;

    public void initShowScreenBtu() {
        EntryStateKeeper.getInstance().initShowScreenBtuState(showDesktopBtu);

        EntryStateKeeper.ShowScreenBtuState.NORMAL.setChangeState((button) ->
                {
                    button.setText(EntryStateKeeper.ShowScreenBtuState.NORMAL.getShowText());
                    //关闭组播
                    broadcast.stopBroadcastThread();

                    //关闭截屏
                    screenShotManager.stopShot();
                    //关闭组播
                    multicast.multicastStop();
                    //关闭客户端连接
                    msgDis.stopMsgDispose();
                }
        );

        EntryStateKeeper.ShowScreenBtuState.START_SHOW_SCREEN.setChangeState((button) ->
        {
            button.setText(EntryStateKeeper.ShowScreenBtuState.START_SHOW_SCREEN.getShowText());

            //开启广播
            broadcast = new Broadcast();
            broadcast.initBroadcastThread();
            broadcast.startBroadcast();

            //开启截屏
            Rectangle shotArea = new Rectangle(800, 800);
            screenShotManager = new ScreenShotManager(30, 50, shotArea);
            screenShotManager.beginShot();

            //开启组播
            String multicastHost = CommentConfig.getInstance().getProper("server.multicast_address");
            int multicastPort = CommentConfig.getInstance().getProperInt("server.default_multicast_port");
            InetSocketAddress groupAddress = new InetSocketAddress(multicastHost, multicastPort);
            multicast = null;
            try {
                multicast = new Multicast(groupAddress, screenShotManager);
                multicast.multicastStart();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //开始等待客户端连接
            msgDis = new MessageDispose();
            try {
                msgDis.startMsgDispose();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });

        showDesktopBtu.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                EntryStateKeeper.getInstance().changeShowScreenBtuState();
            }
        });
    }

    ConnServer connServer = null;
    MulticastReceive multicastRec = null;
    public void initConnectBut() {
        EntryStateKeeper.getInstance().initConnectBut(connectBut);

        EntryStateKeeper.ConnectButState.NORMAL.setChangeState(button -> {
            button.setText(EntryStateKeeper.ConnectButState.NORMAL.getShowText());
            //停止接收组播消息
            multicastRec.stopReceive();
            //停止发送消息
            MessageSender.getInstance().stopAllSendThread();
            //断开与服务器的连接
            connServer.stopConn();
        });

        EntryStateKeeper.ConnectButState.CONNECTED.setChangeState((Button button) -> {
            button.setText(EntryStateKeeper.ConnectButState.CONNECTED.getShowText());

            //连接服务器
            System.out.println(serverAddress + "-----------------");
            String serverHost = serverAddress.split(":")[0];
            int serverPort = Integer.parseInt(serverAddress.split(":")[1]);
            connServer = new ConnServer(serverHost, serverPort);

            //设置昵称
            String nickNameStr = nickName.getText();
            if(nickNameStr!=null&&!nickNameStr.trim().equals("")) {
                InfoManager.getInstance().setNickName(nickNameStr);
            }

            try {
                connServer.connect();
            } catch (InterruptedException e) {
                System.out.println("连接服务器失败");
                e.printStackTrace();
                return;
            }

            Stage displayStage = getDisplayStage();

            try {
                Bootstrap.showDisplayScene(displayStage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //开始接收组播信息
            InfoManager.getInstance().setConnServerState(InfoManager.ConnServerState.CONNECTING);

            String multicastAddr = multicastAddress.getText();

            String multicastHost = multicastAddr.split(":")[0];
            int multicastPort = Integer.parseInt(multicastAddr.split(":")[1]);
            System.out.println(multicastHost + ":" + multicastPort);

            InetSocketAddress groupAddress = new InetSocketAddress(multicastHost, multicastPort);

            try {
                multicastRec = new MulticastReceive(groupAddress);
                InfoManager.getInstance().setMulticastReceive(multicastRec);
                multicastRec.init();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });


        connectBut.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                EntryStateKeeper.getInstance().changeConnectBut();
            }
        });
    }

    private Stage getDisplayStage(){
        Stage displayStage = AppKeeper.getStage(StageKey.DISPLAY_STAGE);
        if(displayStage==null){
            displayStage = new Stage();
            displayStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    Stage s = (Stage) event.getSource();
                    EntryStateKeeper.getInstance().changeConnectBut();
                    s.hide();
                    event.consume();
                }
            });
            AppKeeper.putStage(StageKey.DISPLAY_STAGE,displayStage);
        }
        displayStage.setWidth(500);
        displayStage.setHeight(500);

        displayStage.setTitle("展示桌面");
        return displayStage;
    }
}
