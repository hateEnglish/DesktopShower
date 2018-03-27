package com.xubao.gui.entry;

import com.xubao.client.broadcastReceive.BroadcastReceive;
import com.xubao.client.connection.ConnServer;
import com.xubao.client.connection.MessageSender;
import com.xubao.client.manager.ClientInfoManager;
import com.xubao.client.manager.ServerManager;
import com.xubao.client.multicastReceive.MulticastReceive;
import com.xubao.client.pojo.ServerInfo;
import com.xubao.gui.bootstarp.Bootstrap;
import com.xubao.gui.struct.controlStruct.AppKeeper;
import com.xubao.gui.struct.controlStruct.StageKey;
import com.xubao.gui.timeTask.MyTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.TimerTask;

/**
 * @Author xubao
 * @Date 2018/3/25
 */
public class EntryClientUIController {
    private static Logger log = LoggerFactory.getLogger(EntryUIController.class);

    //连接

    TextField multicastAddress;

    TextField nickName;

    CheckBox fullScreenCheck;

    ListView serverListView;

    Button connectBut;

    String serverAddress;

    public EntryClientUIController(EntryUIController controller){
        multicastAddress = controller.multicastAddress;
        nickName = controller.nickName;
        fullScreenCheck = controller.fullScreenCheck;
        serverListView = controller.serverListView;
        connectBut = controller.connectBut;
    }

    public void init(){
        multicastAddress.setEditable(false);
        connectBut.setDisable(true);

        initConnectBut();
        initServerListView();

        initFullScreenCheck();

        BroadcastReceive broadcastReceive = new BroadcastReceive();
        try {
            broadcastReceive.initServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                                    connectBut.setDisable(false);
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

            //隐藏窗口
            AppKeeper.getStage(StageKey.DISPLAY_STAGE).hide();
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
                ClientInfoManager.getInstance().setNickName(nickNameStr);
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
            ClientInfoManager.getInstance().setConnServerState(ClientInfoManager.ConnServerState.CONNECTING);

            String multicastAddr = multicastAddress.getText();

            String multicastHost = multicastAddr.split(":")[0];
            int multicastPort = Integer.parseInt(multicastAddr.split(":")[1]);
            System.out.println(multicastHost + ":" + multicastPort);

            InetSocketAddress groupAddress = new InetSocketAddress(multicastHost, multicastPort);

            try {
                multicastRec = new MulticastReceive(groupAddress);
                ClientInfoManager.getInstance().setMulticastReceive(multicastRec);
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

        if(ClientInfoManager.getInstance().connFullScreen){
            displayStage.setFullScreen(true);
        }

        displayStage.setTitle("展示桌面");
        return displayStage;
    }


    public void initFullScreenCheck(){
        fullScreenCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                log.debug("oldValue={},newValue={}",oldValue,newValue);
                ClientInfoManager.getInstance().connFullScreen = newValue;
            }
        });
    }
}
