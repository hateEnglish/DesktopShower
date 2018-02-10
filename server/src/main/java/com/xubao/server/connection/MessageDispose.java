package com.xubao.server.connection;

import com.xubao.comment.config.CommentConfig;
import com.xubao.comment.log.Logger;
import com.xubao.comment.proto.Connection;
import com.xubao.server.connection.messageHandler.HeartbeatHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * @Author xubao
 * @Date 2018/2/9
 */
public class MessageDispose {

    private ServerBootstrap serverBootstrap;
    private int connPort = Integer.parseInt(CommentConfig.getProper("server.conn_port"));
    private EventLoopGroup boss;
    private EventLoopGroup worker;


    public void startMsgDispose() throws InterruptedException {
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();

        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss,worker);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.option(ChannelOption.SO_BACKLOG,100);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                ch.pipeline().addLast(new ProtobufDecoder(Connection.Heartbeat.getDefaultInstance()));
                ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                ch.pipeline().addLast(new ProtobufEncoder());

                ch.pipeline().addLast(new HeartbeatHandler());
            }
        });

        ChannelFuture f = serverBootstrap.bind(connPort).sync();
        f.channel().closeFuture().sync();
    }

    public void stopMsgDispose(){
        Logger.debug(MessageDispose.class,"关闭消息处理器");
        boss.shutdownGracefully();
        worker.shutdownGracefully();
        //f.channel().close();
    }

    public static void main(String[] args){
        MessageDispose msgDis = new MessageDispose();
        try {
            msgDis.startMsgDispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       // msgDis.stopMsgDispose();
    }
}
