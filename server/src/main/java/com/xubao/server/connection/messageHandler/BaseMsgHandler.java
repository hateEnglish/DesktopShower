package com.xubao.server.connection.messageHandler;

import com.google.protobuf.Message;
import com.xubao.comment.message.MsgDecoding;
import com.xubao.comment.proto.Connection;
import com.xubao.server.manager.ServerInfoManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author xubao
 * @Date 2018/2/17
 */
public class BaseMsgHandler extends SimpleChannelInboundHandler<Connection.BaseMsg> {
    private static Logger log = LoggerFactory.getLogger(BaseMsgHandler.class);
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("有客户端连入");
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Connection.BaseMsg msg) throws Exception {
        log.debug("收到消息:"+msg);
        Message relMsg = MsgDecoding.decode(msg);
        ServerInfoManager.getInstance().processorProvider.processor(ctx,relMsg);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("与服务器的连接断开");
    }
}
