package com.xubao.gui.bootstarp;

import com.xubao.gui.struct.controlStruct.AppKeeper;
import com.xubao.gui.struct.controlStruct.SceneKey;
import com.xubao.gui.struct.controlStruct.StageKey;
import com.xubao.server.manager.ServerInfoManager;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;

/**
 * @Author xubao
 * @Date 2018/3/9
 */
public class Bootstrap extends Application {
    public static final String DISPLAY_SCENE_FILE = "fxml/DisplayUI.fxml";
    public static final String ENTRY_SCENE_FILE = "fxml/EntryUI.fxml";

    public static Image icon = new Image("picture/icon.png");

    @Override
    public void start(Stage stage) throws Exception {

        AppKeeper.putStage(StageKey.STAGE, stage);

        initstage(stage);

        ServerInfoManager.getInstance().processorProvider.getProcessorByMsgClassName("");

        //showDisplayScene(stage);
        showEntryScene(stage);
    }

    private void initstage(Stage stage) {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (stage.getScene() == AppKeeper.getScene(SceneKey.DISPLAY_SCENE)) {
                    try {
                        showEntryScene(stage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    System.exit(0);
                }
            }
        });
    }

    public static Scene loadScene(String fxmlFile) throws IOException {
        URL resource = Bootstrap.class.getClassLoader().getResource(fxmlFile);
        Parent parent = FXMLLoader.load(resource);
        Scene scene = new Scene(parent);

        return scene;
    }

    public static void showDisplayScene(Stage stage) throws IOException {
        //暂时无法重用
        Scene displayScene = null;//AppKeeper.getScene(SceneKey.DISPLAY_SCENE);
        if (displayScene == null) {
            displayScene = loadScene(DISPLAY_SCENE_FILE);
            AppKeeper.putScene(SceneKey.DISPLAY_SCENE, displayScene);
        }


        stage.setScene(displayScene);
        stage.setTitle("显示界面");
        stage.getIcons().add(icon);

        stage.show();
    }

    public static void showEntryScene(Stage stage) throws IOException {
        Scene entryScene = AppKeeper.getScene(SceneKey.DISPLAY_SCENE);
        if (entryScene == null) {
            entryScene = loadScene(ENTRY_SCENE_FILE);
            AppKeeper.putScene(SceneKey.ENTRY_SCENE, entryScene);
        }

        stage.setScene(entryScene);
        stage.setTitle("局域网桌面共享");
        stage.getIcons().add(icon);

        stage.show();
    }

    public static void changeScene(Stage stage, Scene scene) {
        stage.setScene(scene);
        stage.show();
    }

    public static void hideStage(Stage stage) {
        stage.hide();
    }

}
