package com.xubao.server.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author xubao
 * @Date 2018/2/5
 */
public class ScreenShot {

    private static Logger log = LoggerFactory.getLogger(ScreenShot.class);

    private static Robot robot = null;

    static {
        try {
            init();
        } catch (AWTException e) {
            e.printStackTrace();
            log.error("该系统不支持截屏,e={}", e);
            System.exit(1);
        }
    }

    public static void init() throws AWTException {
        robot = new Robot();
    }

    public static BufferedImage screenShot(Rectangle rect) {
        log.debug("rect={}", rect);
        return robot.createScreenCapture(rect);
    }

    public static BufferedImage screenShot(int x, int y, int width, int height) {
        log.debug("x={},y={},width={},height={}", x, y, width, height);
        Rectangle rect = new Rectangle(x, y, width, height);
        return screenShot(rect);
    }

    public static BufferedImage screenShot(int width, int height) {
        return screenShot(0, 0, width, height);
    }

}
