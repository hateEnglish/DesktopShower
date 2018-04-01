package com.xubao.comment.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author xubao
 * @Date 2018/3/4
 */
public final class BytesReader {
    private BytesReader(){}

    public static int readInt(byte[] src, int srcPos, AtomicInteger offsetAdd){
        if(offsetAdd!=null) {
            offsetAdd.addAndGet(4);
        }
        checkBytesLeng(src,srcPos,"int",4);
        int ch1 = src[srcPos];
        int ch2 = src[srcPos+1];
        int ch3 = src[srcPos+2];
        int ch4 = src[srcPos+3];
        int tmp = 0;
        tmp = tmp|(ch1<<24)&0xff000000;
        tmp = tmp|(ch2<<16)&0xff0000;
        tmp = tmp|(ch3<<8)&0xff00;
        tmp = tmp|ch4&0xff;
        return tmp;
    }

    public static int readInt(byte[] src,int srcPos){
        return readInt(src,srcPos,null);
    }

    public static long readLong(byte[] src,int srcPos){
        checkBytesLeng(src,srcPos,"long",8);
        return (((long)src[srcPos] << 56) +
                ((long)(src[srcPos+1] & 255) << 48) +
                ((long)(src[srcPos+2] & 255) << 40) +
                ((long)(src[srcPos+3] & 255) << 32) +
                ((long)(src[srcPos+4] & 255) << 24) +
                ((src[srcPos+5] & 255) << 16) +
                ((src[srcPos+6] & 255) <<  8) +
                ((src[srcPos+7] & 255) <<  0));
    }

    private static void checkBytesLeng(byte[] src,int srcPos,String type,int typeLeng){
        if(src.length<srcPos+4){
            throw new RuntimeException("位数不足无法读取"+type+"需"+typeLeng+"字节 src.length="+src.length+"srcPost="+srcPos);
        }
    }
}
