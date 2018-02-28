package com.xubao.client.multicastReceive;

import com.xubao.comment.config.CommentConfig;
import com.xubao.comment.util.NetAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author xubao
 * @version 1.0
 * @since 2018/2/28
 */
public class MulticastReceive
{
	private static Logger log = LoggerFactory.getLogger(MulticastReceive.class);

	private EventLoopGroup loopGroup;
	private InetSocketAddress localAddress;
	private InetSocketAddress groupAddress;
	private int muiticastPort = CommentConfig.getInstance().getProperInt("server.default_multicast_port");

	public MulticastReceive(InetSocketAddress groupAddress) throws Exception {
		this.groupAddress = groupAddress;
		if (groupAddress.getPort() != 0) {
			muiticastPort = groupAddress.getPort();
		}
		localAddress = new InetSocketAddress(NetAddress.getLocalHostLANAddress(), muiticastPort);
	}

	private void init() throws InterruptedException
	{
		loopGroup = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(loopGroup)
				.channel(NioDatagramChannel.class)
				.localAddress(localAddress)
				.option(ChannelOption.IP_MULTICAST_IF, NetUtil.LOOPBACK_IF)
				.option(ChannelOption.SO_REUSEADDR, true)
				.handler(new Handler());

		NioDatagramChannel ch = (NioDatagramChannel)bootstrap.bind(groupAddress.getPort()).sync().channel();
		ch.joinGroup(groupAddress, NetUtil.LOOPBACK_IF).sync();
		System.out.println("server");
	}

	private static class Handler extends SimpleChannelInboundHandler<DatagramPacket>{

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception
		{
			log.debug("收到组播消息");
		}
	}


	public static void main(String[] args) throws Exception
	{
		InetSocketAddress groupAddress = new InetSocketAddress(CommentConfig.getInstance().getProper("server.multicast_address"),CommentConfig.getInstance().getProperInt("server.default_multicast_port"));
		MulticastReceive multicast = new MulticastReceive(groupAddress);
		multicast.init();
	}
}
