package com.xubao.client.manager;

import com.xubao.client.pojo.ReceiveFrame;
import com.xubao.client.pojo.ReceiveFramePiece;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author xubao
 * @Date 2018/3/3
 */
public class FrameManager {
    private static FrameManager frameManager = new FrameManager();

    public static FrameManager getInstance() {
        return frameManager;
    }

    private int frameCacheSize;
    //按帧顺序保存帧
    private List<ReceiveFrame> receiveFrameList = new ArrayList<>();

    /**
     * 按帧顺序存储
     */
    public void addFrame(ReceiveFrame frame) {
        int i = receiveFrameList.size();
        for (; i >= 1; i--) {
            if (receiveFrameList.get(i - 1).compareTo(frame) > 0) {
                break;
            }
        }

        receiveFrameList.add(i, frame);
    }

    public void addFramePiece(ReceiveFramePiece framePiecec) {
        ReceiveFrame frame = getFrame(framePiecec.getFrameNumber());
        if (frame == null) {
            frame = new ReceiveFrame(framePiecec.getFrameNumber(),framePiecec.getDataSize());
        }
        frame.addFramePiece(framePiecec);
        addFrame(frame);
    }

    public ReceiveFrame getAndRemoveFirstFrame() {
        if (receiveFrameList.size() == 0) {
            return null;
        }
        ReceiveFrame receiveFrame = receiveFrameList.remove(0);
        return receiveFrame;
    }

    public ReceiveFrame getFrame(int frameNumber) {
        for (ReceiveFrame frame : receiveFrameList) {
            if (frame.getFrameNumber() == frameNumber) {
                return frame;
            }
        }

        return null;
    }

    public ReceiveFrame getFirstFullFrameAndRemoveUnFull() {
        Iterator<ReceiveFrame> iterator = receiveFrameList.iterator();
        while(iterator.hasNext()){
            ReceiveFrame next = iterator.next();
            if(next.isFull()){
                return next;
            }else{
                iterator.remove();
            }
        }

        return null;
    }

    public ReceiveFrame findFirstFullFrame(){
        for(ReceiveFrame frame:receiveFrameList){
            if(frame.isFull()){
                return frame;
            }
        }

        return null;
    }

    public static void main(String[] args) {
        FrameManager.getInstance().addFrame(new ReceiveFrame(3));
        FrameManager.getInstance().addFrame(new ReceiveFrame(2));
        FrameManager.getInstance().addFrame(new ReceiveFrame(1));
        FrameManager.getInstance().addFrame(new ReceiveFrame(4));
        FrameManager.getInstance().addFrame(new ReceiveFrame(2));
        FrameManager.getInstance().addFrame(new ReceiveFrame(3));

        System.out.println(FrameManager.getInstance().getAndRemoveFirstFrame());
        System.out.println(FrameManager.getInstance().getAndRemoveFirstFrame());
        System.out.println(FrameManager.getInstance().getAndRemoveFirstFrame());
        System.out.println(FrameManager.getInstance().getAndRemoveFirstFrame());
        System.out.println(FrameManager.getInstance().getAndRemoveFirstFrame());
        System.out.println(FrameManager.getInstance().getAndRemoveFirstFrame());
        System.out.println(FrameManager.getInstance().getAndRemoveFirstFrame());

        System.out.println(FrameManager.getInstance().getAndRemoveFirstFrame());
        System.out.println(FrameManager.getInstance().getAndRemoveFirstFrame());

    }
}
