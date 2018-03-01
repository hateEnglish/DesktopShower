package com.xubao.server.multicast;

import com.xubao.comment.config.CommentConfig;
import com.xubao.comment.util.NetAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
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
        localAddress = new InetSocketAddress(NetAddress.getLocalHostLANAddress(), groupAddress.getPort());
    }


    private void init() throws InterruptedException {

        loopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(loopGroup)
               .localAddress(localAddress)
                .channelFactory(new ChannelFactory<NioDatagramChannel>() {

                    public NioDatagramChannel newChannel() {
                        return new NioDatagramChannel(InternetProtocolFamily.IPv4);
                    }
                })
                .option(ChannelOption.IP_MULTICAST_IF, NetUtil.LOOPBACK_IF)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new MuiticastHandler());
        ch = bootstrap.bind(0).await().channel();

        ch.writeAndFlush(new DatagramPacket(
                Unpooled.copiedBuffer("8888888888888888888", CharsetUtil.UTF_8),
                groupAddress));
        System.out.println("发送结束");
        ch.close().awaitUninterruptibly();
    }

    public void initMulticastThread() {
        multicastThread = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }


    private static class MuiticastHandler extends SimpleChannelInboundHandler<DatagramPacket> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
            log.debug("收到组播消息");
        }
    }

    public static void main(String[] args) throws Exception {
        String multicastHost = CommentConfig.getInstance().getProper("server.multicast_address");
        int multicastPort = CommentConfig.getInstance().getProperInt("server.default_multicast_port");

        InetSocketAddress groupAddress = new InetSocketAddress(multicastHost, multicastPort);
        Multicast multicast = new Multicast(groupAddress);
        multicast.init();
    }
}
