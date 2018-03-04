package com.xubao.comment.util;

/**
 * @Author xubao
 * @Date 2018/3/4
 */
public final class BytesReader {
    private BytesReader(){}

    public static int readInt(byte[] src,int srcPos){
        checkBytesLeng(src,srcPos,"int",4);
        int ch1 = src[srcPos];
        int ch2 = src[srcPos+1];
        int ch3 = src[srcPos+2];
        int ch4 = src[srcPos+3];
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
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
