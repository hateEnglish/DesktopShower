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

    private BaseMsgHandler baseMsgHandler = new BaseMsgHandler();

    private ProtobufVarint32FrameDecoder pvfd = new ProtobufVarint32FrameDecoder();
    private ProtobufDecoder pd = new ProtobufDecoder(Connection.BaseMsg.getDefaultInstance());
    private ProtobufVarint32LengthFieldPrepender pvlfp = new ProtobufVarint32LengthFieldPrepender();
    private ProtobufEncoder pe = new ProtobufEncoder();

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
                ch.pipeline().addLast(pvfd);
                ch.pipeline().addLast(pd);
                ch.pipeline().addLast(pvlfp);
                ch.pipeline().addLast(pe);

                ch.pipeline().addLast(baseMsgHandler);
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
