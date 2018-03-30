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

    public EntryMenuController(EntryUIController entryUIController){
        about = entryUIController.about;
    }

    public void init(){
        initAbout();
    }

    private void initAbout(){
        about.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AlertWindow.AboutDialog.showDialog();
            }
        });
    }
}
