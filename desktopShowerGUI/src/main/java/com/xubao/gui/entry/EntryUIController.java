/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xubao.gui.entry;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ResourceBundle;

import com.xubao.client.pojo.ServerInfo;
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
//	    HBox hBox = new HBox();
//	    Label l1 = new Label("l1");
//	    l1.setPrefWidth(100);
//	    Label l2 = new Label("l2");
//	    l2.setPrefWidth(100);
//	    Label l3 = new Label("l3");
//	    l3.setPrefWidth(100);
//	    hBox.getChildren().addAll(l1,l2,l3);

	    ServerInfo serverInfo = new ServerInfo(new InetSocketAddress("127.0.0.1",232),new InetSocketAddress("127.0.0.1",3434)
			    ,"comment","dreijfa");

	    ObservableList<HBox> strings = FXCollections.observableArrayList(serverInfo.getListItem(),serverInfo.getListItem(),serverInfo.getListItem(),serverInfo.getListItem());
//	    for(int i = 0;i<10;i++){
//	    	serverListView
//	    }
	    serverListView.setItems(strings);
	    System.out.println("jijisf");
    }
    
}
