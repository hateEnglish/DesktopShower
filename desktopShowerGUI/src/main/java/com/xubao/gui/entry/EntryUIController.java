/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xubao.gui.entry;

import com.xubao.client.broadcastReceive.BroadcastReceive;
import com.xubao.client.connection.ConnServer;
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
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
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

        BroadcastReceive broadcastReceive = new BroadcastReceive();
        try {
            broadcastReceive.initServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //初始化观看列表
    public void initWatchListView() {
        MyTimer.addControlTask(watcherListView, new TimerTask() {
            @Override
            public void run() {
                ClientManager.getInstance().removeHeartbeatTimeoutClient();
                ObservableList<HBox> clientListItems = ClientManager.getInstance().getClientListItems();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        watcherListView.setItems(clientListItems);
                    }
                });

            }
        }, 0, 1000);

    }

    public void initServerListView() {

//        serverListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
//            @Override
//            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
//                System.out.println("o="+oldValue+" n="+newValue);
//                Object temp = newValue==null?oldValue:newValue;
//                ServerInfo serverInfo=(ServerInfo)temp;
//                String text = serverInfo.getListCell().getMulticastAddress().getText();
//                multicastAddress.setText(text);
//            }
//        });




//        serverListView.onMouseClickedProperty().setValue(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                System.out.println("mouseClick");
//            }
//        });

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
                    List<Integer> timeOutServerIndexs = ServerManager.getInstance().getAndRemoveTimeOutServerIndex();
                    List<ServerInfo> newAddServer = ServerManager.getInstance().getNewAddServer();

                    if(newAddServer!=null){
                        for(ServerInfo serverInfo:newAddServer){
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
                                for (Integer index : timeOutServerIndexs) {
                                   serverListView.getItems().remove((int)index);
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
    public void initShowScreenBtu() {
        EntryStateKeeper.getInstance().initShowScreenBtuState(showDesktopBtu);

        EntryStateKeeper.ShowScreenBtuState.NORMAL.setChangeState((button) ->
                {
                    button.setText(EntryStateKeeper.ShowScreenBtuState.NORMAL.getShowText());
                    broadcast.stopBroadcastThread();

                    screenShotManager.stopShot();
                    multicast.multicastStop();
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
            Rectangle shotArea = new Rectangle(800,800);
            screenShotManager = new ScreenShotManager(30,50,shotArea);
            screenShotManager.beginShot();

            //开启组播
            String multicastHost = CommentConfig.getInstance().getProper("server.multicast_address");
            int multicastPort = CommentConfig.getInstance().getProperInt("server.default_multicast_port");
            InetSocketAddress groupAddress = new InetSocketAddress(multicastHost, multicastPort);
            multicast = null;
            try {
                multicast = new Multicast(groupAddress,screenShotManager);
                multicast.multicastStart();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //开始等待客户端连接
            MessageDispose msgDis = new MessageDispose();
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


    public void initConnectBut(){
        EntryStateKeeper.getInstance().initConnectBut(connectBut);

        EntryStateKeeper.ConnectButState.NORMAL.setChangeState(button ->{
            button.setText(EntryStateKeeper.ConnectButState.NORMAL.getShowText());
        } );

        EntryStateKeeper.ConnectButState.CONNECTED.setChangeState((Button button) -> {
            //连接服务器
            System.out.println(serverAddress+"-----------------");
            String serverHost = serverAddress.split(":")[0];
            int serverPort = Integer.parseInt(serverAddress.split(":")[1]);
            ConnServer connServer = new ConnServer(serverHost,serverPort);
            try {
                connServer.connect();
            } catch (InterruptedException e) {
                System.out.println("连接服务器失败");
                e.printStackTrace();
                return;
            }

            Stage stage = AppKeeper.getStage(StageKey.STAGE);
            try {
                Bootstrap.showDisplayScene(stage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //开始接收组播信息
            String multicastAddr = multicastAddress.getText();

            String multicastHost = multicastAddr.split(":")[0];
            int multicastPort = Integer.parseInt(multicastAddr.split(":")[1]);
            System.out.println(multicastHost+":"+multicastPort);

            InetSocketAddress groupAddress = new InetSocketAddress(multicastHost, multicastPort);
            MulticastReceive multicast = null;
            try {
                multicast = new MulticastReceive(groupAddress);
                multicast.init();
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

}
