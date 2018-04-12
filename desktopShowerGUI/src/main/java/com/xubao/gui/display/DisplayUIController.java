package com.xubao.gui.display;

import com.xubao.client.manager.FrameManager;
import com.xubao.client.manager.ClientInfoManager;
import com.xubao.client.pojo.ReceiveFrame;
import com.xubao.client.pojo.MousePointInfo;
import com.xubao.comment.config.CommentConfig;
import com.xubao.gui.struct.controlStruct.AppKeeper;
import com.xubao.gui.struct.controlStruct.StageKey;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * @author xubao
 * @version 1.0
 * @since 2018/3/8
 */
public class DisplayUIController implements Initializable {


    @FXML
    private Button screenControlBtu;

    @FXML
    private Canvas canvas;

    private GraphicsContext graphics;

    private Thread drawThread;

    //画图间隔时间
    private int drawInterval = CommentConfig.getInstance().getProperInt("client.draw_interval");

    //异常帧间隔时间
    private int errorFrameInterval = CommentConfig.getInstance().getProperInt("client.error_frame_interval");

    private int getFrameWaitTime = 20;

    private double windowWidth = 0;

    private double windowHeight = 0;

    private double showHeightRatio = 1;
    private double showWidthRatio = 1;

    private Stage stage = AppKeeper.getStage(StageKey.DISPLAY_STAGE);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        flushParam();
        canvasSetWH(windowWidth, windowHeight);

        //画图
        graphics = canvas.getGraphicsContext2D();
        //Image image = new Image("test.jpg");
        //graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), 0, 0, canvas.getWidth(), canvas.getHeight());


        initDrawThread();

        initScreenControlBtu();

        drawThread.start();
        System.out.println("初始化结束");
    }


    private void flushParam() {
        windowWidth = stage.getWidth();
        windowHeight = stage.getHeight();

        canvasSetWH(windowWidth, windowHeight);
    }

    public void initScreenControlBtu() {
        DisplayStateKeeper.getInstance().initBtuState(screenControlBtu);

        DisplayStateKeeper.BtuState.NOEMAL.setChangeState((Button button) ->
        {
            button.setText(DisplayStateKeeper.BtuState.NOEMAL.getShowText());
            Stage stage = AppKeeper.getStage(StageKey.STAGE);
            stage.setFullScreen(false);
            canvasSetWH(stage.getWidth(), stage.getHeight());
            //graphics.drawImage(image,0,0,image.getWidth(),image.getHeight(),0,0,canvas.getWidth(),canvas.getHeight());

            return true;
        });

        DisplayStateKeeper.BtuState.FULL_SCREEN.setChangeState((button) ->
        {
            button.setText(DisplayStateKeeper.BtuState.FULL_SCREEN.getShowText());
            Stage stage = AppKeeper.getStage(StageKey.STAGE);
            stage.setFullScreen(true);
            canvasSetWH(stage.getWidth(), stage.getHeight());
            //graphics.drawImage(image,0,0,image.getWidth(),image.getHeight(),0,0,canvas.getWidth(),canvas.getHeight());

            return true;
        });
    }

    private void canvasSetWH(double w, double h) {
        canvas.setHeight(h);
        canvas.setWidth(w);
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        DisplayStateKeeper.getInstance().changeBtuState();
    }

    private void drawImage(Image image) {
        graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), 0, 0, windowWidth, windowHeight);
    }

    private void drawSimpleText(Font font, Color color, String text, double x, double y, double maxWidth) {
        graphics.setFill(color);
        graphics.setFont(font);
        graphics.fillText(text, x, y, maxWidth);
    }

    private void notifyText(String text) {
        Font font = new Font(20);
        drawSimpleText(font, Color.RED, text, 5, 50, 150);
    }

    private void clearCanvas() {
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, stage.getWidth(), stage.getHeight());
    }

    private void initDrawThread() {
        drawThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //byte[] buf = new byte[1024*500];
                while (true) {
                    try {
                        Thread.sleep(drawInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }

                    ReceiveFrame frame = FrameManager.getInstance().getAndWaitFirstFrameFull(getFrameWaitTime, TimeUnit.MILLISECONDS, true, false);

                    if (frame == null || !frame.isFull()) {
                        try {
                            //根据与服务器连接状态显示页面
                            ClientInfoManager.ConnServerState connServerState = ClientInfoManager.getInstance().getConnServerState();
                            if (connServerState == ClientInfoManager.ConnServerState.CONNECTING) {
                                notifyText("正在连接");
                            } else if (connServerState == ClientInfoManager.ConnServerState.DISCONNECT) {
                                clearCanvas();
                                notifyText("已经断开连接");

                                //关闭组播接收
                                ClientInfoManager.getInstance().getMulticastReceive().stopReceive();
                                break;
                            }
                            Thread.sleep(errorFrameInterval);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                        continue;
                    }

                    if (ClientInfoManager.getInstance().getConnServerState() == ClientInfoManager.ConnServerState.CONNECTING)
                        ClientInfoManager.getInstance().setConnServerState(ClientInfoManager.ConnServerState.CONNECTED);

                    //System.out.println("开始画图");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream((int) frame.getFrameSize());
                    try {
                        frame.writeData(baos);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Image image = new Image(new ByteArrayInputStream(baos.toByteArray()));

                    flushParam();
                    setShowRatio(image);

                    Point mousePoint = frame.getMousePoint();

                    drawImage(image);
                    //绘画鼠标位置
                    MousePointInfo mousePointInfo = ClientInfoManager.getInstance().mousePointInfo;
                    graphics.setFill(mousePointInfo.getColor());
                    //根据显示比例显示鼠标位置
                    graphics.fillOval((mousePoint.x - frame.getStartCoord().x) / showWidthRatio, (mousePoint.y - frame.getStartCoord().y) / showHeightRatio
                            , mousePointInfo.getRadius() / showWidthRatio, mousePointInfo.getRadius() / showHeightRatio);
                }
            }
        });
    }

    private void setShowRatio(Image image) {
        showHeightRatio = image.getHeight() / windowHeight;
        showWidthRatio = image.getWidth() / windowWidth;
    }

    public void stopDrawThread() {
        drawThread.interrupt();
    }

}
