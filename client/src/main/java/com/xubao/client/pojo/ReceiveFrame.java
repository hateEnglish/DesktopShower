package com.xubao.client.pojo;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author xubao
 * @Date 2018/3/3
 */
public class ReceiveFrame implements Comparable {
    private long frameSize;
    private int frameNumber;
    private long receiveTime;

    private List<ReceiveFramePiece> framePieceList = new ArrayList<>();

    public ReceiveFrame(int frameNumber,long frameSize){
        this.frameNumber = frameNumber;
        this.frameSize = frameSize;
        receiveTime=System.currentTimeMillis();
    }

    public ReceiveFrame(int frameNumber){
        this.frameNumber = frameNumber;
        receiveTime=System.currentTimeMillis();
    }

    public void addFramePiece(ReceiveFramePiece framePiece){
        int i = framePieceList.size();
        for(;i>=1;i--){
            if(framePieceList.get(i-1).compareTo(framePiece)<0){
                break;
            }
        }
        framePieceList.add(i,framePiece);
    }

    public List<ReceiveFramePiece> getFramePieceList() {
        return framePieceList;
    }

    public void setFramePieceList(List<ReceiveFramePiece> framePieceList) {
        this.framePieceList = framePieceList;
    }

    public boolean isFull(){
        long pieceSizeSum = 0;
        for(ReceiveFramePiece framePiece:framePieceList){
            pieceSizeSum+=framePiece.getDataPieceSize();
        }

        if(pieceSizeSum>frameSize){
            throw new RuntimeException(String.format("碎片长度总和超过数据总长度 pieceSizeSum=%d,frameSize=%d",pieceSizeSum,frameSize));
        }

        return pieceSizeSum==frameSize;
    }

    public void writeData(OutputStream os) throws IOException
    {
        writeData(new BufferedOutputStream(os));
    }

    public void writeData(BufferedOutputStream bos) throws IOException
    {
        for(ReceiveFramePiece framePiece:framePieceList){
            bos.write(framePiece.getDataPiece());
        }
        bos.flush();
    }
    @Override
    public int compareTo(Object o) {
        ReceiveFrame receiveFrame = (ReceiveFrame)o;
        return this.frameNumber-receiveFrame.frameNumber;
    }

    public long getFrameNumber() {
        return frameNumber;
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public long getFrameSize() {
        return frameSize;
    }

    public void setFrameSize(long frameSize) {
        this.frameSize = frameSize;
    }

    @Override
    public String toString() {
        return "ReceiveFrame{" +
                "frameSize=" + frameSize +
                ", frameNumber=" + frameNumber +
                ", receiveTime=" + receiveTime +
                '}';
    }
}
