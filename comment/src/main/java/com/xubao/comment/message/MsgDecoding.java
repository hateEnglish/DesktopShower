package com.xubao.comment.message;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.xubao.comment.proto.Connection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author xubao
 * @Date 2018/2/17
 */
public class MsgDecoding {
    public static <T extends Message> T decode(Connection.BaseMsg baseMsg) throws InvalidProtocolBufferException, ClassNotFoundException {

        String msgClassName = baseMsg.getMsgClassName();
        Class<?> clazz = Class.forName(msgClassName);
        Method method = null;
        T defInstance = null;
        try {
            method = clazz.getMethod("getDefaultInstance");
            defInstance = (T) method.invoke(null);
        } catch (NoSuchMethodException e) {
            //忽略
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        Message prototype = defInstance.getDefaultInstanceForType();
        byte[] buf = baseMsg.getMsg().toByteArray();
        MessageLite messageLite = prototype.getParserForType().parseFrom(buf, 0, buf.length);
        return (T)messageLite;
    }
}
