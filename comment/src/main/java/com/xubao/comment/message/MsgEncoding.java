package com.xubao.comment.message;

import com.google.protobuf.Message;
import com.xubao.comment.proto.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author xubao
 * @Date 2018/2/17
 */
public class MsgEncoding {
    private static Logger log = LoggerFactory.getLogger(MsgEncoding.class);
    public static Connection.BaseMsg encode(Message msg){
        Connection.BaseMsg.Builder builder = Connection.BaseMsg.newBuilder();
        builder.setMsgClassName(msg.getClass().getName());
        log.debug("msgClassName={}",msg.getClass().getName());

        builder.setMsg(msg.toByteString());

        return builder.build();
    }
}
