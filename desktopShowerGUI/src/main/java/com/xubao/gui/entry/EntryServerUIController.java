package com.xubao.gui.entry;

import com.xubao.comment.config.CommentConfig;
import com.xubao.gui.timeTask.MyTimer;
import com.xubao.server.base.ScreenShotManager;
import com.xubao.server.broadcast.Broadcast;
import com.xubao.server.connection.MessageDispose;
import com.xubao.server.manager.ClientManager;
import com.xubao.server.manager.ServerInfoManager;
import com.xubao.server.multicast.Multicast;
import com.xubao.server.pojo.ClientInfo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.awt.*;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.TimerTask;

/**
 * @Author xubao
 * @Date 2018/3/25
 */
public class EntryServerUIController {
    //服务
    Button showDesktopBtu;        //分享桌面按钮

    ChoiceBox screenSizeSelect;    //

    ChoiceBox sendDelaySelect;

    ChoiceBox sendQualitySelect;

    CheckBox watchPasswordCheck;

    TextField watchPassword;

    HBox passwordContainer;

    ListView watcherListView;

    public EntryServerUIController(EntryUIController controller){
        showDesktopBtu = controller.showDesktopBtu;
        screenSizeSelect = controller.screenSizeSelect;
        sendDelaySelect = controller.sendDelaySelect;
        sendQualitySelect = controller.sendQualitySelect;
        watchPasswordCheck = controller.watchPasswordCheck;
        watchPassword = controller.watchPassword;
        passwordContainer = controller.passwordContainer;
        watcherListView = controller.watcherListView;
    }

    public void init(){
        initWatchListView();
        initShowScreenBtu();
        initSendDelaySelect();
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
            Rectangle shotArea = ServerInfoManager.getInstance().shotArea;
            screenShotManager = new ScreenShotManager(ServerInfoManager.getInstance().shotCacheSize,
                    ServerInfoManager.getInstance().shotInterval, shotArea);

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


    public void initSendDelaySelect() {
        ObservableList<String> strings = FXCollections.observableArrayList("1", "2", "3");

        sendDelaySelect.setItems(strings);
    }
}
