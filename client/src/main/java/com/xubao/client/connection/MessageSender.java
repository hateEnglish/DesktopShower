package com.xubao.client.connection;

import com.xubao.client.connection.messageHandler.HeartbeatSender;
import com.xubao.comment.config.CommentConfig;
import com.xubao.comment.proto.Connection;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * @Author xubao
 * @Date 2018/2/10
 */
public class MessageSender {

    Bootstrap bootstrap;
    EventLoopGroup group;
    private int serverConnPort = Integer.parseInt(CommentConfig.getProper("server.conn_port"));

    public void connect() throws InterruptedException {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY,true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                ch.pipeline().addLast(new ProtobufDecoder(Connection.HeartbeatResponse.getDefaultInstance()));
                ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                ch.pipeline().addLast(new ProtobufEncoder());


                ch.pipeline().addLast(new HeartbeatSender());
            }
        });
        ChannelFuture f = bootstrap.connect("127.0.0.1", serverConnPort).sync();
        f.channel().closeFuture().sync();
    }

    public static void main(String[] args){
        MessageSender messageSender = new MessageSender();
        try {
            messageSender.connect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
