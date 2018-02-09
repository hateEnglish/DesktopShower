package com.xubao.server.connection;

import com.xubao.comment.config.CommentConfig;
import com.xubao.comment.log.Logger;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

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
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast();
            }
        });

        ChannelFuture f = serverBootstrap.bind(connPort);
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

        msgDis.stopMsgDispose();
    }
}
