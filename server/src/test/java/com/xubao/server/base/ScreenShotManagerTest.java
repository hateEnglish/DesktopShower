package com.xubao.server.base;

import com.xubao.server.base.ScreenShotManager;
import com.xubao.server.pojo.Frame;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author xubao
 * @Date 2018/2/6
 */
public class ScreenShotManagerTest {

    ScreenShotManager screenShotManager;

    @Before
    public void before() {
        screenShotManager = new ScreenShotManager(10, 20);
        screenShotManager.setShotArea(500, 500);
    }

    @Test
    public void testShot() {
        screenShotManager.beginShot();
        int times = 500;
        for (int i = 0; i < times; i++) {
            try {
                Frame frame = screenShotManager.getFrame();
                FileOutputStream fos = new FileOutputStream("z://temp//" + i + ".jpg");
                ByteArrayInputStream bais = new ByteArrayInputStream(frame.getData());
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                byte[] buf = new byte[2048];
                while (bais.read(buf) != -1) {
                    bos.write(buf);
                }

                bos.close();
                fos.close();
                bais.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {

            }
        }

        screenShotManager.stopShot();
    }

}
