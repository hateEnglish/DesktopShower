package com.xubao.client.connection.messageHandler;

import com.google.protobuf.Message;
import com.xubao.client.connection.MessageSender;
import com.xubao.client.manager.ClientInfoManager;
import com.xubao.comment.message.MsgDecoding;
import com.xubao.comment.proto.Connection;
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
        System.out.println("baseMsgHandler");
        MessageSender.getInstance().setCtx(ctx);
        Connection.Register.Builder builder = Connection.Register.newBuilder();
        builder.setNickName(ClientInfoManager.getInstance().getNickName());
        MessageSender.getInstance().sendMsgAndFlush(builder.build());

        MessageSender.getInstance().startSendThread(MessageSender.LongTimeSendMessage.HEARTBEAT);

        ClientInfoManager.getInstance().setConnServerState(ClientInfoManager.ConnServerState.CONNECTING);

        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Connection.BaseMsg msg) throws Exception {
        Message relMsg = MsgDecoding.decode(msg);
        // ProcessorCollector.getInstance().processor(ctx,relMsg);
        ClientInfoManager.getInstance().processorProvider.processor(ctx, relMsg);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("与服务器的连接断开");
        ClientInfoManager.getInstance().setConnServerState(ClientInfoManager.ConnServerState.DISCONNECT);
        MessageSender.getInstance().stopAllSendThread();
        super.channelUnregistered(ctx);
    }
}
