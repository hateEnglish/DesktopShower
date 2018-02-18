package com.xubao.server.connection.messageHandler;

import com.xubao.comment.log.Logger;
import com.xubao.comment.proto.Connection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author xubao
 * @Date 2018/2/17
 */
public class RegisterHandler extends SimpleChannelInboundHandler<Connection.Register> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Logger.debug(RegisterHandler.class,"有客户端连接");
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Connection.Register msg) throws Exception {
        Logger.debug(RegisterHandler.class,"收到注册消息");
    }
}
