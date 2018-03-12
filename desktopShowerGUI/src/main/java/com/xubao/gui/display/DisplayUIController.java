package com.xubao.gui.display;

import com.xubao.client.manager.FrameManager;
import com.xubao.client.pojo.ReceiveFrame;
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
public class DisplayUIController implements Initializable
{
    @FXML
    private Button screenControlBtu;

    @FXML
    private Canvas canvas;

    private GraphicsContext graphics;

    private Thread drawThread;

    private int drawInterval = 50;

    private int getFrameWaitTime = 30;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DisplayStateKeeper.getInstance().initBtuState(screenControlBtu);

        //画图
        graphics = canvas.getGraphicsContext2D();
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

    private void drawImage(Image image){
	    graphics.drawImage(image,0,0,image.getWidth(),image.getHeight(),0,0,canvas.getWidth(),canvas.getHeight());
    }

    private void initDrawThread(){
        drawThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
            	byte[] buf = new byte[1024*500];
	            while(true)
	            {
		            try
		            {
			            Thread.sleep(drawInterval);
		            }
		            catch(InterruptedException e)
		            {
			            e.printStackTrace();
			            break;
		            }

		            ReceiveFrame frame = FrameManager.getInstance().getAndWaitFirstFrameFull(getFrameWaitTime, TimeUnit.MILLISECONDS, true, false);
					if(frame==null||!frame.isFull()){
						continue;
					}
		            ByteArrayOutputStream baos = new ByteArrayOutputStream((int)frame.getFrameSize());
		            try
		            {
			            frame.writeData(baos);
		            }
		            catch(IOException e)
		            {
			            e.printStackTrace();
		            }
		            Image image = new Image(new ByteArrayInputStream(baos.toByteArray()));
		            drawImage(image);
	            }
            }
        });
    }

}
