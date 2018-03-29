package com.xubao.gui.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.awt.*;
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
}
