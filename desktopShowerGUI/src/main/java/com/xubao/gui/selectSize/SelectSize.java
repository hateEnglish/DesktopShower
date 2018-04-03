package com.xubao.gui.selectSize;

import com.xubao.gui.util.MyUtil;
import javafx.event.*;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xubao
 * @version 1.0
 * @since 2018/4/2
 */
public class SelectSize {
    private Stage stage;
    private AnchorPane anchorPane;
    private Scene scene;
    private Canvas canvas;
    private Image desktopImg;
    private EventHandler mouseEventHandler;

    private GraphicsContext graphics;

    public SelectSize() {

        stage = new Stage();
        Rectangle screenSize = MyUtil.getScreenSize();

        anchorPane = new AnchorPane();
        //scene = new Scene(anchorPane, screenSize.width, screenSize.height);
        scene = new Scene(anchorPane, 500, 500);
        canvas = new Canvas();
        graphics = canvas.getGraphicsContext2D();
        canvas.heightProperty().bind(scene.heightProperty());
        canvas.widthProperty().bind(scene.widthProperty());
        anchorPane.getChildren().add(canvas);
        stage.setScene(scene);

        init();
    }

    private void init() {
        initMouseEventHandler();
    }

    private void initMouseEventHandler() {
        AtomicInteger y = new AtomicInteger();
        mouseEventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                y.addAndGet(10);
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    showText("鼠标按下",100,y.get());
                    System.out.println("鼠标按下");
                } else if (event.getEventType() == MouseEvent.MOUSE_MOVED) {
//                    showText("鼠标移动",100,y.get());
//                    System.out.println("鼠标移动");
                } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                    showText("鼠标释放",100,y.get());
                    System.out.println("鼠标释放");
                }else if(event.getEventType() == MouseEvent.MOUSE_DRAGGED){
                    //showText("鼠标拖拽",100,y.get());
                    System.out.println("鼠标推拽");
                }
            }
        };
    }

    public void setDesktopImg(Image img) {
        desktopImg = img;
        drawImg(desktopImg);
    }

    public void drawImg(Image image) {
        graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight());
    }

    public void showText(String text,int x,int y) {
        graphics.fillText(text, x, y);
    }

    public void setMouseListener() {
        canvas.addEventHandler(MouseEvent.ANY, mouseEventHandler);
    }

    public void moveMouseListener() {
        canvas.removeEventHandler(MouseEvent.ANY, mouseEventHandler);
    }

    public void showStage() {
 //       stage.setMaximized(true);
        stage.setWidth(500);
        stage.setHeight(500);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();

        setMouseListener();
    }


    public Pane getAnchorPane() {
        return anchorPane;
    }


    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setAnchorPane(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }
}
