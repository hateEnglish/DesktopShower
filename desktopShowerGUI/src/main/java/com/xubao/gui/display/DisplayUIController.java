package com.xubao.gui.display;

import com.xubao.gui.struct.controlStruct.AppKeeper;
import com.xubao.gui.struct.controlStruct.StageKey;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.awt.*;
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
    private Button screenControlBtu;

    @FXML
    private Canvas canvas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DisplayStateKeeper.getInstance().initBtuState(screenControlBtu);

        //画图
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        Image image = new Image("test.jpg");
        graphics.drawImage(image,0,0,image.getWidth(),image.getHeight(),0,0,canvas.getWidth(),canvas.getHeight());

        DisplayStateKeeper.BtuState.NOEMAL.setChangeState((Button button) ->
        {
            button.setText(DisplayStateKeeper.BtuState.NOEMAL.getShowText());
            Stage stage = AppKeeper.getStage(StageKey.STAGE);
            stage.setFullScreen(false);
            canvasSetHW(stage.getHeight(),stage.getWidth());
            graphics.drawImage(image,0,0,image.getWidth(),image.getHeight(),0,0,canvas.getWidth(),canvas.getHeight());

        });

        DisplayStateKeeper.BtuState.FULL_SCREEN.setChangeState((button) ->
        {
            button.setText(DisplayStateKeeper.BtuState.FULL_SCREEN.getShowText());
            Stage stage = AppKeeper.getStage(StageKey.STAGE);
            stage.setFullScreen(true);
            canvasSetHW(stage.getHeight(),stage.getWidth());
            graphics.drawImage(image,0,0,image.getWidth(),image.getHeight(),0,0,canvas.getWidth(),canvas.getHeight());

        });
    }

    private void canvasSetHW(double h,double w){
        canvas.setHeight(h);
        canvas.setWidth(w);
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        DisplayStateKeeper.getInstance().changeBtuState(screenControlBtu);
    }
}
