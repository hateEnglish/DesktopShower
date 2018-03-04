package com.xubao.client.pojo;

import com.xubao.comment.util.BytesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author xubao
 * @Date 2018/3/4
 */
public class ReceiveFramePiece implements Comparable {
    private static Logger log = LoggerFactory.getLogger(ReceiveFramePiece.class);

    private long dataSize;
    private int dataPieceSize;
    private int frameNumber;
    private int pieceNumber;
    private byte[] dataPiece;

    public ReceiveFramePiece(int frameNumber, int pieceNumber, long dataSize, int dataPieceSize, byte[] dataPiece) {

        log.debug(String.format("dataPieceSize=%d,dataPiece.length=%d",dataPieceSize,dataPiece.length));
        if(dataPieceSize!=dataPiece.length){
            log.debug(String.format("数据异常 dataPieceSize=%d,dataPiece.length=%d",dataPieceSize,dataPiece.length));
        }

        this.dataSize = dataSize;
        this.dataPieceSize = dataPieceSize;
        this.frameNumber = frameNumber;
        this.pieceNumber = pieceNumber;
        this.dataPiece = dataPiece;
    }

//    public ReceiveFramePiece(byte[] completeDataPiece){
//        this.frameNumber = getFrameNumber(completeDataPiece);
//        this.pieceNumber = getPieceNumber(completeDataPiece);
//        int offset = 8;
//
//        byte[] buf = new byte[completeDataPiece.length-offset];
//        System.arraycopy(completeDataPiece,0,buf,offset,buf.length);
//        this.dataPiece = buf;
//    }

    @Override
    public int compareTo(Object o) {
        ReceiveFramePiece receiveFramePiece = (ReceiveFramePiece) o;
        if (receiveFramePiece.getFrameNumber() != this.getFrameNumber()) {
            throw new RuntimeException("帧序号不同,不能比较");
        }

        return this.getPieceNumber() - receiveFramePiece.getPieceNumber();
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public byte[] getDataPiece() {
        return dataPiece;
    }

    public int getPieceNumber() {
        return pieceNumber;
    }

    public long getDataSize() {
        return dataSize;
    }

    public int getDataPieceSize() {
        return dataPieceSize;
    }

    public static int getFrameNumber(byte[] completeDataPiece) {
        return BytesReader.readInt(completeDataPiece, 12);
    }

    public static int getPieceNumber(byte[] completeDataPiece) {
        return BytesReader.readInt(completeDataPiece, 16);
    }
}
