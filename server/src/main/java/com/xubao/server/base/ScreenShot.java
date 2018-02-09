package com.xubao.server.base;

import com.xubao.comment.log.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author xubao
 * @Date 2018/2/5
 */
public class ScreenShot {

    private static Robot robot = null;

    static {
        try {
            init();
        } catch (AWTException e) {
            e.printStackTrace();
            Logger.error(ScreenShot.class, "该系统不支持截屏,e=%s", e);
            System.exit(1);
        }
    }

    public static void init() throws AWTException {
        robot = new Robot();
    }

    public static BufferedImage screenShot(Rectangle rect) {
        Logger.debug(ScreenShot.class, "rect=%s", rect);
        return robot.createScreenCapture(rect);
    }

    public static BufferedImage screenShot(int x, int y, int width, int height) {
        Logger.debug(ScreenShot.class, "x=%d,y=%d,width=%d,height=%d", x, y, width, height);
        Rectangle rect = new Rectangle(x, y, width, height);
        return screenShot(rect);
    }

    public static BufferedImage screenShot(int width, int height) {
        return screenShot(0, 0, width, height);
    }

}
