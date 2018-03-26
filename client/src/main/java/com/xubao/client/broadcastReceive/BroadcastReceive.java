package com.xubao.client.broadcastReceive;

import com.google.protobuf.Message;
import com.xubao.client.connection.ProcessorCollector;
import com.xubao.client.manager.ClientInfoManager;
import com.xubao.client.manager.ServerManager;
import com.xubao.client.pojo.ServerInfo;
import com.xubao.comment.config.CommentConfig;
import com.xubao.comment.message.MsgDecoding;
import com.xubao.comment.proto.Connection;
import com.xubao.comment.util.NetAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.codec.DatagramPacketEncoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @Author xubao
 * @Date 2018/2/19
 */
public class BroadcastReceive {
    private int broadcastPort = CommentConfig.getInstance().getProperInt("server.broadcast_port");

    private DatagramPacketDecoder dpd = new DatagramPacketDecoder(new ProtobufDecoder(Connection.BaseMsg.getDefaultInstance()));
    private DatagramPacketEncoder dpe = new DatagramPacketEncoder<Connection.BaseMsg>(new ProtobufEncoder());

    private InetAddress localAddress;

    public void initServer() throws Exception {//udp服务端，接受客户端发送的广播

        localAddress = NetAddress.getLocalHostLANAddress();
        try {
            Bootstrap b = new Bootstrap();
            EventLoopGroup group = new NioEventLoopGroup();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        @Override
                        protected void initChannel(DatagramChannel ch) throws Exception {
                            //ch.pipeline().addLast(dpd);
                            // ch.pipeline().addLast(dpe);
                            ch.pipeline().addLast(new UdpServerHandler());
                            // ch.pipeline().addLast(new MsgHandler());
                        }
                    });
            b.bind(localAddress, broadcastPort).sync().channel().closeFuture();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
        private static Logger log = LoggerFactory.getLogger(UdpServerHandler.class);
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
            log.debug("收到广播信息");
	        int readableLength = packet.content().readableBytes();
	        byte[] buf = new byte[readableLength];
	        packet.content().readBytes(buf);
	        Connection.BaseMsg baseMsg = MsgDecoding.bytesToBaseMsg(buf);
	        Connection.Broadcast broadcast = MsgDecoding.decode(baseMsg);

	        //ProcessorCollector.getInstance().processor(ctx,broadcast);
            ClientInfoManager.getInstance().processorProvider.processor(ctx,broadcast);

        }
    }


    public static void main(String[] args) {
        BroadcastReceive broadcastReceive = new BroadcastReceive();
        try {
            broadcastReceive.initServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
