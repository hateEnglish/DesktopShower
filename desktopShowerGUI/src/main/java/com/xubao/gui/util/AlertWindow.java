package com.xubao.gui.util;

import com.xubao.gui.bootstarp.Bootstrap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

/**
 * @author xubao
 * @version 1.0
 * @since 2018/3/29
 */
public class AlertWindow {
    public static void notify(String text, Stage stage){
        Alert alert = new Alert(Alert.AlertType.NONE, text,new ButtonType("确定"));
        //设置窗口的标题
        alert.setTitle("提示");
        alert.initOwner(stage);
        alert.showAndWait();
    }

    public static String getInput(String title,String text,Stage stage){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText("");
        dialog.setContentText(text+": ");
        dialog.initOwner(stage);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            System.out.println(result.get()+"koko");
            return result.get().trim().equals("")?null:result.get();
        }
        return null;
    }




    public static class AboutDialog {

        public static void showDialog() {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);

            BorderPane aboutPane = createAboutPane(stage);
            Scene scene = new Scene(aboutPane, 300, 180);

            stage.setScene(scene);
            stage.setTitle("About");
            stage.show();
        }

        private static BorderPane createAboutPane(Stage dialogStage) {
            BorderPane pane = new BorderPane();
            HBox hBox = new HBox();
            Label label = new Label(Bootstrap.TITLE);
            hBox.getChildren().add(label);
            hBox.setAlignment(Pos.CENTER);
            pane.setCenter(hBox);
            pane.setBottom(createHomeLink());
            pane.setOnMouseClicked(e -> dialogStage.close());

            return pane;
        }

        private static Hyperlink createHomeLink() {
            String homeUrl = "https://github.com/hateEnglish/DesktopShower";
            Hyperlink link = new Hyperlink(homeUrl);
            link.setOnAction(e -> {
                try {
                    Desktop.getDesktop().browse(URI.create(homeUrl));
                } catch (IOException x) {
                    x.printStackTrace(System.err);
                }
            });

            BorderPane.setAlignment(link, Pos.CENTER);
            BorderPane.setMargin(link, new Insets(8));
            return link;
        }

    }
}
