package com.xubao.comment.message;

import com.google.protobuf.Message;
import com.xubao.comment.log.Logger;
import com.xubao.comment.proto.Connection;

/**
 * @Author xubao
 * @Date 2018/2/17
 */
public class MsgEncoding {
    public static Connection.BaseMsg encode(Message msg){
        Connection.BaseMsg.Builder builder = Connection.BaseMsg.newBuilder();
        builder.setMsgClassName(msg.getClass().getName());
        Logger.debug(MsgEncoding.class,"msgClassName=%s",msg.getClass().getName());

        builder.setMsg(msg.toByteString());

        return builder.build();
    }
}
