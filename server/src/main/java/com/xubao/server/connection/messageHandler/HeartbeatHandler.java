package com.xubao.server.connection.messageHandler;

import com.xubao.comment.log.Logger;
import com.xubao.comment.proto.Connection;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author xubao
 * @Date 2018/2/10
 */
public class HeartbeatHandler extends SimpleChannelInboundHandler<Connection.Heartbeat>{


    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Logger.debug(HeartbeatHandler.class,"有客户端连接地址=%s",ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    public void channelRead0(ChannelHandlerContext ctx, Connection.Heartbeat msg) throws Exception {
        Logger.debug(HeartbeatHandler.class,"收到心跳消息地址=%s",ctx.channel().remoteAddress());
        Logger.debug(HeartbeatHandler.class,"msg="+msg.toString());
    }

}
