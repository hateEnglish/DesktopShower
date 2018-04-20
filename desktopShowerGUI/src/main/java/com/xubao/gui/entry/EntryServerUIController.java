package com.xubao.gui.entry;

import com.xubao.comment.config.CommentConfig;
import com.xubao.comment.util.MyTimer;
import com.xubao.comment.util.NetAddress;
import com.xubao.gui.settingSave.ClientSetting;
import com.xubao.gui.settingSave.DBUtil;
import com.xubao.gui.settingSave.ServerSetting;
import com.xubao.gui.struct.controlStruct.AppKeeper;
import com.xubao.gui.struct.controlStruct.StageKey;
import com.xubao.gui.util.AlertWindow;
import com.xubao.gui.util.MyUtil;
import com.xubao.server.base.ScreenShotManager;
import com.xubao.server.broadcast.Broadcast;
import com.xubao.server.connection.MessageDispose;
import com.xubao.server.manager.ClientManager;
import com.xubao.server.manager.ServerInfoManager;
import com.xubao.server.multicast.Multicast;
import com.xubao.server.pojo.ClientInfo;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.TimerTask;

/**
 * @Author xubao
 * @Date 2018/3/25
 */
public class EntryServerUIController {
    private static Logger log = LoggerFactory.getLogger(EntryUIController.class);

    //服务
    Button showDesktopBtu;        //分享桌面按钮

    ChoiceBox screenSizeSelect;    //

    ChoiceBox sendDelaySelect;

    ChoiceBox sendQualitySelect;

    CheckBox watchPasswordCheck;

    TextField watchPassword;

    HBox passwordContainer;

    ListView watcherListView;

    Label pwdNotice;

    TextField showTheme;

    public EntryServerUIController(EntryUIController controller) {
        showDesktopBtu = controller.showDesktopBtu;
        screenSizeSelect = controller.screenSizeSelect;
        sendDelaySelect = controller.sendDelaySelect;
        sendQualitySelect = controller.sendQualitySelect;
        watchPasswordCheck = controller.watchPasswordCheck;
        watchPassword = controller.watchPassword;
        passwordContainer = controller.passwordContainer;
        watcherListView = controller.watcherListView;
        pwdNotice = controller.pwdNotice;
        showTheme = controller.showTheme;
    }

    public void init() {
        initWatchListView();
        initShowScreenBtu();
        initSendDelaySelect();

        initScreenSizeSelect();

        initPassword();

        initSendQualitySelect();

        setting();
    }

