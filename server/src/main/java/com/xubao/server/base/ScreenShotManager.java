package com.xubao.server.base;

import com.xubao.comment.config.CommentConfig;
import com.xubao.comment.contentStruct.Content;
import com.xubao.comment.contentStruct.ContentProvider;
import com.xubao.server.pojo.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.*;

/**
 * @Author xubao
 * @Date 2018/2/5
 */
public class ScreenShotManager implements ContentProvider{
    private static Logger log = LoggerFactory.getLogger(ScreenShotManager.class);

    private BlockingQueue<Frame> frames;
    //截屏时间间隔(毫秒)
    private int screenShotInterval;

    private int shotThreadNumber = CommentConfig.getInstance().getProperInt("server.shot_thread");

    //截屏帧转换线程
    private ExecutorService executorService = Executors.newFixedThreadPool(shotThreadNumber);

    //截屏状态
    private ScreenShotState screenShotState = ScreenShotState.STOP;

    //截屏线程
    private Thread shotThread;

    //截屏区域
    private Rectangle shotArea;

    //帧编号
    private int frameNumber=0;

    @Override
    public Content getContent() throws InterruptedException {
        return getFrame();
    }

    public enum ScreenShotState {
        WAITING,
        SHOTING,
        STOP,
    }

    public ScreenShotManager(int cacheSize, int screenShotInterval,Rectangle shotArea) {
        frames = new LinkedBlockingDeque<>(cacheSize);
        this.screenShotInterval = screenShotInterval;
        this.shotArea = shotArea;
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

    public int getNextContentNumber(){
        return (frameNumber++)&Integer.MAX_VALUE;
    }

    public Frame getFrame() throws InterruptedException {
        return getFrame(20, TimeUnit.MILLISECONDS);
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
                int i = 0;
                while (true) {
                    try {
                        if (isShoting()) {
                            Frame frame = new Frame();

                            i++;
                            System.out.println("i="+i);
                            long spendTime = System.currentTimeMillis();
                            System.out.println("开始处理时间="+spendTime);
                            BufferedImage bufferedImage = ScreenShot.screenShot(ScreenShotManager.this.shotArea);
                            spendTime = System.currentTimeMillis() - spendTime;
                            System.out.println("i="+i);
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
                            frames.put(frame);
                            System.out.println("i="+i);
                            System.out.println("结束处理时间="+System.currentTimeMillis());
                            System.out.println("睡眠时间="+(screenShotInterval - spendTime));
                            Thread.sleep(screenShotInterval - spendTime > 0 ? screenShotInterval - spendTime : 0);
                        } else if (isShotWaiting()) {
                            log.debug("等待截屏开始...");
                            Thread.sleep(screenShotInterval);
                        } else if (isShotStop()) {
                            log.debug("停止截屏...");
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        log.debug("因中断停止截屏...");
                        break;
                    }
                }
            }
        });
    }

    private void addMsgHeader(OutputStream os){
        DataOutputStream dataOutputStream = new DataOutputStream(os);
        try {
            dataOutputStream.writeInt(getNextContentNumber());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void main(String[] args){
        ScreenShotManager screenShotManager = new ScreenShotManager(2,3,new Rectangle());
        System.out.println(screenShotManager.getNextContentNumber());
        System.out.println(screenShotManager.getNextContentNumber());
        System.out.println(screenShotManager.getNextContentNumber());
        System.out.println(screenShotManager.getNextContentNumber());
        System.out.println(screenShotManager.getNextContentNumber());
    }

}
