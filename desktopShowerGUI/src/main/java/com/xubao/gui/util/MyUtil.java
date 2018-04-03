package com.xubao.gui.util;

import java.awt.*;

/**
 * @author xubao
 * @version 1.0
 * @since 2018/4/3
 */
public class MyUtil {
    public static Rectangle getScreenSize(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double height = screenSize.getHeight();
        double width = screenSize.getWidth();

        return new Rectangle((int)width,(int)height);
    }
}
