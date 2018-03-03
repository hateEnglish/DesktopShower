package com.xubao.client.shotDesktop;

import com.xubao.server.base.ScreenShotManager;
import com.xubao.server.pojo.Frame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.io.ByteArrayInputStream;

/**
 * @Author xubao
 * @Date 2018/2/6
 */
public class ShowDesktopController extends Pane {
    @FXML
    Label label;

    @FXML
    Canvas canvas;

    GraphicsContext gc;

    ScreenShotManager screenShotManager;

    public ShowDesktopController() {
        screenShotManager = new ScreenShotManager(10, 20,new Rectangle());
        screenShotManager.setShotArea(500, 500);

    }

    int c = 0;

    @FXML
    public void handOnClick(MouseEvent event) {
        System.out.println("click");
        label.setText("点击次数:" + (++c));
        draw();
    }

    private void draw() {
        if (gc == null) {
            gc = canvas.getGraphicsContext2D();
        }
        Frame frame = null;
        try {
            frame = screenShotManager.getFrame();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       // Image img = new Image(new ByteArrayInputStream(frame.getData()));
       // gc.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), 0, 0, canvas.getWidth(), canvas.getHeight());
        Thread thread = null;
        if (c % 3 == 1) {
            screenShotManager.beginShot();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread = new Thread(runnable);
            thread.start();
        } else if (c % 3 == 2) {
            screenShotManager.waitShot();
        } else if (c % 3 == 0) {
            thread.interrupt();
            screenShotManager.stopShot();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                Frame frame = null;
                try {
                    frame = screenShotManager.getFrame();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Image img = new Image(new ByteArrayInputStream(frame.getData()));
                gc.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), 0, 0, canvas.getWidth(), canvas.getHeight());

                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("因中断停止绘制...");
                    break;
                }
            }
        }
    };

}