    public void setting(){
        try {
            ServerSetting serverSetting = DBUtil.SimpleDBUtil.getInstance().selectServerSetting();
            if(serverSetting!=null){
                sendQualitySelect.getSelectionModel().select(serverSetting.getSendQuality());
                sendDelaySelect.getSelectionModel().select(serverSetting.getSendDelay());
                screenSizeSelect.getSelectionModel().select(serverSetting.getScreenSize());
                showTheme.setText(serverSetting.getShareTheme());
                watchPasswordCheck.setSelected(serverSetting.isNeedPwd());
                watchPassword.setText(serverSetting.getPwd());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    ServerSetting serverSetting = new ServerSetting(showTheme.getText(),screenSizeSelect.getSelectionModel().getSelectedIndex()
                            ,sendDelaySelect.getSelectionModel().getSelectedIndex()
                            ,sendQualitySelect.getSelectionModel().getSelectedIndex(),watchPasswordCheck.isSelected(),watchPassword.getText());
                    DBUtil.SimpleDBUtil.getInstance().saveServerSetting(serverSetting);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        ));
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

                    //开启其他控件
                    disOthersControl(false);

                    return true;
                }
        );

        EntryStateKeeper.ShowScreenBtuState.START_SHOW_SCREEN.setChangeState((Button button) ->
        {
            InetAddress localHost = NetAddress.getLocalHostLANAddress();
            System.out.println(localHost);
            if(localHost.isLoopbackAddress()){
                AlertWindow.notify("不在局域网中", AppKeeper.getStage(StageKey.STAGE));
                return false;
            }

            //展示提示
            if(watchPasswordCheck.isSelected()&&watchPassword.getText().trim().equals("")){
                pwdNotice.setTextFill(Color.RED);
                pwdNotice.setText("密码为空");
            }
            //获取共享主题
            ServerInfoManager.getInstance().showTheme = showTheme.getText();

            //关闭其他控件
            disOthersControl(true);

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


            return true;
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
                            watcherListView.refresh();
                        }
                        //System.out.println("观看列表size="+watcherListView.getItems().size());
                        if (removeIndexs != null) {
                            for (int index : removeIndexs) {
                                watcherListView.getItems().remove(index);
                            }

                            watcherListView.refresh();
                        }
                    }
                });

            }
        }, 0, 1000);

    }


    public void initSendDelaySelect() {
        ObservableList<String> strings = FXCollections.observableArrayList("自动", "5s", "10s", "15s");

        sendDelaySelect.setItems(strings);
        sendDelaySelect.getSelectionModel().select(0);

        sendDelaySelect.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                log.debug("oldValue={},newValue={}", oldValue, newValue);

                ServerInfoManager.getInstance().sendDelay = (String)newValue;
            }
        });
    }

    public void initScreenSizeSelect() {

        Rectangle testArea = new Rectangle(800,800);
        String testSize = String.format("测试%d*%d",testArea.width,testArea.height);

        ObservableList<String> strings = FXCollections.observableArrayList("全屏",testSize);//, "自选");

        screenSizeSelect.setItems(strings);
        screenSizeSelect.getSelectionModel().select(0);

        screenSizeSelect.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                log.debug("oldValue={},newValue={}", oldValue, newValue);
                if(newValue.equals(testSize)){
                    ServerInfoManager.getInstance().shotArea = testArea;
                    ServerInfoManager.getInstance().startCoord = new Point(0,0);
                    ServerInfoManager.getInstance().endCoord = new Point(testArea.width,testArea.height);
                }else if(newValue.equals("全屏")){
                    Rectangle screenSize = MyUtil.getScreenSize();
                    ServerInfoManager.getInstance().shotArea = screenSize;
                    ServerInfoManager.getInstance().startCoord = new Point(0,0);
                    ServerInfoManager.getInstance().endCoord = new Point(screenSize.width,screenSize.height);
                }
            }
        });
    }

    public void initPassword() {
        passwordContainer.setVisible(false);

        watchPasswordCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                log.debug("oldValue={},newValue={}", oldValue, newValue);
                ServerInfoManager.getInstance().isNeedPwd = false;

                passwordContainer.setVisible(newValue);
                pwdNotice.setText("");
                watchPassword.setText("");
            }
        });

        watchPassword.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                log.debug("oldValue={},newValue={}", oldValue, newValue);
                if (!newValue) {
                    if (watchPassword.getText().trim().equals("")) {
                        ServerInfoManager.getInstance().isNeedPwd = false;

                        pwdNotice.setTextFill(Color.RED);
                        pwdNotice.setText("密码为空");
                    } else {
                        ServerInfoManager.getInstance().watchPwd = watchPassword.getText();
                        ServerInfoManager.getInstance().isNeedPwd = true;
                        pwdNotice.setText("");
                    }
                }
            }
        });
    }

    public void initSendQualitySelect() {
        sendQualitySelect.setItems(FXCollections.observableArrayList(ClientInfo.Quality.HEIGHT, ClientInfo.Quality.MIDDLE, ClientInfo.Quality.LOWER));
        sendQualitySelect.getSelectionModel().select(0);

        sendQualitySelect.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ClientInfo.Quality>() {
            @Override
            public void changed(ObservableValue observable, ClientInfo.Quality oldValue, ClientInfo.Quality newValue) {
                log.debug("oldValue={},newValue={}", oldValue, newValue);
                ServerInfoManager.getInstance().quality = newValue;
            }
        });
    }

    public void disOthersControl(boolean disable){
        screenSizeSelect.setDisable(disable);
        sendDelaySelect.setDisable(disable);
        sendQualitySelect.setDisable(disable);
        watchPassword.setDisable(disable);
        watchPasswordCheck.setDisable(disable);
        showTheme.setDisable(disable);
    }
}
