package com.xubao.gui.selectSize;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @author xubao
 * @version 1.0
 * @since 2018/4/2
 */
public class SelectSize {
    private Stage stage;
    private AnchorPane pane;
    private Scene scene;
    private Canvas canvas;

    public SelectSize(){
        stage = new Stage();
        stage.setHeight(300);
        stage.setWidth(300);
        pane = new AnchorPane();
        scene = new Scene(pane,300,300);
       canvas = new Canvas();
        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();

        canvas.setHeight(300);
       canvas.setHeight(300);
       graphicsContext2D.setFill(Color.RED);
        graphicsContext2D.fillRect(0,0,canvas.getHeight(),canvas.getWidth());
        //imageView.fitHeightProperty().bind(stage.heightProperty());
        //imageView.fitWidthProperty().bind(stage.widthProperty());

        pane.getChildren().add(canvas);
       // pane.centerProperty().setValue(imageView);

        stage.show();
    }

    public static void main(String[] args){
        SelectSize selectSize = new SelectSize();
        selectSize.stage.show();
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Pane getPane() {
        return pane;
    }


    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setPane(AnchorPane pane) {
        this.pane = pane;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }
}
