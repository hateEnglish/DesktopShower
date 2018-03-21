package com.xubao.client.connection;

import com.xubao.client.connection.messageHandler.BaseMsgHandler;
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
public class ConnServer {

    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private int serverConnPort = Integer.parseInt(CommentConfig.getInstance().getProper("server.conn_port"));
    private String serverConnIP = "127.0.0.1";

    private BaseMsgHandler baseMsgHandler = new BaseMsgHandler();

    private ProtobufVarint32FrameDecoder pvfd = new ProtobufVarint32FrameDecoder();
    private ProtobufDecoder pd = new ProtobufDecoder(Connection.BaseMsg.getDefaultInstance());
    private ProtobufVarint32LengthFieldPrepender pvlfp = new ProtobufVarint32LengthFieldPrepender();
    private ProtobufEncoder pe = new ProtobufEncoder();

    public ConnServer(){}

    public ConnServer(String serverConnIP){
        this.serverConnIP = serverConnIP;
    }

    public ConnServer(String serverIP, int serverConnPort){
        this.serverConnIP = serverIP;
        this.serverConnPort = serverConnPort;
    }

    public void connect() throws InterruptedException {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY,true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(pvfd);
                ch.pipeline().addLast(pd);
                ch.pipeline().addLast(pvlfp);
                ch.pipeline().addLast(pe);

                ch.pipeline().addLast(baseMsgHandler);
            }
        });
        ChannelFuture f = bootstrap.connect(serverConnIP, serverConnPort);//.sync();
        //f.channel().closeFuture().sync();
    }

    public void stopConn(){
        group.shutdownGracefully();
    }

    public static void main(String[] args){

        ConnServer connServer = new ConnServer();
        try {
            connServer.connect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
