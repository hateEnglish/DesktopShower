package com.xubao.server.multicast;

import com.xubao.comment.config.CommentConfig;
import com.xubao.comment.util.NetAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @Author xubao
 * @Date 2018/2/22
 */
public class Multicast {
    private static Logger log = LoggerFactory.getLogger(Multicast.class);


    private Channel ch;
    private EventLoopGroup loopGroup;
    private InetSocketAddress groupAddress;
    private SocketAddress localAddress;

    private Bootstrap bootstrap;

    private int muiticastPort = CommentConfig.getInstance().getProperInt("server.default_multicast_port");

    private Thread multicastThread;

    public Multicast(InetSocketAddress groupAddress) throws Exception {
        this.groupAddress = groupAddress;
        if (groupAddress.getPort() != 0) {
            muiticastPort = groupAddress.getPort();
        }
        localAddress = new InetSocketAddress(NetAddress.getLocalHostLANAddress(), muiticastPort);
    }


    private void init() throws InterruptedException {

        bootstrap = new Bootstrap();
        bootstrap.localAddress(localAddress)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new MuiticastHandler());
        ch = bootstrap.bind().sync().channel();

        ch.writeAndFlush(new DatagramPacket(
                Unpooled.copiedBuffer("QOTM?", CharsetUtil.UTF_8),
                groupAddress));
    }

    public void initMulticastThread(){
        multicastThread = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }



    private static class MuiticastHandler extends SimpleChannelInboundHandler<DatagramPacket> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
            log.debug("受到组播消息");
        }
    }
}
