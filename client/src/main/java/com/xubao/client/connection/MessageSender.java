package com.xubao.client.connection;

import com.google.protobuf.Message;
import com.xubao.comment.config.CommentConfig;
import com.xubao.comment.message.MsgEncoding;
import com.xubao.comment.proto.Connection;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author xubao
 * @Date 2018/2/18
 */
public class MessageSender {

    Logger log = LoggerFactory.getLogger(MessageSender.class);

    private static MessageSender messageSender = new MessageSender();

    public static MessageSender getInstance() {
        return messageSender;
    }

    private ChannelHandlerContext ctx;

    private Map<LongTimeSendMessage, Thread> longTimeThreadMap = new HashMap();

    public enum LongTimeSendMessage {
        HEARTBEAT,
    }

    static {
        Connection.Heartbeat.Builder builder = Connection.Heartbeat.newBuilder();
        builder.setTime(System.currentTimeMillis());
        messageSender.createLongTimeMsgSendThread(LongTimeSendMessage.HEARTBEAT, builder.build(), CommentConfig.getInstance().getProperInt("client.heartbeat_interval"));
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void sendMsg(Message msg) {
        ctx.write(MsgEncoding.encode(msg));
    }

    public void sendMsgAndFlush(Message msg) {
        ctx.writeAndFlush(MsgEncoding.encode(msg));
    }

    public void createLongTimeMsgSendThread(LongTimeSendMessage type, Message msg, final int interval) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                    sendMsgAndFlush(msg);
                }
            }
        });

        if (longTimeThreadMap.get(type) != null) {
            longTimeThreadMap.get(type).interrupt();
        }

        longTimeThreadMap.put(type, thread);
    }

    public void startSendThread(LongTimeSendMessage type) {
        longTimeThreadMap.get(type).start();
    }

    public void stopSendThread(LongTimeSendMessage type){
        if(longTimeThreadMap.get(type)!=null){
            log.debug("停止消息发送type={}",type);
            longTimeThreadMap.get(type).interrupt();
        }
    }

    public void stopAllSendThread(){
        for(Map.Entry<LongTimeSendMessage,Thread> entry: longTimeThreadMap.entrySet()){
            entry.getValue().interrupt();
        }
    }

}
