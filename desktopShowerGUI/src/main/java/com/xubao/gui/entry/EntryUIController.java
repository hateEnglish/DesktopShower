/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xubao.gui.entry;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimerTask;

import com.xubao.client.manager.ServerManager;
import com.xubao.client.pojo.ServerInfo;
import com.xubao.gui.timeTask.MyTimer;
import com.xubao.server.manager.ClientManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

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
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
	    ServerManager.getInstance().addServerInfo(new ServerInfo(new InetSocketAddress("127.0.0.1",10001),new InetSocketAddress("fjisfj",121),
			    "jdifjis","jfisjif"));
		initServerListView();
    }


    public void initWatchListView(){
	    MyTimer.addControlTask(watcherListView, new TimerTask()
	    {
		    @Override
		    public void run()
		    {
		    	ClientManager.getInstance().removeHeartbeatTimeoutClient();
			    ObservableList<HBox> clientListItems = ClientManager.getInstance().getClientListItems();
			    watcherListView.setItems(clientListItems);
		    }
	    },0,1000);

    }

	public void initServerListView(){

    	//serverListView.onMouseClickedProperty()
    	//serverListView.onScrollToProperty()
    	//serverListView.focusModelProperty()
    	//serverListView.selectionModelProperty()
// .addListener(new ChangeListener()
//	    {
//		    @Override
//		    public void changed(ObservableValue observable, Object oldValue, Object newValue)
//		    {System.out.println("jisjidf");
//			    HBox hBox = (HBox)newValue;
//
//		    }
//	    });

		//serverListView.setOnMouseClicked();


		MyTimer.addControlTask(serverListView, new TimerTask()
		{
			@Override
			public void run()
			{
				ServerManager.getInstance().removeTimeOutServer();
				ObservableList<HBox> serverListItems = ServerManager.getInstance().getServerListItems();
				watcherListView.setItems(serverListItems);
			}
		},0,1000);

	}



}
