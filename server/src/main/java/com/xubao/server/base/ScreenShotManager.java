package com.xubao.server.base;

import com.xubao.comment.log.Logger;
import com.xubao.server.pojo.Frame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * @Author xubao
 * @Date 2018/2/5
 */
public class ScreenShotManager {
    private BlockingQueue<Frame> frames;
    //截屏时间间隔(毫秒)
    private int screenShotInterval;

    //截屏帧转换线程
    private ExecutorService executorService = Executors.newFixedThreadPool(7);

    //截屏状态
    private ScreenShotState screenShotState = ScreenShotState.STOP;

    //截屏线程
    private Thread shotThread;

    //截屏区域
    private Rectangle shotArea;

    enum ScreenShotState {
        WAITING,
        SHOTING,
        STOP,
    }

    public ScreenShotManager(int cacheSize, int screenShotInterval) {
        frames = new LinkedBlockingDeque<>(cacheSize);
        screenShotInterval = screenShotInterval;
    }

    public Frame getFrame(long timeout, TimeUnit timeUnit) throws InterruptedException {
        Frame frame = null;
        while (!frames.isEmpty()) {
            frame = frames.poll(timeout, timeUnit);
            if (frame.getData() != null)
                return frame;
        }
        return null;
    }

    public Frame getFrame() throws InterruptedException {
        return getFrame(0, TimeUnit.MILLISECONDS);
    }

    public boolean isShotWaiting() {
        return screenShotState == ScreenShotState.WAITING;
    }

    public boolean isShotStop() {
        return screenShotState == ScreenShotState.STOP;
    }

    public boolean isShoting() {
        return screenShotState == ScreenShotState.SHOTING;
    }

    public void setShotArea(Rectangle shotArea) {
        this.shotArea = shotArea;
    }

    public void setShotArea(int width, int height) {
        setShotArea(new Rectangle(width, height));
    }

    public void setShotArea(int x, int y, int width, int height) {
        setShotArea(new Rectangle(x, y, width, height));
    }

    private void initShotThread() {
        shotThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (isShoting()) {
                            Frame frame = new Frame();
                            frames.put(frame);

                            long spendTime = System.currentTimeMillis();
                            BufferedImage bufferedImage = ScreenShot.screenShot(ScreenShotManager.this.shotArea);
                            spendTime = System.currentTimeMillis() - spendTime;

                            frame.setBufferedImage(bufferedImage);
                            frame.setTime(System.currentTimeMillis());
                            Future future = executorService.submit(new Callable<byte[]>() {
                                @Override
                                public byte[] call() {
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    try {
                                        ImageIO.write(frame.getBufferedImage(), "jpg", baos);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return baos.toByteArray();
                                }
                            });
                            frame.setFuture(future);

                            Thread.sleep(screenShotInterval - spendTime > 0 ? screenShotInterval - spendTime : 0);
                        } else if (isShotWaiting()) {
                            Logger.debug(ScreenShotManager.class, "等待截屏开始...");
                            Thread.sleep(screenShotInterval);
                        } else if (isShotStop()) {
                            Logger.debug(ScreenShotManager.class, "停止截屏...");
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Logger.debug(ScreenShotManager.class, "因中断停止截屏...");
                        break;
                    }
                }
            }
        });
    }

    public void beginShot() {
        if (isShotStop()) {
            initShotThread();
            shotThread.start();
        }
        screenShotState = ScreenShotState.SHOTING;
    }

    public void waitShot(){
        screenShotState = ScreenShotState.WAITING;
    }

    public void stopShot() {
        screenShotState = ScreenShotState.STOP;
        if (executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        if (shotThread.isAlive()) {
            shotThread.interrupt();
        }
    }

}
