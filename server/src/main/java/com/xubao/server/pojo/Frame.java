package com.xubao.server.pojo;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @Author xubao
 * @Date 2018/2/5
 */
public class Frame {
    private long time;
    private byte[] data;
    private BufferedImage bufferedImage;
    private Future<byte[]> future;

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
}
