package com.xubao.server.pojo;

import com.xubao.comment.contentStruct.Content;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @Author xubao
 * @Date 2018/2/5
 */
public class Frame implements Content {
    private long time;
    private byte[] data;
    private BufferedImage bufferedImage;
    private Future<byte[]> future;
    //鼠标位置
    private Point mousePoint;

    //开始截屏位置
    private Point startCoord;
    //结束截屏位置
    private Point endCoord;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public byte[] getData() {
        try {
            data = future.get();
        } catch (InterruptedException e) {

        } catch (ExecutionException e) {

        }
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public Future<byte[]> getFuture() {
        return future;
    }

    public void setFuture(Future<byte[]> future) {
        this.future = future;
    }

    public Point getMousePoint() {
        return mousePoint;
    }

    public void setMousePoint(Point mousePoint) {
        this.mousePoint = mousePoint;
    }

    public Point getStartCoord() {
        return startCoord;
    }

    public void setStartCoord(Point startCoord){
        this.startCoord = startCoord;
    }
    public void setStartCoord(int x, int y) {
        this.startCoord = new Point(x, y);
    }

    public Point getEndCoord() {
        return endCoord;
    }

    public void setEndCoord(int x, int y) {
        this.endCoord = new Point(x, y);
    }
    public void setEndCoord(Point endCoord){
        this.endCoord = endCoord;
    }
}
