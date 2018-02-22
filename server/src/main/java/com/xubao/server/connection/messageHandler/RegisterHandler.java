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
// * @Date 2018/2/17
// */
//public class RegisterHandler extends SimpleChannelInboundHandler<Connection.Register> {
//
//    private static Logger log = LoggerFactory.getLogger(RegisterHandler.class);
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        log.debug("有客户端连接");
//        super.channelActive(ctx);
//    }
//
//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, Connection.Register msg) throws Exception {
//        log.debug("收到注册消息");
//    }
//}
