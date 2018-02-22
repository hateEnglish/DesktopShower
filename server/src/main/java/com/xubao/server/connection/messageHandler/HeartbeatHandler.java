//package com.xubao.server.connection.messageHandler;
//
//import com.xubao.comment.proto.Connection;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * @Author xubao
// * @Date 2018/2/10
// */
//public class HeartbeatHandler extends SimpleChannelInboundHandler<Connection.Heartbeat>{
//
//    private static Logger log = LoggerFactory.getLogger(HeartbeatHandler.class);
//
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        log.debug("有客户端连接地址={}",ctx.channel().remoteAddress());
//        super.channelActive(ctx);
//    }
//
//    public void channelRead0(ChannelHandlerContext ctx, Connection.Heartbeat msg) throws Exception {
//        log.debug("收到心跳消息地址={}",ctx.channel().remoteAddress());
//        log.debug("msg={}",msg.toString());
//    }
//
//}
