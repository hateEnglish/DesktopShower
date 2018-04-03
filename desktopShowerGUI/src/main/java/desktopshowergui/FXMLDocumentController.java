/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package desktopshowergui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.xubao.gui.selectSize.SelectSize;
import com.xubao.gui.util.MyUtil;
import com.xubao.server.base.ScreenShot;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import javax.imageio.ImageIO;

/**
 *
 * @author admin
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label label;
    @FXML
    private Button button;
    @FXML
    private Button but1;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    SelectSize selectSize ;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        but1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(selectSize==null)
                selectSize = new SelectSize();
                System.out.println(selectSize.getCanvas().getHeight());
                Rectangle screenSize = MyUtil.getScreenSize();
                BufferedImage bufferedImage = ScreenShot.screenShot(screenSize.width, screenSize.height);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ImageIO.write(bufferedImage,"jpg",baos);
                    ImageIO.write(bufferedImage,"jpg",new FileOutputStream("z:/1.jpg"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Image image = new Image(new ByteArrayInputStream(baos.toByteArray()));
                GraphicsContext graphicsContext2D = selectSize.getCanvas().getGraphicsContext2D();
                graphicsContext2D.drawImage(image,0,0,image.getWidth(),image.getHeight());

                selectSize.showStage();
            }
        });
    }

    
}
