package com.xubao.server.connection.messageHandler;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.xubao.comment.message.MsgDecoding;
import com.xubao.comment.proto.Connection;
import com.xubao.server.connection.ProcessorCollector;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author xubao
 * @Date 2018/2/17
 */
public class BaseMsgHandler extends SimpleChannelInboundHandler<Connection.BaseMsg> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有客户端连入");
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Connection.BaseMsg msg) throws Exception {
        System.out.println("收到消息");
        Message relMsg = MsgDecoding.decode(msg);
        ProcessorCollector.getInstance().processor(ctx,relMsg);
    }
}
