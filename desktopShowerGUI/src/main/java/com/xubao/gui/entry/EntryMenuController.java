package com.xubao.gui.entry;

import com.xubao.gui.util.AlertWindow;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

/**
 * @author xubao
 * @version 1.0
 * @since 2018/3/30
 */
public class EntryMenuController {
    public MenuItem about;
    public MenuItem exit;

    public EntryMenuController(EntryUIController entryUIController){
        about = entryUIController.about;
        exit = entryUIController.exit;
    }

    public void init(){
        initAbout();
        initExit();
    }

    private void initAbout(){
        about.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AlertWindow.AboutDialog.showDialog();
            }
        });
    }

    private void initExit(){
        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });
    }
}
