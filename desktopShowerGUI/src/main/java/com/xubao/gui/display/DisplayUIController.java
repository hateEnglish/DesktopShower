package com.xubao.gui.display;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author xubao
 * @version 1.0
 * @since 2018/3/8
 */
public class DisplayUIController implements Initializable
{
    @FXML
    private Button but;
    @FXML
    private ImageView imgView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        StateKeeper.getInstance().initBtuState(but);
    }

    @FXML
    private void handleButtonAction(MouseEvent event) {
        StateKeeper.getInstance().changeBtuState(but);
    }
}
