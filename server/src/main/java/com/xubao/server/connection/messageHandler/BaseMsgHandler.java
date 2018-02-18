package com.xubao.server.connection.messageHandler;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.xubao.comment.message.MsgDecoding;
import com.xubao.comment.proto.Connection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author xubao
 * @Date 2018/2/17
 */
public class BaseMsgHandler extends SimpleChannelInboundHandler<Connection.BaseMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Connection.BaseMsg msg) throws Exception {
        ByteString msg1 = msg.getMsg();
        Message decode = MsgDecoding.decode(msg);
        System.out.println("-------------------------");
        System.out.println(msg.getMsgClassName());
        System.out.println(decode);
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxx");
//        Message decode1 = MsgDecoding.decode(Connection.Heartbeat.getDefaultInstance(), msg1);
//        System.out.println("----------------------------");
//        System.out.println(decode1);
//        System.out.println("yyyyyyyyyyyyyyyyyyyyyyyyyyyy");
    }
}
