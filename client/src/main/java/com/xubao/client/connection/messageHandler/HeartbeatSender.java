package com.xubao.client.connection.messageHandler;

import com.xubao.comment.log.Logger;
import com.xubao.comment.message.MsgEncoding;
import com.xubao.comment.proto.Connection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author xubao
 * @Date 2018/2/10
 */
public class HeartbeatSender extends SimpleChannelInboundHandler<Connection.HeartbeatResponse> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Logger.debug(HeartbeatSender.class,"连接服务端成功地址=%s",ctx.channel().remoteAddress());

        Connection.Register.Builder builder1 = Connection.Register.newBuilder();
       // builder1.setNickName("徐豹");
        ctx.write(MsgEncoding.encode(builder1.build()));

        Connection.Heartbeat.Builder builder = Connection.Heartbeat.newBuilder();
        builder.setInfo("info");
        ctx.writeAndFlush(MsgEncoding.encode(builder.build()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Connection.HeartbeatResponse msg) throws Exception {
        Logger.debug(HeartbeatSender.class,"收到心跳返回地址=%s",ctx.channel().remoteAddress());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
