package com.xubao.gui.bootstarp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * @Author xubao
 * @Date 2018/3/9
 */
public class Bootstrap extends Application{
    public static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {

        Bootstrap.stage = stage;

        URL resource = this.getClass().getClassLoader().getResource("fxml/DisplayUI.fxml");
        System.out.println(resource);
        Parent root = FXMLLoader.load(resource);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
