package com.xubao.client.bootstart;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class BootStart extends Application{

    private Stage primaryStage;
    private Pane rootLayout;
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(BootStart.class.getClassLoader().getResource("showDesktop.fxml"));
        rootLayout = (Pane) fxmlLoader.load();

        // Show the scene containing the root layout.
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
