package com.xubao.server.connection;

import com.xubao.comment.config.CommentConfig;
import com.xubao.comment.proto.Connection;
import com.xubao.server.connection.messageHandler.BaseMsgHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author xubao
 * @Date 2018/2/9
 */
public class MessageDispose {

    private static Logger log = LoggerFactory.getLogger(MessageDispose.class);

    private ServerBootstrap serverBootstrap;
    private int connPort = CommentConfig.getInstance().getProperInt("server.conn_port");
    private EventLoopGroup boss;
    private EventLoopGroup worker;


    public void startMsgDispose() throws InterruptedException {
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();

        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 100);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                ch.pipeline().addLast(new ProtobufDecoder(Connection.BaseMsg.getDefaultInstance()));
                ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                ch.pipeline().addLast(new ProtobufEncoder());

                ch.pipeline().addLast(new BaseMsgHandler());
            }
        });

        ChannelFuture f = serverBootstrap.bind(connPort);//.sync();
        f.channel().closeFuture();//.sync();
    }

    public void stopMsgDispose() {
        log.debug("关闭消息处理器");
        boss.shutdownGracefully();
        worker.shutdownGracefully();
        //f.channel().close();
    }

    public static void main(String[] args) {
        CommentConfig commentConfig = new CommentConfig();
        MessageDispose msgDis = new MessageDispose();
        try {
            msgDis.startMsgDispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // msgDis.stopMsgDispose();
    }
}
